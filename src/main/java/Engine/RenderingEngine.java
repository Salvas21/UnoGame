package Engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class RenderingEngine {

    private final Screen screen;
    private final JPanel panel;
    private BufferedImage bufferedImage;
    private int width = 800;
    private int height = 600;
    private String title = "UNTITLED WINDOW";
    private static RenderingEngine instance;

    public static RenderingEngine getInstance() {
        if (instance == null) {
            instance = new RenderingEngine();
        }
        return instance;
    }

    public Screen getScreen() {
        return screen;
    }

    public void start() {
        screen.start();
    }

    public void stop() {
        screen.end();
    }

    public Buffer getRenderingBuffer() {
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setPaint(Color.BLACK);
        graphics.fillRect(0, 0, width, height);
        graphics.setRenderingHints(getOptimalRenderingHints());
        return new Buffer(graphics);
    }

    public void renderBufferOnScreen() {
        Graphics2D graphics = (Graphics2D) panel.getGraphics();
        graphics.drawImage(bufferedImage, 0, 0, panel);
        Toolkit.getDefaultToolkit().sync();
        graphics.dispose();
    }

    public void addListener(KeyListener listener) {
        panel.addKeyListener(listener);
    }

    public void addListener(MouseListener listener) {
        panel.addMouseListener(listener);
    }

    public void setTitle(String title) {
        this.title = title;
        screen.setTitle(title);
    }

    public void setViewport(int width, int height) {
        this.width = width;
        this.height = height;
        panel.setSize(width, height);
        screen.setSize(width, height);
    }

    public int getViewportWidth() {
        return width;
    }

    public int getViewportHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    private RenderingHints getOptimalRenderingHints() {
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        return rh;
    }

    private RenderingEngine() {
        panel = new JPanel();
        panel.setBackground(Color.BLUE);
        panel.setFocusable(true);
        panel.setDoubleBuffered(true);
        screen = new Screen();
        screen.setPanel(panel);
    }
}