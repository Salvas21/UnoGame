import Engine.MovementController;

import java.awt.event.KeyEvent;

public class PlayerController extends MovementController {
    private final int ESC = KeyEvent.VK_ESCAPE;
    private final int FIRE = KeyEvent.VK_SPACE;
    private final int SHIFT = KeyEvent.VK_SHIFT;
    private final int RELOAD = KeyEvent.VK_R;
    private final int ACTION = KeyEvent.VK_E;

    private final int ANY = KeyEvent.VK_ALL_CANDIDATES;

    private final int FULLSCREEN = KeyEvent.VK_1;

    public PlayerController() {
        int[] keys = {ESC, FIRE, SHIFT, RELOAD, ACTION};
        super.registerKeys(keys);
    }

    public boolean isFirePressed() {
        return super.isKeyPressed(FIRE);
    }

    public boolean isEscPressed() {
        return super.isKeyPressed(ESC);
    }

    public boolean isQuitPressed() {
        return  super.isKeyPressed(SHIFT) && super.isKeyPressed(ESC);
    }

    public boolean isReloadPressed() {
        return super.isKeyPressed(RELOAD);
    }

    public boolean isActionPressed() {
        return super.isKeyPressed(ACTION);
    }

    public boolean isFullScreenPressed() {
        return super.isKeyPressed(FULLSCREEN);
    }

    public boolean isAnyPressed() {
        return super.isKeyPressed(ANY);
    }



}
