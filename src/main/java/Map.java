import Engine.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Map extends GameEntity {
    private static Map instance;
    private BufferedImage map;

    public static Map getInstance() {
        if (instance == null) {
            instance = new Map();
        }
        return instance;
    }

    private Map() {
        loadMap();
        teleport(0, -4000);
    }

    private void loadMap() {
        try {
            map = ImageIO.read(this.getClass().getResource("Map.png"));
        }catch (IOException e) {

        }
    }

    private void loadNewMap() {
        try {
            map = ImageIO.read(this.getClass().getResource("NewMap.png"));
        }catch (IOException e) {

        }
    }

    public void changeMap() {
        loadNewMap();
    }

    @Override
    public void draw(Buffer buffer) {
        buffer.drawImage(map, x, y);
    }
}
