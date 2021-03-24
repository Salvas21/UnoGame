package Engine;

import java.awt.event.KeyEvent;

public class MovementController extends Controller {

    private int upKey = KeyEvent.VK_W;
    private int downKey = KeyEvent.VK_S;
    private int leftKey = KeyEvent.VK_A;
    private int rightKey = KeyEvent.VK_D;

    public MovementController() {
        register();
    }

    public boolean isLeftPressed() {
        return super.isKeyPressed(leftKey);
    }

    public boolean isRightPressed() {
        return super.isKeyPressed(rightKey);
    }

    public boolean isUpPressed() {
        return super.isKeyPressed(upKey);
    }

    public boolean isDownPressed() {
        return super.isKeyPressed(downKey);
    }

    public void setMovementKeys(int upKey, int downKey,
                                int leftKey, int rightKey) {
        this.upKey = upKey;
        this.downKey = downKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        super.resetKeys();
        register();
    }

    public boolean isMoving() {
        return isDownPressed() || isLeftPressed()
                || isRightPressed() || isUpPressed();
    }

    private void register() {
        int[] keys = {upKey, downKey, leftKey, rightKey};
        super.registerKeys(keys);
    }
}
