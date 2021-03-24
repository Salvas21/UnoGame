import Engine.*;

import java.awt.image.BufferedImage;

public abstract class Camera extends MovableGameEntity {

    // Thanks @ Maxime Desmarais for the help

    private MovementController controller;

    public Camera(MovementController controller) {
        this.controller = controller;
    }

    public abstract void update();

    public abstract void draw(Buffer buffer);

    public void moveAccordingToHandler() {
        if (controller.isMoving()) {
            setMoved(true);
            setNormalDirection();
            int allowedSpeed = getCollision().getAllowedSpeed(getDirection());
            setReversedDirection();
            for (GameEntity entity : CollidableRepository.getInstance()) {
                if (!(entity instanceof Player)) {
                    entity.teleport(entity.getX() + getDirection().getVelocityX(allowedSpeed), entity.getY() + getDirection().getVelocityY(allowedSpeed));
                }
            }
            Map map = Map.getInstance();
            map.teleport(map.getX() + getDirection().getVelocityX(allowedSpeed), map.getY() + getDirection().getVelocityY(allowedSpeed));
            setNormalDirection();
        } else {
            stopMovement();
        }
    }

    private void setNormalDirection() {
        if (controller.isUpPressed()) {
            setDirection(Direction.UP);
        } else if (controller.isDownPressed()) {
            setDirection(Direction.DOWN);
        } else if (controller.isLeftPressed()) {
            setDirection(Direction.LEFT);
        } else if (controller.isRightPressed()) {
            setDirection(Direction.RIGHT);
        }
    }

    private void setReversedDirection() {
        if (controller.isUpPressed()) {
            setDirection(Direction.DOWN);
        } else if (controller.isDownPressed()) {
            setDirection(Direction.UP);
        } else if (controller.isLeftPressed()) {
            setDirection(Direction.RIGHT);
        } else if (controller.isRightPressed()) {
            setDirection(Direction.LEFT);
        }
    }

}
