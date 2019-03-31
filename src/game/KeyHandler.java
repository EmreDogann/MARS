package game;

import city.cs.engine.*;

import org.jbox2d.common.Vec2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

/**
 * Responsible for handling all the keystroke events where the user presses a key while the game is running.
 */
public class KeyHandler extends JFrame implements KeyListener {

    private MainCharacter player;
    private int previousKey;
    private ActionListener lookAtCursor;

    private StepHandler step;

    private StaticBody dustCloud;
    private StaticBody jumpCloud;
    private boolean flip = false;

    private Game game;

    /**
     * Constructor for KeyHandler.
     * @param player Instance of the current player.
     * @param lookAtCursor Reference to the lookAtCursor ActionListener in SuperLevel.
     * @param step Instance of StepHandler.
     * @param world Current world.
     * @param game Instance of Game.
     */
    public KeyHandler(MainCharacter player, ActionListener lookAtCursor, StepHandler step, World world, Game game) {
        this.player = player;
        this.lookAtCursor = lookAtCursor;
        this.step = step;
        this.game = game;
        dustCloud = new StaticBody(world);
        jumpCloud = new StaticBody(world);

    }

    //This ActionListener will play a dust cloud PNGs (in data/DustCloud) whenever the player dashes while grounded.
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

    //This ActionListener will play a the jump cloud PNGs (in data/JumpCloud1-3) whenever the player pressed the space bar.
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

    /**
     * Not used. Included because of interface.
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Called whenever a key is pressed by the user.
     * @param e The key event which holds information about the key pressed.
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (game.getLevelNum() > 4 || player.isAcquiredBoots()) {
            // If the dashing cool-down timer is depleted and the user does not have both keys pressed (!player.stopped).
            if (player.isCanDash() && !player.isBothKeysPressed()) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    // stopWalking() is called otherwise the applied force would be overwritten when the step listener in Walker changes the velocity.
                    player.stopWalking();
                    player.changeImages("DashPic.png");
                    if (!player.getPlayerImage().isFlippedHorizontal()) {
                        player.applyForce(new Vec2(3000 * player.getDashSpeed(), player.getLinearVelocity().y));
                        player.addImage(new BodyImage("data/Dash.gif", 2));
                    } else {
                        player.applyForce(new Vec2(-3000 * player.getDashSpeed(), player.getLinearVelocity().y));
                        player.addImage(new BodyImage("data/Dash.gif", 2)).flipHorizontal();
                    }

                    if (game.getLevelNum() == 1) {
                        // If the player is grounded, produce a dust cloud when dashing.
                        if (!player.isJumping()) {
                            dustCloud.setPosition(player.getPosition());
                            flip = false;
                            timer.start();
                        }
                    }

                    step.setDashing();
                    player.setCanDash(false);
                }
            }
        }

        if (step.isNotDashing()) {
            if (key == KeyEvent.VK_D) {  //Pressing the 'd' key moves the player right.
                player.setMovingDir(1);
                if (!player.isDPressed()) {
                    step.setStartAccelerate(true);
                }
                player.setDPressed(true);
                if (player.getPlayerImage().isFlippedHorizontal()) {  //If player is facing left, flip him to face right.
                    player.getPlayerImage().flipHorizontal();
                    player.getArm().flipHorizontal();
                }

                if (!player.isJumping()) {
                    player.changeImages("WalkAnimation.gif");
                }
                previousKey = key;

            } else if (key == KeyEvent.VK_A) {  //Pressing the 'a' key moves the player left.
                player.setMovingDir(-1);
                if (!player.isAPressed()) {
                    step.setStartAccelerate(true);
                }
                player.setAPressed(true);
                if (!player.getPlayerImage().isFlippedHorizontal()) {  //If player is facing right, flip him to face left.
                    player.getPlayerImage().flipHorizontal();
                    player.getArm().flipHorizontal();
                }

                if (!player.isJumping()) {
                    player.changeImages("WalkAnimation.gif");
                }
                previousKey = key;

            } else if (key == KeyEvent.VK_SPACE && player.isCanJump()) {  //If the player presses SPACE while the jump cool-down timer is depleted.
                if (player.isJumping() && player.getExtraJumps() == 0) {
                    player.setJumpPressedRemember(player.getJumpPressedRememberTime());
                }
                if (player.getExtraJumps() > 0) {
                    player.setLinearVelocity(new Vec2(player.getLinearVelocity().x, 0));
                    player.jump(26);
                    jumpCloud.setPosition(player.getPosition());
                    index = 0;
                    timer1.start();
                }
                player.changeImages("Jump.png");
                previousKey = key;
                player.setJumping(true);
                player.setCanJump(false);
                step.setJump(true);  // Start the jump cool-down timer in StepHandler.
                player.setExtraJumps(player.getExtraJumps() - 1);
            }
        } else {  // If currently dashing...
            if (key == KeyEvent.VK_D) {
                player.setMovingDir(1);
                player.setDPressed(true);
                previousKey = key;
            } else if (key == KeyEvent.VK_A) {
                player.setMovingDir(-1);
                player.setAPressed(true);
                previousKey = key;
            } else if (key == KeyEvent.VK_SPACE) {
                previousKey = key;
                player.setCanJump(true);
            }
        }

        //If both 'a' and 'd' keys are pressed while grounded.
        if (player.isAPressed() && player.isDPressed() && (key == KeyEvent.VK_D || key == KeyEvent.VK_A)) {
            player.stopWalking();
            step.setStartAccelerate(false);
            //Uncomment below to stop player in mid-air when both A and D are pressed.
//            if (player.jumpState) {
//                player.setLinearVelocity(new Vec2(0, player.getLinearVelocity().y));
//            }
            player.setBothKeysPressed(true);
            if (step.isNotDashing()) {
                if (!player.isJumping()) {  //If the player is not dashing or currently in the air, then slow the player down to a stop and give it an idle image.
                    player.changeImages("Idle.png");
                    player.setLinearVelocity(new Vec2(player.getLinearVelocity().x / 2, player.getLinearVelocity().y));
                }
                player.getPlayerImage().flipHorizontal();
                player.getArm().flipHorizontal();
            }
        }
        //If the player presses a key while in the air while both 'a' and 'd' keys are not simultaneously pressed.
        if (player.isJumping() && !player.isBothKeysPressed()) {
            if (key == KeyEvent.VK_D) {  //Move the player right.
                player.setMovingDir(1);
                player.setAPressed(false);
                player.setDPressed(true);
                player.stopWalking();
            } else if (key == KeyEvent.VK_A) {  //Move the player left.
                player.setMovingDir(-1);
                player.setAPressed(true);
                player.setDPressed(false);
                player.stopWalking();
            }
        }
        //Have the arm rotate to look at the mouse cursor.
        lookAtCursor.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
    }

    /**
     * Called whenever a key is released by the user.
     * @param e The key event which holds information about the key pressed.
     */
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        //If both keys are pressed and either the 'a' or 'd' key is released.
        if (player.isBothKeysPressed() && (key == KeyEvent.VK_A || key == KeyEvent.VK_D)) {
            if (step.isNotDashing()) {
                //Face the character in the opposite direction if true.
                if (key != previousKey) {
                    player.getPlayerImage().flipHorizontal();
                    player.getArm().flipHorizontal();
                }
            }
            player.setBothKeysPressed(false);
            //If 'd' was released, move the player left, otherwise move right.
            if (key == KeyEvent.VK_D) {
                player.setMovingDir(-1);
                player.setDPressed(false);
                previousKey = KeyEvent.VK_A;
                step.setStartAccelerate(true);
            } else {
                player.setMovingDir(1);
                player.setAPressed(false);
                previousKey = KeyEvent.VK_D;
                step.setStartAccelerate(true);
            }
            //Change to a running animation.
            if (step.isNotDashing() && !player.isJumping()) {
                player.changeImages("WalkAnimation.gif");
            }
            lookAtCursor.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));

        } else if (key == previousKey && (key == KeyEvent.VK_A || key == KeyEvent.VK_D)) {  //If the player pressed 'a' or 'd' and released it without pressing a different key.
            if (step.isNotDashing()) {
                //Half the player's current speed to bring him to a stop earlier.
                if (!player.isJumping()) {
                    player.setLinearVelocity(new Vec2(player.getLinearVelocity().x / 2, player.getLinearVelocity().y));
                }
                //Slowly bring the player to a stop.
                if (key == KeyEvent.VK_D) {
                    player.setDPressed(false);
                } else {
                    player.setAPressed(false);
                }
                step.setStartAccelerate(false);
                player.stopWalking();

            } else {  //If the player is dashing...
                if (key == KeyEvent.VK_D) {
                    player.setDPressed(false);
                } else {
                    player.setAPressed(false);
                }
            }

            if (!player.isJumping() && step.isNotDashing()) {
                player.changeImages("Idle.png");
            }
            lookAtCursor.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));

        } else if (previousKey == KeyEvent.VK_SPACE && (key == KeyEvent.VK_D || key == KeyEvent.VK_A)) {  //If the first key released after pressing SPACE is either 'a' or 'd'...
            if (step.isNotDashing()) {
                if (key == KeyEvent.VK_D) {
                    player.setDPressed(false);
                } else {
                    player.setAPressed(false);
                }
                player.stopWalking();
                step.setStartAccelerate(false);

                if (!player.isJumping()) {
                    player.changeImages("Idle.png");
                }
            } else {
                if (key == KeyEvent.VK_D) {
                    player.setDPressed(false);
                } else {
                    player.setAPressed(false);
                }
            }

        } else if (key == KeyEvent.VK_SHIFT && !player.isBothKeysPressed()) {  //If the player releases the SHIFT key and does not have both 'a' and 'd' keys pressed.
            if (player.isDPressed()) {
                previousKey = KeyEvent.VK_D;
            } else if (player.isAPressed()) {
                previousKey = KeyEvent.VK_A;
            }
        }
    }
}
