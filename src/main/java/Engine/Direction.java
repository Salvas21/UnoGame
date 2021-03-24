package Engine;

public enum Direction {

    LEFT(-1, 0),
    RIGHT(1, 0),
    UP(0, -1),
    DOWN(0, 1);

    private final int xMultiplier;
    private final int yMultiplier;

    Direction(int xMultiplier, int yMultiplier) {
        this.xMultiplier = xMultiplier;
        this.yMultiplier = yMultiplier;
    }

    public int getVelocityX(int speed) {
        return xMultiplier * speed;
    }

    public int getVelocityY(int speed) {
        return yMultiplier * speed;
    }
}
