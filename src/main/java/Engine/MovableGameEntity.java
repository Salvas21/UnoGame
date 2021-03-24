package Engine;

import java.awt.Rectangle;

public abstract class MovableGameEntity extends GameEntity {

    private int speed;
    private Direction direction = Direction.UP;
    private final Collision collision;
    private boolean moved;

    public abstract void update();

    public MovableGameEntity() {
        collision = new Collision(this);
    }

    public boolean hasMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public void stopMovement() {
        moved = false;
    }

    public void move(Direction direction) {
        this.direction = direction;
        int allowedSpeed = collision.getAllowedSpeed(direction);
        x += direction.getVelocityX(allowedSpeed);
        y += direction.getVelocityY(allowedSpeed);
        if (x != 0 && y != 0) {
            moved = true;
        }
    }

    public void moveLeft() {
        move(Direction.LEFT);
    }

    public void moveRight() {
        move(Direction.RIGHT);
    }

    public void moveUp() {
        move(Direction.UP);
    }

    public void moveDown() {
        move(Direction.DOWN);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean collisionBoundIntersectWith(GameEntity other) {
        if (other == null) {
            return false;
        }
        return getCollisionBound().intersects(other.getBounds());
    }

    protected Rectangle getCollisionBound() {
        switch (direction) {
            case UP: return getCollisionUpperBound();
            case DOWN: return getCollisionLowerBound();
            case LEFT: return getCollisionLeftBound();
            case RIGHT: return getCollisionRightBound();
            default: return getBounds();
        }
    }

    private Rectangle getCollisionUpperBound() {
        return new Rectangle(x, y - speed, width, speed);
    }

    private Rectangle getCollisionLowerBound() {
        return new Rectangle(x, y + height, width, speed);
    }

    private Rectangle getCollisionLeftBound() {
        return new Rectangle(x - speed, y, speed, height);
    }

    private Rectangle getCollisionRightBound() {
        return new Rectangle(x + width, y, speed, height);
    }

    public Collision getCollision() {
        return collision;
    }


}
