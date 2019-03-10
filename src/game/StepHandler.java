package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import java.util.List;

public class StepHandler implements StepListener {

    private MainCharacter astronaut;
    private BackgroundPanel view;
    private Game game;
    private SimulationSettings settings = new SimulationSettings(60);

    private float dashDuration;
    private float defaultDashDuration = 0.15f;
    private boolean dashing = false;

    private float jumpCooldown;
    private boolean jump = false;

    private boolean startAccelerate = false;

    private Score score;
    private float scoreAmount = 0;
    private StaticBody ground;

    private float enemyDamageDealt = 0;
    private float groundDamageDealt = 0;

    StepHandler(BackgroundPanel view, MainCharacter astronaut, Game game) {
        this.game = game;
        this.score = game.getScore();
        this.view = view;
        this.astronaut = astronaut;
        this.dashDuration = defaultDashDuration;
        this.jumpCooldown = 0.0f;
        if (game.getLevelNum() == 1) {
            findGround();
        }
    }

    private void findGround() {
        List<StaticBody> bodies = game.getWorld().getStaticBodies();
        for (Body body : bodies) {
            if (body.getName().equals("Ground")) {
                ground = (StaticBody) body;
                break;
            }
        }
    }

    public boolean isStartAccelerate() {
        return startAccelerate;
    }

    void setStartAccelerate(boolean startAccelerate) {
        this.startAccelerate = startAccelerate;
    }

    public boolean isJump() {
        return jump;
    }

    void setJump(boolean jump) {
        this.jump = jump;
    }

    boolean isDashing() {
        return jump;
    }

    void setDashing(boolean dashing) {
        this.dashing = dashing;
    }

    @Override
    public void preStep(StepEvent stepEvent) {
        if (score != null) {
            score.setCurrTime(score.getCurrTime() + settings.getTimeStep());
            if (score.isScoreChanged()) {
                if (scoreAmount < score.getChangeAmount()) {
                    scoreAmount += 5;
                    score.setCurrScore(score.getCurrScore() + 5);
                } else {
                    scoreAmount = 0;
                    score.setChangeAmount(0);
                    score.setScoreChanged(false);
                }
            }
        }
        view.setCentre(new Vec2(astronaut.getPosition().x, 0));
        view.setX(astronaut.getPosition().x);

        //If the player is currently in the air and is not dashing and does not have both keys held down...
        if (astronaut.isJumping() && !dashing && (astronaut.isAPressed() || astronaut.isDPressed())) {
            accelerate(33.6f);
        } else if (startAccelerate && !astronaut.isBothKeysPressed()) { /*This is used for whenever the player is not in the air.*/
            accelerate(10);
//            if (astronaut.isDPressed()) {
//                view.setCentre(new Vec2(view.getCentre().x + 1, 0));
//            } else if (astronaut.isAPressed()) {
//                view.setCentre(new Vec2(view.getCentre().x - 1, 0));
//            }
        }

        //Once the astronaut reaches a speed of 12 or -12, we stop accelerating him and give it at constant velocity.
        if ((astronaut.getLinearVelocity().x >= 12 || astronaut.getLinearVelocity().x <= -12) && !dashing && !astronaut.isJumping() && startAccelerate) {
            startAccelerate = false;
            astronaut.startWalking(astronaut.getLinearVelocity().x);
        }

        /*Checks if the player is currently dashing. If he is, then the game will count down how long the dash has left
        (dashDuration) until it needs to stop dashing.*/
        if (dashing) {
            if (dashDuration <= 0) {
                dashDuration = defaultDashDuration;
                if (!astronaut.isBothKeysPressed()) {
                    if (astronaut.isDPressed()) {
                        dashEnd(astronaut.getMovingDir());
                        if (astronaut.getAstronautImage().isFlippedHorizontal()) {
                            astronaut.getAstronautImage().flipHorizontal();
                            astronaut.getArm().flipHorizontal();
                        }
                    } else if (astronaut.isAPressed()) {
                        dashEnd(astronaut.getMovingDir());
                        if (!astronaut.getAstronautImage().isFlippedHorizontal()) {
                            astronaut.getAstronautImage().flipHorizontal();
                            astronaut.getArm().flipHorizontal();
                        }
                    } else {
                        if (!astronaut.getAstronautImage().isFlippedHorizontal()) {
                            astronaut.setLinearVelocity(new Vec2(6, 0));
                        } else if (astronaut.getAstronautImage().isFlippedHorizontal()) {
                            astronaut.setLinearVelocity(new Vec2(-6, 0));
                        }
                        if (!astronaut.isJumping()) {
                            astronaut.changeImages("Idle.png");
                        } else {
                            astronaut.changeImages("Jump.png");
                        }
                    }
                } else {
                    if (!astronaut.getAstronautImage().isFlippedHorizontal()) {
                        astronaut.setLinearVelocity(new Vec2(12, 0));
                    } else if (astronaut.getAstronautImage().isFlippedHorizontal()) {
                        astronaut.setLinearVelocity(new Vec2(-12, 0));
                    }
                    if (!astronaut.isJumping()) {
                        astronaut.changeImages("Idle.png");
                    } else {
                        astronaut.changeImages("Jump.png");
                    }
                }
                dashing = false;
                astronaut.setDashCooldown(0.15f);
            } else {
                dashDuration -= settings.getTimeStep();
            }
            /*If the player is not currently dashing, then the game will count down how long the player has left until
            he can dash again (dashCooldown).*/
        } else {
            astronaut.setDashCooldown(astronaut.getDashCooldown() - settings.getTimeStep());
            if (astronaut.getDashCooldown() <= 0.0 && !astronaut.isJumping()) {
                astronaut.setCanDash(true);
            }
        }

        /*Checks if the player is currently jumping. If he is, then the game will count down how long the jump has left
        (jumpDuration) until it needs to stop dashing.*/
        if (jump) {
            jump = false;
            jumpCooldown = 0.01f;
        } else {
            jumpCooldown -= settings.getTimeStep();
            if (jumpCooldown <= 0.0) {
                astronaut.setCanJump(true);
            }
        }
        astronaut.setJumpPressedRemember(astronaut.getJumpPressedRemember() - settings.getTimeStep());
    }

    @Override
    public void postStep(StepEvent stepEvent) {
        view.setX(astronaut.getPosition().x);
        if (game.getLevelNum() > 1 && game.getWorld().getState() == STATE.GAME) {
            if (astronaut.getPosition().y < -20) {
                game.gameOver();
            }
        } else if (game.getLevelNum() == 1 && game.getWorld().getState() == STATE.GAME) {
            if (score.getCurrTime() > 1.5f) {
                List<Body> bodiesInContact = astronaut.getBodiesInContact();
                if (bodiesInContact.contains(ground)) {
                    groundDamageDealt = computeDamageDealt(groundDamageDealt, 3f);
                }
            }
        }

        List<Body> bodiesInContact = astronaut.getBodiesInContact();
        for (Body body : bodiesInContact) {
            if (body instanceof Enemy) {
                enemyDamageDealt = computeDamageDealt(enemyDamageDealt, 0.85f);
            }
        }
    }

    private float computeDamageDealt(float damage, float damageAmount) {
        if (damage > 2) {
            astronaut.setHealth(astronaut.getHealth() - (int) damage);
            damage = 0;
        } else {
            damage += damageAmount;
        }
        if (astronaut.getHealth() <= 0) {
            astronaut.setHealth(0);
            game.gameOver();
        }
        return damage;
    }

    /*This method will determine how to player will respond after the dash has ended.
    It will adjust the player's velocity depending on which keys are being held down by the end of the dash.
    The direction parameter is used to determine which way the player should move.*/
    private void dashEnd(int direction) {
        if (astronaut.isJumping()) {
            astronaut.setLinearVelocity(new Vec2(direction, 0));
        }
        if (astronaut.getMovingDir() == direction) {
            astronaut.setLinearVelocity(new Vec2(0, 0));
            astronaut.startWalking(direction * 12);
            if (!astronaut.isJumping()) {
                astronaut.changeImages("WalkAnimation.gif");
            } else {
                astronaut.changeImages("Jump.png");
            }
        }
    }

    /*This method will ensure that when the player presses a key from the them being stationary,
    the character will accelerate at a certain rate until it reaches our desired velocity at which point it
    will keep moving at a constant velocity.*/
    private void accelerate(float dampening) {
        float horizontalVelocity = astronaut.getLinearVelocity().x;
        if (astronaut.isJumping() && astronaut.isBothKeysPressed()) {
            horizontalVelocity += -astronaut.getMovingDir() * 3;
        } else {
            horizontalVelocity += astronaut.getMovingDir() * 3;
        }
        float horizontalDampening = 0.32f;
        horizontalVelocity *= Math.pow(1f - horizontalDampening, settings.getTimeStep() * dampening);
        astronaut.setLinearVelocity(new Vec2(horizontalVelocity, astronaut.getLinearVelocity().y));
    }
}
