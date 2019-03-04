package game;

import city.cs.engine.*;
import city.cs.engine.Shape;
import org.jbox2d.common.Vec2;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static game.Game.getWorld;

public class MouseHandler extends MouseInputAdapter {

    private static final Shape bulletShape = new BoxShape(0.125f, 0.125f);

    private static final BodyImage bulletImage = new BodyImage("data/Bullet.png", 0.25f);

    private WorldView view;

    private MainCharacter astronaut;

    private double theta;
    private float distance;
    private Random rand = new Random();

    static ArrayList<String> lines;

    MouseHandler(WorldView view, MainCharacter astronaut) {
        this.view = view;
        this.astronaut = astronaut;
        lines = new ArrayList<>();
    }

    @SuppressWarnings("Duplicates")
    public void mousePressed(MouseEvent e) {
        //Fire a bullet if left click is pressed.
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (astronaut.getAmmo() > 0) {
                Bullet bullet = new Bullet(view.getWorld(), bulletShape, 35);
                bullet.addImage(bulletImage);
                astronaut.setAmmo(astronaut.getAmmo() - 1);

                float xDirection = (float) Math.cos(theta);
                float yDirection = (float) Math.sin(theta);

                bullet.setPosition(new Vec2(astronaut.getPosition().x + xDirection * 1.3f, astronaut.getPosition().y + yDirection * 1.3f));
                bullet.setLinearVelocity(new Vec2((xDirection * distance) * 30, (yDirection * distance) * 30));
            }
        }

        if (getWorld().getState() == STATE.LEVEL_EDITOR) {
            //Creates a new platform when middle click is pressed.
            if (e.getButton() == MouseEvent.BUTTON2) {
                boolean noBody = true;
                List<StaticBody> list = getWorld().getStaticBodies();

                //Checks that the user is not trying to create an object on-top of another one.
                for (StaticBody staticBody : list) {
                    if (staticBody.contains(view.viewToWorld(e.getPoint()))) {
                        noBody = false;
                    }
                }

                if (noBody) {
                    switch (LevelEditorUI.createItem) {
                        case "Platform":
                        case "Crumbling Platform": {
                            int randNum;

                            if (view.viewToWorld(e.getPoint()).y <= -3.3) {
                                randNum = (rand.nextInt(7) + 1);
                            } else {
                                randNum = (rand.nextInt(4) + 8);
                            }

                            Shape shape1 = new BoxShape(1.60f, 0.07f);
                            Platform platform;
                            if (LevelEditorUI.createItem.equals("Platform")) {
                                platform = new Platform(getWorld(), shape1, "Platform");
                                platform.setName("Body" + (lines.size() + 1));
                                lines.add("platform" + randNum + "," + view.viewToWorld(e.getPoint()).x + "," + view.viewToWorld(e.getPoint()).y + "," + "1.60,0.07," + (lines.size() + 1));
                                platform.addImage(new BodyImage("data/Platform/platform" + randNum + ".png", 15));
                            } else {
                                platform = new Platform(getWorld(), shape1, "Crumbling Platform");
                                platform.setName("Body" + (lines.size() + 1));
                                lines.add("crumblingPlatform," + view.viewToWorld(e.getPoint()).x + "," + view.viewToWorld(e.getPoint()).y + "," + "1.60,0.07," + (lines.size() + 1));
                                platform.addImage(new BodyImage("data/crumblingPlatform.png", 3));
                            }
                            platform.setPosition(new Vec2(view.viewToWorld(e.getPoint())));
                            break;
                        }
                        case "Enemy": {
                            PolygonShape shape1 = new PolygonShape(-1.186f, -0.816f, -1.186f, -0.1f, -0.251f, 0.825f, 0.251f, 0.825f, 1.181f, -0.105f, 1.186f, -0.821f, -1.186f, -0.821f);
                            StaticBody enemy = new StaticBody(getWorld(), shape1);
                            enemy.setName("Body" + (lines.size() + 1));
                            lines.add("enemy," + view.viewToWorld(e.getPoint()).x + "," + view.viewToWorld(e.getPoint()).y + ", , ," + (lines.size() + 1));
                            enemy.addImage(new BodyImage("data/enemy.png", 1.65f));
                            enemy.setPosition(new Vec2(view.viewToWorld(e.getPoint())));
                            enemy.setAlwaysOutline(true);
                            enemy.setLineColor(Color.BLACK);
                            break;
                        }
                        case "Health":
                        case "Shield":
                        case "Ammo": {
                            Shape shape1 = new BoxShape(0.5f, 0.5f);
                            ItemPickup item = new ItemPickup(getWorld(), shape1, LevelEditorUI.createItem);
                            item.addImage(new BodyImage("data/ItemPickup/" + LevelEditorUI.createItem + ".png"));
                            item.setPosition(new Vec2(view.viewToWorld(e.getPoint())));
                            item.setName("Body" + (lines.size() + 1));
                            lines.add("pickup" + LevelEditorUI.createItem + "," + view.viewToWorld(e.getPoint()).x + "," + view.viewToWorld(e.getPoint()).y + ",0.5,0.5," + (lines.size() + 1));
                            item.setAlwaysOutline(true);
                            break;
                        }
                    }
                }
            }

            //Destroys body selected by the cursor when right click is pressed.
            if (e.getButton() == MouseEvent.BUTTON3) {
                List<StaticBody> list = getWorld().getStaticBodies();
                for (int counter = 0; counter < list.size(); counter++) {
                    StaticBody element = list.get(counter);
                    if (element.contains(view.viewToWorld(e.getPoint()))) {
                        System.out.println("hey");
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

    public void mouseDragged(MouseEvent e) {
        if (getWorld().getState() == STATE.LEVEL_EDITOR) {
            List<StaticBody> list = getWorld().getStaticBodies();
            for (StaticBody staticBody : list) {
                if (staticBody.contains(view.viewToWorld(e.getPoint()))) {
                    staticBody.setPosition(view.viewToWorld(e.getPoint()));
                    break;
                }
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (getWorld().getState() == STATE.LEVEL_EDITOR) {
            List<StaticBody> list = getWorld().getStaticBodies();
            for (StaticBody element : list) {
                if (element.contains(view.viewToWorld(e.getPoint()))) {
                    for (int i = 0; i < lines.size(); i++) {
                        String elementID = element.getName().substring(4);
                        String[] line = lines.get(i).split(",");

                        if (line[5].equals(elementID)) {
                            line[1] = Float.toString(view.viewToWorld(e.getPoint()).x);
                            line[2] = Float.toString(view.viewToWorld(e.getPoint()).y);
                            String text = line[0] + "," + line[1] + "," + line[2] + "," + line[3] + "," + line[4] + "," + line[5];
                            lines.set(i, text);
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }


    public void mouseMoved(MouseEvent e) {
        calculateTheta(e.getPoint());
    }

    void calculateTheta(Point e) {
        Vec2 mousePoint = view.viewToWorld(e);
        Vec2 armPoint = astronaut.getPosition();

        float deltaX = armPoint.x - mousePoint.x;
        float deltaY = armPoint.y - mousePoint.y;

        distance = (float) Math.hypot(deltaX, deltaY);

        theta = astronaut.getArm().isFlippedHorizontal() ? Math.atan2(deltaY, deltaX) + Math.PI : Math.atan2(deltaY, deltaX) + Math.PI;

        if (astronaut.getArm().isFlippedHorizontal()) {
            astronaut.getArm().setRotation((float) (Math.PI - theta));
        } else {
            astronaut.getArm().setRotation((float) theta);
        }
    }
}
