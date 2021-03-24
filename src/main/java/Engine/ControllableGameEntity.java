package Engine;

public abstract class ControllableGameEntity extends MovableGameEntity {

    protected final MovementController controller;

    public ControllableGameEntity(MovementController controller) {
        this.controller = controller;
    }

    public void moveAccordingToHandler() {
        if (!controller.isMoving()) {
            return;
        }
        if (controller.isDownPressed()) {
            moveDown();
        } else if (controller.isUpPressed()) {
            moveUp();
        } else if (controller.isLeftPressed()) {
            moveLeft();
        } else if (controller.isRightPressed()) {
            moveRight();
        }
    }
}
