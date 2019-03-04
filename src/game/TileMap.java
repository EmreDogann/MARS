package game;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class TileMap {

    private Image[][] tile;
    private LinkedList<Object> sprite;
    private String fileName = "data/level1OLD.txt";

    private TileMap(int width, int height) {
        tile = new Image[width][height];
        sprite = new LinkedList<>();
    }

    public int getMapWidth() { return tile.length; }
    public int getMapHeight() { return tile[0].length; }
    public Image getTile(int x, int y) { return tile[x][y]; }

    private void setTile(int x, int y, Image tile) { this.tile[x][y] = tile; }
    public void addSprite(Object sprite) { this.sprite.add(sprite); }
    public void removeSprite(Object sprite) { this.sprite.remove(sprite); }

    public TileMap loadMap(String fileName, ArrayList<Image> element) throws IOException {

        ArrayList<String> lines = new ArrayList<>();
        int width = 0;
        int height;

        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        boolean flag = false;
        while (!flag) {
            String line = reader.readLine();
            if (line == null) {
                reader.close();
                flag = true;
            }

            assert line != null;
            if (!line.startsWith("/")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        height = lines.size();
        TileMap map = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                int tiles = ch - 'A';
                if (tiles >= 0 && tiles < element.size()) {
                    map.setTile(x, y, element.get(tiles));
                }
            }
        }
        return map;
    }

}
