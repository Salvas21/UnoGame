import Engine.*;

import java.awt.*;

public class CollisionBlock extends GameEntity {
    private String name;

    public CollisionBlock(int x, int y, String name) {
        this.name = name;
        super.setDimension(64,64);
        super.teleport(x,y - 4000);
        CollidableRepository.getInstance().registerEntity(this);
    }

    public boolean nameIs(String desiredName) {
        return name.equalsIgnoreCase(desiredName);
    }

    @Override
    public void draw(Buffer buffer) {
        buffer.drawRectangle(x,y,width,height, Color.WHITE);
    }
}
