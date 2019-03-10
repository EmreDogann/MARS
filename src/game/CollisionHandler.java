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

public class CollisionHandler implements CollisionListener, SensorListener {

    private ActionListener spawnPlayer;
    private ConcurrentHashMap<Platform, Float[]> platformHashMap = new ConcurrentHashMap<>();
    private HashMap<Platform, Long> timeHashMap = new HashMap<>();
    private HashMap<Platform, Timer> platformTimerHashMap = new HashMap<>();
    private Game game;

    CollisionHandler(Game game) { this.game = game; }

    CollisionHandler(Game game, ActionListener spawnPlayer) {
        this.spawnPlayer = spawnPlayer;
        this.game = game;
    }

    public void collide(CollisionEvent ce) {
        if (ce.getReportingBody() instanceof MainCharacter) {
            MainCharacter astronaut = (MainCharacter) ce.getReportingBody();
            /*If player collides with an asteroid.*/
            if (ce.getOtherBody() instanceof Asteroid) {
                int newHealth = astronaut.getHealth() - (int) (((Asteroid) ce.getOtherBody()).getDamage() * astronaut.getDamageReduction());
                if (newHealth < 0) {
                    astronaut.setHealth(0);
                    game.gameOver();
                } else {
                    astronaut.setHealth(newHealth);
                    astronaut.setArmour(astronaut.getArmour() - 5);
                    if (astronaut.getArmour() < 0) {
                        astronaut.setArmour(0);
                    }
                }
                //System.out.println("Player Hit by Asteroid! Health: " + astronaut.getHealth() + ", Armour: " + astronaut.getArmour());

            } else if (ce.getOtherBody() instanceof Enemy) { /*If player collides with an enemy.*/
                int newHealth = astronaut.getHealth() - (int) (((Enemy) ce.getOtherBody()).getDamage() * astronaut.getDamageReduction());
                if (newHealth < 0) {
                    astronaut.setHealth(0);
                    game.gameOver();
                } else {
                    astronaut.setHealth(newHealth);
                    astronaut.setArmour(astronaut.getArmour() - 10);
                    if (astronaut.getArmour() < 0) {
                        astronaut.setArmour(0);
                    }
                }
                //System.out.println("Player Hit by Enemy! Health: " + astronaut.getHealth() + ", Armour: " + astronaut.getArmour());

            } else if (ce.getOtherBody() instanceof Platform) { /*If player collides with a platform or the ground.*/
                astronaut.changeImages("Idle.png");
                Platform platform = (Platform) ce.getOtherBody();
                if (platform.getType().equals("crumblingPlatform") && !platformHashMap.containsKey(platform)) {
                    astronaut.changeImages("Idle.png");
                    if (game.getWorld().getState() == STATE.GAME) {
                        Float[] nums = {0f, 0f, 0f, 0f, 0f};
                        timer.start();
                        this.platformHashMap.put(platform, nums);
                        timeHashMap.put(platform, System.nanoTime() / 1000000000);
                    }
                } else if (platform.getType().equals("exit")) {
                    if (game.getWorld().getState() != STATE.LEVEL_EDITOR) {
                        Score score = game.getScore();
                        score.computeScore(astronaut.getHealth(), astronaut.getArmour(), astronaut.getAmmo());
                        if (score.getHighScore() < score.getCurrScore() && score.getCurrScore() >= 0) {
                            score.setHighScore(score.getCurrScore());
                        }
                        System.out.println("Final Score: " + game.getScore().getCurrScore());

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

                        if (game.getLevelNum() == 6) {
                            game.gameWin();
                        } else {
                            game.setLevelNum(game.getLevelNum() + 1);
                            game.loadLevel();
                        }
                    }
                }
            }

            if (!(ce.getOtherBody() instanceof Asteroid)) {
                astronaut.setJumping(false);

                /*If the player pressed SPACE after depleting all their extra jumps, then a count-down timer will be set in StepHandler.
                If the player collides with the ground while that count down timer has not reached 0, then it will automatically
                jump the character back into the air. The reason for why this was done is because if the player presses SPACE
                when they think they are touching the ground (whereas in reality they are not), the game will not register this as a jump
                as therefore make the jumping feel less responsive.*/
                if (astronaut.getJumpPressedRemember() > 0) {
                    astronaut.setJumpPressedRemember(0);
                    astronaut.jump(26);
                    astronaut.setExtraJumps(astronaut.getExtraJumpsLimit() - 1);
                    astronaut.setJumping(true);
                    astronaut.changeImages("Jump.png");
                } else {  /*If the count-down timer has finished, then just reset the players extra jumps.*/
                    astronaut.setExtraJumps(astronaut.getExtraJumpsLimit());
                }

                if ((astronaut.isDPressed() || astronaut.isAPressed()) && !astronaut.isBothKeysPressed()) {
                    if (!astronaut.isJumping()) {
                        astronaut.changeImages("WalkAnimation.gif");
                    }
                    if (astronaut.isDPressed()) {
                        astronaut.startWalking(12);
                    } else {
                        astronaut.startWalking(-12);
                    }
                } else {
                    astronaut.stopWalking();
                }
            }
        } else if (ce.getReportingBody() instanceof  Bullet) {
            Bullet bullet = (Bullet) ce.getReportingBody();
            if (ce.getOtherBody() instanceof Enemy) {
                Enemy enemy  = (Enemy) ce.getOtherBody();
                enemy.setHealth(enemy.getHealth()-bullet.getDamage());
                if (enemy.getHealth() <= 0) {
                    enemy.destroy();
                    game.getWorld().getEnemies().remove(enemy);
                }
            } else if (ce.getOtherBody() instanceof MainCharacter) {
                MainCharacter player = (MainCharacter) ce.getOtherBody();
                player.setHealth(player.getHealth() - bullet.getDamage());
                if (player.getHealth() <= 0) {
                    game.gameOver();
                }
            }
            bullet.destroy();
            game.getWorld().getDynamicBodies().remove(bullet);
        }

        if (ce.getReportingBody() instanceof Asteroid) {
            Asteroid asteroid = (Asteroid) ce.getReportingBody();
            asteroid.spawn();
        }

        if (ce.getReportingBody().getName() == "PlayerSpaceship") {
            spawnPlayer.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
        }
    }

    private ActionListener respawnCrumblingPlatform = e -> {
        Platform platform = null;
        SortedSet<Long> keys = new TreeSet<>(timeHashMap.values());
        for (Map.Entry<Platform, Long> entry : timeHashMap.entrySet()) {
            if (Objects.equals(keys.first(), entry.getValue())) {
                platform =  entry.getKey();
                if (platformTimerHashMap.containsKey(platform)) {
                    platformTimerHashMap.get(platform).stop();
                    platformTimerHashMap.remove(platform);
                }
                timeHashMap.remove(platform);
                break;
            }
        }
        Platform newPlatform = new Platform(game.getWorld(), new BoxShape(1.60f, 0.07f), "crumblingPlatform");
        newPlatform.addImage(new BodyImage("data/crumblingPlatform.png", 3)).setOffset(new Vec2(0, 0.12f));
        assert platform != null;
        newPlatform.setPosition(new Vec2(platformHashMap.get(platform)[3], platformHashMap.get(platform)[4]));
        newPlatform.setName(platform.getName());
        newPlatform.setLineColor(Color.BLUE);
        platformHashMap.remove(platform);
    };

    private ActionListener playCrumblingPlatform = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (Platform platform : platformHashMap.keySet()) {
                if (platformHashMap.get(platform)[1] <= 2) {
                    platform.removeAllImages();
                    new AttachedImage(platform, new BodyImage("data/CrumblingPlatform/crumblingPlatform" + Math.round(platformHashMap.get(platform)[0]) + ".png", 3), 1, 0, new Vec2(0, 0));
                    platformHashMap.get(platform)[0]++;

                    if ((platformHashMap.get(platform)[0] == 16) && (platformHashMap.get(platform)[2] == 0)) {
                        platformHashMap.get(platform)[1]++;
                        platformHashMap.get(platform)[0] = 0f;
                    } else if (platformHashMap.get(platform)[0] >= 24) {
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
                    if (platformHashMap.get(platform)[1] >= 2) {
                        platformHashMap.get(platform)[2] = 1f;
                    }
                }
            }
        }
    };
    private Timer timer = new Timer(10, playCrumblingPlatform);

    @Override
    public void beginContact(SensorEvent sensorEvent) {
        if (game.getWorld().getState() != STATE.LEVEL_EDITOR) {
            if (sensorEvent.getContactBody() instanceof MainCharacter) {
                MainCharacter astronaut = (MainCharacter) sensorEvent.getContactBody();
                if (sensorEvent.getSensor().getBody() instanceof ItemPickup) {
                    ItemPickup item = (ItemPickup) sensorEvent.getSensor().getBody();
                    if (item.getType().contains("Loot")) {
                        setStat(game.getScore(), item);
                        item.destroy();
                    } else {
                        if (item.getType().equals("DoubleJump")) {
                            astronaut.setExtraJumpsLimit(2);
                            item.destroy();
                        } else if (item.getType().equals("DashBoots")) {
                            astronaut.setAcquiredBoots(true);
                            item.destroy();
                        } else if (item.getType().equals("Pistol")) {
                            game.getWorld().getView().addMouseMotionListener(game.getWorld().getMouseHandler());
                            game.getWorld().getPlayer().setArmImage("data/ArmPistol.png");
                            item.destroy();
                        } else if (item.getType().equals("Health")) {
                            if (astronaut.getHealth() != 100) {
                                setStat(astronaut, item);
                                item.destroy();
                            }
                        } else if (item.getType().equals("Shield")) {
                            if (astronaut.getArmour() != 25) {
                                setStat(astronaut, item);
                                item.destroy();
                            }
                        } else {
                            setStat(astronaut, item);
                            item.destroy();
                        }
                    }
                } else {
                    if (game.getLevelNum() == 6) {
                        game.getWorld().getView().startShaking(7);
                        game.getWorld().createAsteroids();
                        sensorEvent.getSensor().getBody().destroy();
                    }
                }
            }
        }
    }

    @Override
    public void endContact(SensorEvent sensorEvent) {
    }

    private void setStat(MainCharacter astronaut, ItemPickup item) {
        switch (item.getType()) {
            case "Health":
                astronaut.setHealth(astronaut.getHealth() + item.getRestoreAmount());
                break;
            case "Shield":
                astronaut.setArmour(astronaut.getArmour() + item.getRestoreAmount());
                break;
            case "Ammo":
                astronaut.setAmmo(astronaut.getAmmo() + item.getRestoreAmount());
                break;
        }
        if (astronaut.getHealth() > 100) {
            astronaut.setHealth(100);
        }
        if (astronaut.getArmour() > 25) {
            astronaut.setArmour(25);
        }
    }

    private void setStat(Score score, ItemPickup item) {
        score.increaseScore(item.getRestoreAmount());
    }
}
