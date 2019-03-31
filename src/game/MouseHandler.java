package game;

import city.cs.engine.*;
import city.cs.engine.Shape;
import org.jbox2d.common.Vec2;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Responsible for handling all the mouse events (such as when the player presses a mouse button or moves the mouse within the frame).
 */
public class MouseHandler extends MouseInputAdapter {

    private SuperLevel world;
    private BackgroundPanel view;
    private Game game;

    private MainCharacter player;

    private double theta;
    private float distance;
    private Random rand = new Random();
    private boolean itemDragged = false;
    private StaticBody body;

    //The lines variable ensures that the object and all of its states are kept track of, so that when the user decides the save their game it remembers all the objects that the user created and moved around.
    static ArrayList<String> lines = new ArrayList<>();

    /**
     * Constructor for MouseHandler.
     * @param world Current world.
     * @param view Instance of BackgroundPanel.
     * @param player Current player.
     * @param game Instance of Game.
     */
    public MouseHandler(SuperLevel world, BackgroundPanel view, MainCharacter player, Game game) {
        this.world = world;
        this.view = view;
        this.player = player;
        this.game = game;
    }

    /**
     * Called whenever the player presses a mouse button while the mouse is in the JFrame.
     * @param e Holds all the information relating to the mouse event.
     */
    @SuppressWarnings("Duplicates")
    public void mousePressed(MouseEvent e) {
        //Fire a bullet if left click is pressed.
        if (player.isAcquiredPistol() && world.getState() == STATE.GAME) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (player.getAmmo() > 0) {
                    final Shape bulletShape = new BoxShape(0.125f, 0.125f);
                    final BodyImage bulletImage = new BodyImage("data/Bullet.png", 0.25f);

                    //Create instance of bullet.
                    Bullet bullet = new Bullet(view.getWorld(), bulletShape, 35);
                    bullet.addImage(bulletImage);
                    player.setAmmo(player.getAmmo() - 1);

                    //Find x and y direction the player arm is facing.
                    float xDirection = (float) Math.cos(theta);
                    float yDirection = (float) Math.sin(theta);

                    bullet.setPosition(new Vec2(player.getPosition().x + xDirection * 1.3f, player.getPosition().y + yDirection * 1.3f));
                    //Shoot the bullet in the direction the arm was pointing.
                    bullet.setLinearVelocity(new Vec2((xDirection * distance) * 30, (yDirection * distance) * 30));
                    bullet.addCollisionListener(new CollisionHandler(game));
                }
            }
        }

        if (world.getState() == STATE.LEVEL_EDITOR) {
            //Creates a new platform when middle click is pressed.
            if (e.getButton() == MouseEvent.BUTTON2) {
                LevelEditorUI.isLevelSaved = false;
                boolean bodyClicked = false;
                List<StaticBody> list = world.getStaticBodies();

                //Checks that the user is not trying to create an object on-top of another one.
                for (StaticBody staticBody : list) {
                    if (staticBody.contains(view.viewToWorld(e.getPoint()))) {
                        bodyClicked = true;
                    }
                }

                if (!bodyClicked) {
                    //Create an object at the position of the mouse position depending on what item the user has selected in the drop-down menu.
                    switch (LevelEditorUI.createItem1) {
                        case "Platform":
                        case "Crumbling Platform":
                        case "Exit": {
                            float width = 1.60f, height = 0.10f;
                            int randNum;

                            if (view.viewToWorld(e.getPoint()).y <= -3.3) {
                                randNum = (rand.nextInt(7) + 1);
                            } else {
                                randNum = (rand.nextInt(4) + 8);
                            }

                            Shape shape1 = new BoxShape(width, height);
                            Platform platform;
                            if (LevelEditorUI.createItem1.equals("Platform")) {
                                platform = new Platform(world, shape1, "platform", ("platform" + randNum));
                                platform.setName("Body" + (lines.size() + 1));
                                lines.add("platform" + randNum + "," + view.viewToWorld(e.getPoint()).x + "," + view.viewToWorld(e.getPoint()).y + "," + width + "," + height + "," + (lines.size() + 1));
                                if (LevelEditorUI.createItem2.equals("Mars Surface")) {
                                    platform.addImage(new BodyImage("data/Platform/platform" + randNum + ".png", 15)).setOffset(new Vec2(0, 0.12f));
                                } else {
                                    platform.addImage(new BodyImage("data/Platform2/platform" + randNum + ".png", 15)).setOffset(new Vec2(0, 0.12f));
                                }
                            } else if (LevelEditorUI.createItem1.equals("Crumbling Platform")) {
                                platform = new Platform(world, shape1, "crumblingPlatform", "crumblingPlatform");
                                platform.setName("Body" + (lines.size() + 1));
                                lines.add("crumblingPlatform," + view.viewToWorld(e.getPoint()).x + "," + view.viewToWorld(e.getPoint()).y + "," + "1.60,0.10," + (lines.size() + 1));
                                platform.addImage(new BodyImage("data/crumblingPlatform.png", 3)).setOffset(new Vec2(0, 0.12f));
                            } else {
                                shape1 = new BoxShape(1.30f, 0.60f);
                                platform = new Platform(world, shape1, "exit", "exit");
                                platform.setName("Body" + (lines.size() + 1));
                                lines.add("exit," + view.viewToWorld(e.getPoint()).x + "," + view.viewToWorld(e.getPoint()).y + "," + "1.30,0.60," + (lines.size() + 1));
                                platform.addImage(new BodyImage("data/exit.png", 3));
                            }
                            if (LevelEditorUI.isOutlineEnabled) {
                                platform.setAlwaysOutline(true);
                            }
                            platform.setLineColor(Color.BLUE);
                            platform.setPosition(new Vec2(view.viewToWorld(e.getPoint())));
                            break;
                        }
                        case "Enemy": {
                            PolygonShape shape1 = new PolygonShape(-1.186f, -0.816f, -1.186f, -0.1f, -0.251f, 0.825f, 0.251f, 0.825f, 1.181f, -0.105f, 1.186f, -0.821f, -1.186f, -0.821f);
                            StaticBody enemy = new StaticBody(world, shape1);
                            enemy.setName("Body" + (lines.size() + 1));
                            lines.add("enemy," + view.viewToWorld(e.getPoint()).x + "," + view.viewToWorld(e.getPoint()).y + ",100,35," + (lines.size() + 1));
                            enemy.addImage(new BodyImage("data/enemy.png", 1.65f));
                            enemy.setPosition(new Vec2(view.viewToWorld(e.getPoint())));
                            enemy.setLineColor(Color.BLUE);
                            if (LevelEditorUI.isOutlineEnabled) {
                                enemy.setAlwaysOutline(true);
                            }
                            break;
                        }
                        case "Health":
                        case "Shield":
                        case "Loot Bag":
                        case "Loot Chest":
                        case "Loot Coins":
                        case "Loot Crystal":
                        case "Loot Goblet":
                        case "Loot Treasure Sack":
                        case "Ammo": {
                            Shape shape1 = new BoxShape(0.5f, 0.5f);
                            ItemPickup item = new ItemPickup(world, shape1, LevelEditorUI.createItem1, game);
                            item.addImage(new BodyImage("data/ItemPickup/" + LevelEditorUI.createItem1 + ".png", 1.3f));
                            item.setPosition(new Vec2(view.viewToWorld(e.getPoint())));
                            item.setName("Body" + (lines.size() + 1));
                            lines.add("pickup" + LevelEditorUI.createItem1 + "," + view.viewToWorld(e.getPoint()).x + "," + view.viewToWorld(e.getPoint()).y + ",0.5,0.5," + (lines.size() + 1));
                            break;
                        }
                    }
                }
            }

            //Destroys body selected by the cursor when right click is pressed.
            if (e.getButton() == MouseEvent.BUTTON3) {
                LevelEditorUI.isLevelSaved = false;
                List<StaticBody> list = world.getStaticBodies();
                for (int counter = 0; counter < list.size(); counter++) {
                    StaticBody element = list.get(counter);
                    if (element.contains(view.viewToWorld(e.getPoint()))) {
                        String elementID = element.getName().substring(4);
                        list.get(counter).destroy();
                        list.remove(counter);
                        for (int i = 0; i < lines.size(); i++) {
                            String[] line = lines.get(i).split(",");
                            if (line[5].equals(elementID)) {
                                lines.remove(i);
                                for (int x = i; x < lines.size(); x++) {
                                    if (counter != 0) counter--;
                                    String[] text;
                                    text = lines.get(x).split(",");

                                    text[5] = Integer.toString(Integer.parseInt(text[5]) - 1);
                                    list.get(counter).setName("Body" + text[5]);
                                    lines.set(x, text[0] + "," + text[1] + "," + text[2] + "," + text[3] + "," + text[4] + "," + text[5]);
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Called whenever the player holds down a mouse button and drags while the mouse is in the JFrame.
     * @param e Holds all the information relating to the mouse event.
     */
    public void mouseDragged(MouseEvent e) {
        if (world.getState() == STATE.LEVEL_EDITOR && !itemDragged) {
            LevelEditorUI.isLevelSaved = false;
            List<StaticBody> list = world.getStaticBodies();
            for (StaticBody staticBody : list) {
                if (staticBody.contains(new Vec2(view.viewToWorld(e.getPoint()).x, view.viewToWorld(e.getPoint()).y))) {
                    staticBody.setPosition(view.viewToWorld(e.getPoint()));
                    itemDragged = true;
                    body = staticBody;
                    break;
                }
            }
        } else if (itemDragged) {
            body.setPosition(view.viewToWorld(e.getPoint()));
        }
    }

    /**
     * Called whenever the player releases a mouse button while the mouse is in the JFrame.
     * @param e Holds all the information relating to the mouse event.
     */
    public void mouseReleased(MouseEvent e) {
        if (world.getState() == STATE.LEVEL_EDITOR) {
            List<StaticBody> list = world.getStaticBodies();
            for (StaticBody element : list) {
                //Destroys the object at the mouse position if there is one.
                if (element.contains(view.viewToWorld(e.getPoint())) || (itemDragged && element == body)) {
                    for (int i = 0; i < lines.size(); i++) {
                        String elementID = element.getName().substring(4);
                        String[] line = lines.get(i).split(",");

                        if (line[5].equals(elementID)) {
                            line[1] = Float.toString(view.viewToWorld(e.getPoint()).x);
                            line[2] = Float.toString(view.viewToWorld(e.getPoint()).y);
                            String text = line[0] + "," + line[1] + "," + line[2] + "," + line[3] + "," + line[4] + "," + line[5];
                            lines.set(i, text);
                            itemDragged = false;
                            body = null;
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * Called whenever the player moves the mouse while the mouse is in the JFrame.
     * @param e Holds all the information relating to the mouse event.
     */
    public void mouseMoved(MouseEvent e) {
        calculateTheta(e.getPoint());
    }

    /**
     * Called whenever the player presses a mouse button while the mouse is in the JFrame.
     * @param mousePos The position of the mouse in view coordinates.
     */
    public void calculateTheta(Point mousePos) {
        //Get the mouse position in world coordinates.
        Vec2 mousePoint = view.viewToWorld(mousePos);
        Vec2 armPoint = player.getPosition();

        //Find the x and y distance between the arm and the mouse cursor.
        float deltaX = armPoint.x - mousePoint.x;
        float deltaY = armPoint.y - mousePoint.y;

        //Find the distance between the arm and mouse cursor.
        distance = (float) Math.hypot(deltaX, deltaY);

        //Find the angle to turn the arm based on the x and y distance.
        theta = player.getArm().isFlippedHorizontal() ? Math.atan2(deltaY, deltaX) + Math.PI : Math.atan2(deltaY, deltaX) + Math.PI;

        //Rotate the arm depending on which way the player is facing.
        if (player.getArm().isFlippedHorizontal()) {
            player.getArm().setRotation((float) (Math.PI - theta));
        } else {
            player.getArm().setRotation((float) theta);
        }
    }
}
