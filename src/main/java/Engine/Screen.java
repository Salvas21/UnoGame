package Engine;

import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Screen {

    private static GraphicsDevice device;
    private JFrame frame;
    private DisplayMode fullScreenDisplayMode;
    private DisplayMode windowedDisplayMode;
    private boolean isFullScreenMode;
    private Cursor invisibleCursor;

    public Screen() {
        initializeFrame();
        initializeHiddenCursor();
        initializeDevice();
    }

    public void fullScreen() {
        if (device.isDisplayChangeSupported()) {
            device.setDisplayMode(fullScreenDisplayMode);
            frame.setLocationRelativeTo(null);
            if (device.isFullScreenSupported()) {
                device.setFullScreenWindow(frame);
            }
            isFullScreenMode = true;
        }
    }

    public void windowed() {
        if (device.isDisplayChangeSupported()) {
            if (device.isFullScreenSupported()) {
                device.setFullScreenWindow(null);
            }
            device.setDisplayMode(windowedDisplayMode);
            frame.setLocationRelativeTo(null);
            isFullScreenMode = false;
        }
    }

    public void toggleFullScreen() {
        if (isFullScreenMode) {
            windowed();
        } else {
            fullScreen();
        }
    }

    public void hideCursor() {
        frame.setCursor(invisibleCursor);
    }

    public void showCursor() {
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    protected void setPanel(JPanel panel) {
        frame.add(panel);
    }

    protected void setTitle(String title) {
        frame.setTitle(title);
    }

    protected void setSize(int width, int height) {
        boolean frameIsVisible = frame.isVisible();
        if (frameIsVisible) {
            frame.setVisible(false);
        }
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        if (frameIsVisible) {
            frame.setVisible(true);
        }
        fullScreenDisplayMode = findClosestDisplayMode(width, height);
    }

    protected void start() {
        frame.setVisible(true);
    }

    protected void end() {
        frame.setVisible(false);
        frame.dispose();
    }

    private DisplayMode findClosestDisplayMode(int width, int height) {
        DisplayMode displayModes[] = device.getDisplayModes();
        int desiredResolution = width * height;
        int availableResolutions[] = new int[displayModes.length];
        for (int i = 0; i < displayModes.length; ++i) {
            availableResolutions[i] = displayModes[i].getWidth() * displayModes[i].getHeight();
        }
        return displayModes[closestIndexOfValue(desiredResolution, availableResolutions)];
    }

    private int closestIndexOfValue(int value, int[] list) {
        int closestIndex = -1;
        for (int i = 0, min = Integer.MAX_VALUE; i < list.length; ++i) {
            final int difference = Math.abs(list[i] - value);
            if (difference < min) {
                min = difference;
                closestIndex = i;
            }
        }
        return closestIndex;
    }

    private void initializeFrame() {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setUndecorated(true);
        frame.setIgnoreRepaint(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setState(JFrame.NORMAL);
    }

    private void initializeHiddenCursor() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Point hotSpot = new Point(0,0);
        BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
        invisibleCursor = toolkit.createCustomCursor(cursorImage, hotSpot, "InvisibleCursor");
    }

    private void initializeDevice() {
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        windowedDisplayMode = device.getDisplayMode();
        System.out.println(windowedDisplayMode.getWidth() + "x" + windowedDisplayMode.getHeight());
    }
}