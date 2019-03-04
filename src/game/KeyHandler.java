package game;

import city.cs.engine.*;

import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class KeyHandler extends JFrame implements KeyListener {

    private MainCharacter astronaut;
    private int previousKey;
    private ActionListener lookAtCursor;

    private StepHandler step;

    private float dashSpeed;
    private StaticBody dustCloud;
    private StaticBody jumpCloud;
    private boolean flip = false;

    KeyHandler(MainCharacter astronaut, ActionListener lookAtCursor, StepHandler step, float dashSpeed, World world) {
        //System.out.println("Listening for keys...");
        this.astronaut = astronaut;
        this.lookAtCursor = lookAtCursor;
        this.step = step;
        dustCloud = new StaticBody(world);
        jumpCloud = new StaticBody(world);

        this.dashSpeed = dashSpeed;
    }

    //This ActionListener will play a dust cloud gif whenever the player dashes while grounded.
    private ActionListener playDustCloud = new ActionListener() {
        int index = 4;

        @Override
        public void actionPerformed(ActionEvent e) {
            dustCloud.removeAllImages();
            if (!flip) {
                new AttachedImage(dustCloud, new BodyImage("data/DustCloud/cloud_" + index + ".png", 9), 1, 0, new Vec2(0, 3));
            } else {
                new AttachedImage(dustCloud, new BodyImage("data/DustCloud/cloud_" + index + ".png", 9), 1, 0, new Vec2(0, 3)).flipHorizontal();
            }
            index++;
            if (index == 59) {
                index = 4;
                timer.stop();
            }
        }
    };

    private Timer timer = new Timer(10, playDustCloud);
    private int index = 0;

    //This ActionListener will play a dust cloud gif whenever the player dashes while grounded.
    private ActionListener playJumpCloud = new ActionListener() {
        Random rand = new Random();

        @Override
        public void actionPerformed(ActionEvent e) {
            jumpCloud.removeAllImages();
            new AttachedImage(jumpCloud, new BodyImage("data/JumpCloud" + (rand.nextInt(3) + 1) + "/JumpCloud_" + index + ".png", 9), 1, 0, new Vec2(0, 1));
            index++;
            if (index == 29) {
                index = 0;
                timer1.stop();
            }
        }
    };

    private Timer timer1 = new Timer(10, playJumpCloud);

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        /*If the dashing cool-down timer is depleted and the user does not have both keys pressed (!astronaut.stopped).*/
        if (astronaut.isCanDash() && !astronaut.isBothKeysPressed()) {
            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                /*stopWalking() is called otherwise the applied force would be overwritten when the step listener in Walker changes the velocity.*/
                astronaut.stopWalking();
                astronaut.changeImages("DashPic.png");
                if (!astronaut.getAstronautImage().isFlippedHorizontal()) {
                    astronaut.applyForce(new Vec2(3000 * dashSpeed, astronaut.getLinearVelocity().y));
                    astronaut.addImage(new BodyImage("data/Dash.gif", 2));
                } else {
                    astronaut.applyForce(new Vec2(-3000 * dashSpeed, astronaut.getLinearVelocity().y));
                    astronaut.addImage(new BodyImage("data/Dash.gif", 2)).flipHorizontal();
                }

                /*If the player is grounded, produce a dust cloud when dashing.*/
                if (!astronaut.isJumping()) {
                    dustCloud.setPosition(astronaut.getPosition());
                    flip = false;
                    timer.start();
                }

                step.setDashing(true);
                astronaut.setCanDash(false);
            }
        }

        if (!step.isDashing()) {
            if (key == KeyEvent.VK_D) {  //Pressing the 'd' key moves the player right.
                astronaut.setMovingDir(1);
                if (!astronaut.isDPressed()) {
                    step.setStartAccelerate(true);
                }
                astronaut.setDPressed(true);
                if (astronaut.getAstronautImage().isFlippedHorizontal()) {  //If player is facing left, flip him to face right.
                    astronaut.getAstronautImage().flipHorizontal();
                    astronaut.getArm().flipHorizontal();
                }

                if (!astronaut.isJumping()) {
                    astronaut.changeImages("WalkAnimation.gif");
                }
                previousKey = key;

            } else if (key == KeyEvent.VK_A) {  //Pressing the 'a' key moves the player left.
                astronaut.setMovingDir(-1);
                if (!astronaut.isAPressed()) {
                    step.setStartAccelerate(true);
                }
                astronaut.setAPressed(true);
                if (!astronaut.getAstronautImage().isFlippedHorizontal()) {  //If player is facing right, flip him to face left.
                    astronaut.getAstronautImage().flipHorizontal();
                    astronaut.getArm().flipHorizontal();
                }

                if (!astronaut.isJumping()) {
                    astronaut.changeImages("WalkAnimation.gif");
                }
                previousKey = key;

            } else if (key == KeyEvent.VK_SPACE && astronaut.isCanJump()) {  //If the player presses SPACE while the jump cool-down timer is depleted.
                if (astronaut.isJumping() && astronaut.getExtraJumps() == 0) {
                    astronaut.setJumpPressedRemember(astronaut.getJumpPressedRememberTime());
                }
                if (astronaut.getExtraJumps() > 0) {
                    astronaut.setLinearVelocity(new Vec2(astronaut.getLinearVelocity().x, 0));
                    astronaut.jump(26);
                    jumpCloud.setPosition(astronaut.getPosition());
                    index = 0;
                    timer1.start();
                }
                astronaut.changeImages("Jump.png");
                previousKey = key;
                astronaut.setJumping(true);
                astronaut.setCanJump(false);
                step.setJump(true);  /*Start the jump cool-down timer in StepHandler.*/
                astronaut.setExtraJumps(astronaut.getExtraJumps() - 1);
            }
        } else {  /*If currently dashing...*/
            if (key == KeyEvent.VK_D) {
                astronaut.setMovingDir(1);
                astronaut.setDPressed(true);
                previousKey = key;
            } else if (key == KeyEvent.VK_A) {
                astronaut.setMovingDir(-1);
                astronaut.setAPressed(true);
                previousKey = key;
            } else if (key == KeyEvent.VK_SPACE) {
                previousKey = key;
                astronaut.setCanJump(true);
            }
        }

        //If both 'a' and 'd' keys are pressed while grounded.
        if (astronaut.isAPressed() && astronaut.isDPressed() && (key == KeyEvent.VK_D || key == KeyEvent.VK_A)) {
            astronaut.stopWalking();
            step.setStartAccelerate(false);
            //Uncomment below to stop player in mid-air when both A and D are pressed.
//            if (astronaut.jumpState) {
//                astronaut.setLinearVelocity(new Vec2(0, astronaut.getLinearVelocity().y));
//            }
            astronaut.setBothKeysPressed(true);
            if (!step.isDashing()) {
                if (!astronaut.isJumping()) {  //If the player is not dashing or currently in the air, then slow the player down to a stop and give it an idle image.
                    astronaut.changeImages("Idle.png");
                    astronaut.setLinearVelocity(new Vec2(astronaut.getLinearVelocity().x / 2, astronaut.getLinearVelocity().y));
                }
                astronaut.getAstronautImage().flipHorizontal();
                astronaut.getArm().flipHorizontal();
            }
        }
        //If the player presses a key while in the air while both 'a' and 'd' keys are not simultaneously pressed.
        if (astronaut.isJumping() && !astronaut.isBothKeysPressed()) {
            if (key == KeyEvent.VK_D) {  //Move the player right.
                astronaut.setMovingDir(1);
                astronaut.setAPressed(false);
                astronaut.setDPressed(true);
                astronaut.stopWalking();
            } else if (key == KeyEvent.VK_A) {  //Move the player left.
                astronaut.setMovingDir(-1);
                astronaut.setAPressed(true);
                astronaut.setDPressed(false);
                astronaut.stopWalking();
            }
        }
        //Have the arm rotate to look at the mouse cursor.
        lookAtCursor.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        //If both keys are pressed and either the 'a' or 'd' key is released.
        if (astronaut.isBothKeysPressed() && (key == KeyEvent.VK_A || key == KeyEvent.VK_D)) {
            if (!step.isDashing()) {
                //Face the character in the opposite direction if true.
                if (key != previousKey) {
                    astronaut.getAstronautImage().flipHorizontal();
                    astronaut.getArm().flipHorizontal();
                }
            }
            astronaut.setBothKeysPressed(false);
            //If 'd' was released, move the player left, otherwise move right.
            if (key == KeyEvent.VK_D) {
                astronaut.setMovingDir(-1);
                astronaut.setDPressed(false);
                previousKey = KeyEvent.VK_A;
                step.setStartAccelerate(true);
            } else {
                astronaut.setMovingDir(1);
                astronaut.setAPressed(false);
                previousKey = KeyEvent.VK_D;
                step.setStartAccelerate(true);
            }
            //Change to a running animation.
            if (!step.isDashing() && !astronaut.isJumping()) {
                astronaut.changeImages("WalkAnimation.gif");
            }
            lookAtCursor.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));

        } else if (key == previousKey && (key == KeyEvent.VK_A || key == KeyEvent.VK_D)) {  //If the player pressed 'a' or 'd' and released it without pressing a different key.
            if (!step.isDashing()) {
                //Half the player's current speed to bring him to a stop earlier.
                if (!astronaut.isJumping()) {
                    astronaut.setLinearVelocity(new Vec2(astronaut.getLinearVelocity().x / 2, astronaut.getLinearVelocity().y));
                }
                //Slowly bring the astronaut to a stop.
                if (key == KeyEvent.VK_D) {
                    astronaut.setDPressed(false);
                } else {
                    astronaut.setAPressed(false);
                }
                step.setStartAccelerate(false);
                astronaut.stopWalking();

            } else {  //If the player is dashing...
                if (key == KeyEvent.VK_D) {
                    astronaut.setDPressed(false);
                } else {
                    astronaut.setAPressed(false);
                }
            }

            if (!astronaut.isJumping() && !step.isDashing()) {
                astronaut.changeImages("Idle.png");
            }
            lookAtCursor.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));

        } else if (previousKey == KeyEvent.VK_SPACE && (key == KeyEvent.VK_D || key == KeyEvent.VK_A)) {  //If the first key released after pressing SPACE is either 'a' or 'd'...
            if (!step.isDashing()) {
                if (key == KeyEvent.VK_D) {
                    astronaut.setDPressed(false);
                } else {
                    astronaut.setAPressed(false);
                }
                astronaut.stopWalking();
                step.setStartAccelerate(false);

                if (!astronaut.isJumping()) {
                    astronaut.changeImages("Idle.png");
                }
            } else {
                if (key == KeyEvent.VK_D) {
                    astronaut.setDPressed(false);
                } else {
                    astronaut.setAPressed(false);
                }
            }

        } else if (key == KeyEvent.VK_SHIFT && !astronaut.isBothKeysPressed()) {  //If the player releases the SHIFT key and does not have both 'a' and 'd' keys pressed.
            if (astronaut.isDPressed()) {
                previousKey = KeyEvent.VK_D;
            } else if (astronaut.isAPressed()) {
                previousKey = KeyEvent.VK_A;
            }
        }
    }
}
