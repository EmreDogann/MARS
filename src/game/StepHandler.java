package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import java.util.List;

/**
 * Handles everything that needs to be checked on continuously (such as player movement, updating score time, etc.)
 */
public class StepHandler implements StepListener {

    private MainCharacter player;
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

    /**
     * Constructor for StepHandler.
     * @param view Instance of Background Panel.
     * @param player Current player.
     * @param game Instance of Game.
     */
    public StepHandler(BackgroundPanel view, MainCharacter player, Game game) {
        this.game = game;
        this.score = game.getScore();
        this.view = view;
        this.player = player;
        this.dashDuration = defaultDashDuration;
        this.jumpCooldown = 0.0f;
        if (game.getLevelNum() == 1) {
            findGround();
        }
    }

    //Find the ground object in level 1 in order to deal damage to the player over time if they are touching the ground.
    private void findGround() {
        List<StaticBody> bodies = game.getWorld().getStaticBodies();
        for (Body body : bodies) {
            if (body.getName().equals("Ground")) {
                ground = (StaticBody) body;
                break;
            }
        }
    }

    /**
     * Update the state of whether or not the player is accelerating.
     * @param startAccelerate New boolean state.
     */
    public void setStartAccelerate(boolean startAccelerate) {
        this.startAccelerate = startAccelerate;
    }

    /**
     * @return if the player is jumping.
     */
    public boolean isJump() {
        return jump;
    }

    /**
     * Update the state of whether or not the player is jumping.
     * @param jump New boolean state.
     */
    public void setJump(boolean jump) {
        this.jump = jump;
    }

    /**
     * @return if the player is not dashing.
     */
    public boolean isNotDashing() {
        return !jump;
    }

    /**
     * Set if the player is dashing to true.
     */
    public void setDashing() {
        this.dashing = true;
    }

    /**
     * Called at the start of each step. Used to keep track of the player's jump and dash state, as well as the score and score time.
     * @param stepEvent Information relating to the current step.
     */
    @Override
    public void preStep(StepEvent stepEvent) {
        //Update the current score and time.
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
        //Follow the x position of the player with the camera.
        view.setCentre(new Vec2(player.getPosition().x, 0));
        //Used for parallaxing.
        view.setX(player.getPosition().x);

        //If the player is currently in the air and is not dashing and does not have both keys held down...
        if (player.isJumping() && !dashing && (player.isAPressed() || player.isDPressed())) {
            accelerate(33.6f);
        } else if (startAccelerate && !player.isBothKeysPressed()) { //This is used for whenever the player is not in the air.
            accelerate(10);
        }

        //Once the player reaches a speed of 12 or -12, we stop accelerating him and give it at constant velocity.
        if ((player.getLinearVelocity().x >= 12 || player.getLinearVelocity().x <= -12) && !dashing && !player.isJumping() && startAccelerate) {
            startAccelerate = false;
            player.startWalking(player.getLinearVelocity().x);
        }

        /* Checks if the player is currently dashing. If he is, then the game will count down how long the dash has left
        (dashDuration) until it needs to stop dashing. */
        if (dashing) {
            //If the dash is finished...
            if (dashDuration <= 0) {
                dashDuration = defaultDashDuration;
                if (!player.isBothKeysPressed()) {
                    if (player.isDPressed()) {
                        //Stop the dash and start moving normally.
                        dashEnd(player.getMovingDir());
                        //Flip the player if needed.
                        if (player.getPlayerImage().isFlippedHorizontal()) {
                            player.getPlayerImage().flipHorizontal();
                            player.getArm().flipHorizontal();
                        }
                    } else if (player.isAPressed()) {
                        dashEnd(player.getMovingDir());
                        if (!player.getPlayerImage().isFlippedHorizontal()) {
                            player.getPlayerImage().flipHorizontal();
                            player.getArm().flipHorizontal();
                        }
                    } else { //If the player is not pressing d or a at the end of the dash...
                        //Have the player slide to a halt.
                        if (!player.getPlayerImage().isFlippedHorizontal()) {
                            player.setLinearVelocity(new Vec2(6, 0));
                        } else if (player.getPlayerImage().isFlippedHorizontal()) {
                            player.setLinearVelocity(new Vec2(-6, 0));
                        }
                        if (!player.isJumping()) {
                            player.changeImages("Idle.png");
                        } else {
                            player.changeImages("Jump.png");
                        }
                    }
                } else { //If the player is pressing both a and d at the same time when the dash ends...
                    //Have the player slide to a halt.
                    if (!player.getPlayerImage().isFlippedHorizontal()) {
                        player.setLinearVelocity(new Vec2(9, 0));
                    } else if (player.getPlayerImage().isFlippedHorizontal()) {
                        player.setLinearVelocity(new Vec2(-9, 0));
                    }
                    if (!player.isJumping()) {
                        player.changeImages("Idle.png");
                    } else {
                        player.changeImages("Jump.png");
                    }
                }
                dashing = false;
                player.setDashCooldown(0.15f);
            } else {
                dashDuration -= settings.getTimeStep();
            }
            /* If the player is not currently dashing, then the game will count down how long the player has left until
            he can dash again (dashCooldown). */
        } else {
            player.setDashCooldown(player.getDashCooldown() - settings.getTimeStep());
            if (player.getDashCooldown() <= 0.0 && !player.isJumping()) {
                player.setCanDash(true);
            }
        }

        /* Checks if the player is currently jumping. If he is, then the game will count down how long the jump has left
        (jumpDuration) until it needs to stop dashing. */
        if (jump) {
            jump = false;
            jumpCooldown = 0.01f;
        } else {
            jumpCooldown -= settings.getTimeStep();
            if (jumpCooldown <= 0.0) {
                player.setCanJump(true);
            }
        }
        player.setJumpPressedRemember(player.getJumpPressedRemember() - settings.getTimeStep());
    }

    /**
     * Called at the end of each step. Used to check if the player has died or to deal damage to the player over time.
     * @param stepEvent Information relating to the current step.
     */
    @Override
    public void postStep(StepEvent stepEvent) {
        view.setX(player.getPosition().x);
        //Check if the player has fallen off the level. If so, show death state.
        if (game.getLevelNum() > 1 && game.getWorld().getState() == STATE.GAME) {
            if (player.getPosition().y < -20) {
                game.gameOver();
            }
        } else if (game.getLevelNum() == 1 && game.getWorld().getState() == STATE.GAME) {
            //Only start dealing damage to the player if they are in contact with the ground 1.5 seconds after the player spawns.
            if (score.getCurrTime() > 1.5f) {
                List<Body> bodiesInContact = player.getBodiesInContact();
                if (bodiesInContact.contains(ground)) {
                    groundDamageDealt = computeDamageDealt(groundDamageDealt, 3f);
                }
            }
        }

        List<Body> bodiesInContact = player.getBodiesInContact();
        //deal damage to the player over time as long as they are in contact with an enemy.
        for (Body body : bodiesInContact) {
            if (body instanceof Enemy) {
                enemyDamageDealt = computeDamageDealt(enemyDamageDealt, 0.85f);
            }
        }
    }

    //Deal damage to the player.
    private float computeDamageDealt(float damage, float damageAmount) {
        if (damage > 2) {
            player.setHealth(player.getHealth() - (int) damage);
            damage = 0;
        } else {
            damage += damageAmount;
        }
        if (player.getHealth() <= 0) {
            player.setHealth(0);
            game.gameOver();
        }
        return damage;
    }

    /* This method will determine how to player will respond after the dash has ended.
    It will adjust the player's velocity depending on which keys are being held down by the end of the dash.
    The direction parameter is used to determine which way the player should move. */
    private void dashEnd(int direction) {
        if (player.isJumping()) {
            player.setLinearVelocity(new Vec2(direction, 0));
        }
        if (player.getMovingDir() == direction) {
            player.setLinearVelocity(new Vec2(0, 0));
            player.startWalking(direction * 12);
            if (!player.isJumping()) {
                player.changeImages("WalkAnimation.gif");
            } else {
                player.changeImages("Jump.png");
            }
        }
    }

    /* This method will ensure that when the player presses a key from the them being stationary,
    the character will accelerate at a certain rate until it reaches our desired velocity at which point it
    will keep moving at a constant velocity. */
    private void accelerate(float dampening) {
        float horizontalVelocity = player.getLinearVelocity().x;
        if (player.isJumping() && player.isBothKeysPressed()) {
            horizontalVelocity += -player.getMovingDir() * 3;
        } else {
            horizontalVelocity += player.getMovingDir() * 3;
        }
        float horizontalDampening = 0.32f;
        horizontalVelocity *= Math.pow(1f - horizontalDampening, settings.getTimeStep() * dampening);
        player.setLinearVelocity(new Vec2(horizontalVelocity, player.getLinearVelocity().y));
    }
}
