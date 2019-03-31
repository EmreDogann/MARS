package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles all the collisions which occurs with bodies in the game.
 */
public class CollisionHandler implements CollisionListener, SensorListener {

    private ActionListener spawnPlayer;
    //Stores crumbling platform instance with it's states (explained further down).
    private ConcurrentHashMap<Platform, Float[]> platformHashMap = new ConcurrentHashMap<>();
    //Stores crumbling platform and the time it was added at.
    private HashMap<Platform, Long> timeHashMap = new HashMap<>();
    //Stores crumbling platform and its corresponding respawn timer.
    private HashMap<Platform, Timer> platformTimerHashMap = new HashMap<>();
    private Game game;

    /**
     * One constructor for CollisionHandler which is called by level 1.
     * @param game An instance of Game.
     * @param spawnPlayer A reference to the ActionListener spawnPlayer in the LevelOne class.
     */
    CollisionHandler(Game game, ActionListener spawnPlayer) {
        this.spawnPlayer = spawnPlayer;
        this.game = game;
    }

    /**
     * A second constructor for CollisionHandler which is called by every other level.
     * @param game An instance of Game.
     */
    CollisionHandler(Game game) { this.game = game; }

    /**
     * Every collision between bodies with collision listeners (except bodies that are an instance of the ItemPickup class) is handled and filtered through here.
     * @param ce The collision event which contains all the information about the collision (the reporting body, the other body, etc).
     */
    public void collide(CollisionEvent ce) {
        if (ce.getReportingBody() instanceof MainCharacter) {
            MainCharacter player = (MainCharacter) ce.getReportingBody();
            //If player collides with an asteroid.
            if (ce.getOtherBody() instanceof Asteroid) {
                int newHealth = player.getHealth() - (int) (((Asteroid) ce.getOtherBody()).getDamage() * player.getDamageReduction());
                if (newHealth < 0) {
                    player.setHealth(0);
                    game.gameOver();
                } else {
                    player.setHealth(newHealth);
                    player.setArmour(player.getArmour() - 5);
                    if (player.getArmour() < 0) {
                        player.setArmour(0);
                    }
                }
            } else if (ce.getOtherBody() instanceof Enemy) { //If player collides with an enemy.
                int newHealth = player.getHealth() - (int) (((Enemy) ce.getOtherBody()).getDamage() * player.getDamageReduction());
                if (newHealth < 0) {
                    player.setHealth(0);
                    game.gameOver();
                } else {
                    player.setHealth(newHealth);
                    player.setArmour(player.getArmour() - 10);
                    if (player.getArmour() < 0) {
                        player.setArmour(0);
                    }
                }
            } else if (ce.getOtherBody() instanceof Platform) { //If player collides with a platform or the ground.
                player.changeImages("Idle.png");
                Platform platform = (Platform) ce.getOtherBody();
                //If the player collides with a crumbling platform...
                if (platform.getType().equals("crumblingPlatform") && !platformHashMap.containsKey(platform)) {
                    player.changeImages("Idle.png");
                    if (game.getWorld().getState() == STATE.GAME) {
                        //This array holds different information about the current crumbling platform.
                        //The array goes in this order - image index, numbers of loops done, finished (0 for no and 1 for yes), x, y.
                        Float[] nums = {0f, 0f, 0f, 0f, 0f};
                        //Timer to start the crumbling platform animation.
                        timer.start();
                        this.platformHashMap.put(platform, nums);
                        //Adds platform and time it was a added in 10^-18 seconds.
                        timeHashMap.put(platform, System.nanoTime() / 1000000000);
                    }
                } else if (platform.getType().equals("exit")) { //If the player collides with the exit...
                    if (game.getWorld().getState() != STATE.LEVEL_EDITOR) {
                        //Work out the final score.
                        Score score = game.getScore();
                        score.computeScore(player.getHealth(), player.getArmour(), player.getAmmo());
                        if (score.getHighScore() < score.getCurrScore() && score.getCurrScore() >= 0) {
                            score.setHighScore(score.getCurrScore());
                        }
                        System.out.println("Final Score: " + game.getScore().getCurrScore());

                        //Then save the final score to a text file at data/Score.txt
                        BufferedReader reader;
                        String line;
                        String[] text;
                        StringBuilder inputBuffer = new StringBuilder();
                        try {
                            reader = new BufferedReader(new FileReader("data/Score.txt"));
                            while ((line = reader.readLine()) != null) {
                                text = line.split(":");
                                if (text[0].equals(game.getCurrentLevel())) {
                                    String[] textNums = text[1].split(",");
                                    textNums[0] = Float.toString(score.getHighScore());
                                    if (score.getCurrScore() >= 0) {
                                        textNums[1] = Float.toString(score.getCurrScore());
                                    } else {
                                        textNums[1] = "0";
                                    }
                                    textNums[2] = Float.toString(score.getCurrTime());
                                    text[1] = textNums[0] + "," + textNums[1] + "," + textNums[2];
                                }
                                inputBuffer.append(text[0]).append(":").append(text[1]).append("\n");
                            }
                            reader.close();

                            FileOutputStream fileOut = new FileOutputStream("data/Score.txt");
                            fileOut.write(inputBuffer.toString().getBytes());
                            fileOut.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        //If the player collided with the exit in the final level, show win state.
                        if (game.getLevelNum() == 6) {
                            game.gameWin();
                        } else { //Otherwise, progress the level number by 1 to load the next level.
                            game.setLevelNum(game.getLevelNum() + 1);
                            game.loadLevel();
                        }
                    }
                }
            }

            if (!(ce.getOtherBody() instanceof Asteroid)) {
                player.setJumping(false);

                /*If the player pressed SPACE after depleting all their extra jumps, then a count-down timer will be set in StepHandler.
                If the player collides with the ground while that count down timer has not reached 0, then it will automatically
                jump the character back into the air. The reason for why this was done is because if the player presses SPACE
                when they think they are touching the ground (whereas in reality they are not), the game will not register this as a jump
                as therefore make the jumping feel less responsive.*/
                if (player.getJumpPressedRemember() > 0) {
                    player.setJumpPressedRemember(0);
                    player.jump(26);
                    player.setExtraJumps(player.getExtraJumpsLimit() - 1);
                    player.setJumping(true);
                    player.changeImages("Jump.png");
                } else {  /*If the count-down timer has finished, then just reset the players extra jumps.*/
                    player.setExtraJumps(player.getExtraJumpsLimit());
                }

                if ((player.isDPressed() || player.isAPressed()) && !player.isBothKeysPressed()) {
                    if (!player.isJumping()) {
                        player.changeImages("WalkAnimation.gif");
                    }
                    if (player.isDPressed()) {
                        player.startWalking(12);
                    } else {
                        player.startWalking(-12);
                    }
                } else {
                    player.stopWalking();
                }
            }
        } else if (ce.getReportingBody() instanceof  Bullet) { //If the bullet hit an object...
            Bullet bullet = (Bullet) ce.getReportingBody();
            //If the bullet hit an enemy...
            if (ce.getOtherBody() instanceof Enemy) {
                Enemy enemy  = (Enemy) ce.getOtherBody();
                enemy.setHealth(enemy.getHealth()-bullet.getDamage());
                if (enemy.getHealth() <= 0) {
                    enemy.destroy();
                    game.getWorld().getEnemies().remove(enemy);
                }
            } else if (ce.getOtherBody() instanceof MainCharacter) { //If the bullet hit the player...
                MainCharacter player = (MainCharacter) ce.getOtherBody();
                player.setHealth(player.getHealth() - bullet.getDamage());
                if (player.getHealth() <= 0) {
                    game.gameOver();
                }
            }
            bullet.destroy();
            game.getWorld().getDynamicBodies().remove(bullet);
        }

        //If the asteroid collided with anything, respawn it at the top of the screen.
        if (ce.getReportingBody() instanceof Asteroid) {
            Asteroid asteroid = (Asteroid) ce.getReportingBody();
            asteroid.spawn();
        }

        //If the spaceship hit the ground in level 1, spawn the player.
        if (ce.getReportingBody().getName() == "PlayerSpaceship") {
            spawnPlayer.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
        }
    }

    //Called to respawn the crumbling platform via a timer.
    private ActionListener respawnCrumblingPlatform = e -> {
        Platform platform = null;
        SortedSet<Long> values = new TreeSet<>(timeHashMap.values());
        for (Map.Entry<Platform, Long> entry : timeHashMap.entrySet()) {
            //Find the first crumbling platform that the player landed on and remove it from the hash map.
            if (Objects.equals(values.first(), entry.getValue())) {
                platform =  entry.getKey();
                if (platformTimerHashMap.containsKey(platform)) {
                    //Stop the current crumbling platform's respawnCrumblingPlatform() timer so it doesn't keep respawning.
                    platformTimerHashMap.get(platform).stop();
                    platformTimerHashMap.remove(platform);
                }
                timeHashMap.remove(platform);
                break;
            }
        }
        //Create a new instance of the platform that was found at the same position.
        Platform newPlatform = new Platform(game.getWorld(), new BoxShape(1.60f, 0.07f), "crumblingPlatform", "crumblingPlatform");
        newPlatform.addImage(new BodyImage("data/crumblingPlatform.png", 3)).setOffset(new Vec2(0, 0.12f));
        assert platform != null;
        newPlatform.setPosition(new Vec2(platformHashMap.get(platform)[3], platformHashMap.get(platform)[4]));
        newPlatform.setName(platform.getName());
        newPlatform.setLineColor(Color.BLUE);
        platformHashMap.remove(platform);
    };

    private ActionListener playCrumblingPlatform = new ActionListener() {
        @Override
        //Used to play the crumbling platform animation.
        public void actionPerformed(ActionEvent e) {
            //Play the animation for every crumbling platform in platformHashMap.
            for (Platform platform : platformHashMap.keySet()) {
                if (platformHashMap.get(platform)[1] <= 2) {
                    platform.removeAllImages();
                    new AttachedImage(platform, new BodyImage("data/CrumblingPlatform/crumblingPlatform" + Math.round(platformHashMap.get(platform)[0]) + ".png", 3), 1, 0, new Vec2(0, 0));
                    platformHashMap.get(platform)[0]++;

                    //If the platform has reached the end of the images, loop back to the start again and update it's loop number by 1.
                    if ((platformHashMap.get(platform)[0] == 16) && (platformHashMap.get(platform)[2] == 0)) {
                        platformHashMap.get(platform)[1]++;
                        platformHashMap.get(platform)[0] = 0f;
                    } else if (platformHashMap.get(platform)[0] >= 24) { //When the platform reaches the end of its animation, stop its timer and start its respawn timer.
                        platformHashMap.get(platform)[1]++;
                        if (platformHashMap.size() == 1) {
                            timer.stop();
                        }
                        Timer timer1 = new Timer(2500, respawnCrumblingPlatform);
                        platformTimerHashMap.put(platform, timer1);
                        platformTimerHashMap.get(platform).start();

                        platformHashMap.get(platform)[3] = platform.getPosition().x;
                        platformHashMap.get(platform)[4] = platform.getPosition().y;
                        platform.destroy();
                    }

                    //If the platform 2 or more times, set its finished state to 1.
                    if (platformHashMap.get(platform)[1] >= 2) {
                        platformHashMap.get(platform)[2] = 1f;
                    }
                }
            }
        }
    };
    private Timer timer = new Timer(10, playCrumblingPlatform);

    /**
     * Handles all the sensor collisions from bodies with sensors attached.
     * @param sensorEvent The sensor event which contains all the information relating to the sensor being triggered.
     */
    @Override
    public void beginContact(SensorEvent sensorEvent) {
        if (game.getWorld().getState() != STATE.LEVEL_EDITOR) {
            //If the sensor came into contact with the player.
            if (sensorEvent.getContactBody() instanceof MainCharacter) {
                MainCharacter player = (MainCharacter) sensorEvent.getContactBody();
                //If the sensor is attached to an object of type ItemPickup.
                if (sensorEvent.getSensor().getBody() instanceof ItemPickup) {
                    ItemPickup item = (ItemPickup) sensorEvent.getSensor().getBody();
                    //If the item is a gold pickup then increase the score.
                    if (item.getType().contains("Loot")) {
                        setStat(game.getScore(), item);
                        item.destroy();
                    } else {
                        //Otherwise check what type the item is and perform the corresponding actions.
                        switch (item.getType()) {
                            case "DoubleJump":
                                player.setExtraJumpsLimit(2);
                                item.destroy();
                                break;
                            case "DashBoots":
                                player.setAcquiredBoots(true);
                                item.destroy();
                                break;
                            case "Pistol":
                                player.setAcquiredPistol(true);
                                game.getWorld().getView().addMouseMotionListener(game.getWorld().getMouseHandler());
                                game.getWorld().getPlayer().setArmImage("data/ArmPistol.png");
                                item.destroy();
                                break;
                            case "Health":
                                if (player.getHealth() != 100) {
                                    setStat(player, item);
                                    item.destroy();
                                }
                                break;
                            case "Shield":
                                if (player.getArmour() != 25) {
                                    setStat(player, item);
                                    item.destroy();
                                }
                                break;
                            default:
                                setStat(player, item);
                                item.destroy();
                                break;
                        }
                    }
                } else { //If the sensor is not attached to an object of type ItemPickup...
                    //If the level is 6, then start shaking the screen.
                    if (game.getLevelNum() == 6) {
                        game.getWorld().getView().startShaking(7);
                        game.getWorld().createAsteroids();
                        sensorEvent.getSensor().getBody().destroy();
                    }
                }
            }
        }
    }

    /**
     * Not used. Included because of the interface.
     */
    @Override
    public void endContact(SensorEvent sensorEvent) {
    }

    private void setStat(MainCharacter player, ItemPickup item) {
        switch (item.getType()) {
            case "Health":
                player.setHealth(player.getHealth() + item.getItemValue());
                break;
            case "Shield":
                player.setArmour(player.getArmour() + item.getItemValue());
                break;
            case "Ammo":
                player.setAmmo(player.getAmmo() + item.getItemValue());
                break;
        }

        //Ensure the health bar does not go over 100.
        if (player.getHealth() > 100) {
            player.setHealth(100);
        }

        //Ensure the armour bar does not go over 25.
        if (player.getArmour() > 25) {
            player.setArmour(25);
        }
    }

    private void setStat(Score score, ItemPickup item) {
        score.increaseScore(item.getItemValue());
    }
}
