package Engine;

import java.awt.*;

public abstract class GameEntity {

    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public abstract void draw(Buffer buffer);

    public void setDimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void teleport(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean intersectWith(GameEntity other) {
        return getBounds().intersects(other.getBounds());
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
