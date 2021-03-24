package Engine;

public abstract class RenderingGame implements Runnable{

    private final RenderingEngine renderingEngine;
    private RenderingGameTime gameTime;
    private boolean playing = true;

    protected abstract void initialize();
    protected abstract void update();
    protected abstract void draw(Buffer buffer);
    protected abstract void conclude();
    private Thread renderingThread;

    public RenderingGame() {
        renderingEngine = RenderingEngine.getInstance();
        renderingEngine.setViewport(800, 600);
    }

    public RenderingGame(int viewportWidth, int viewportHeight) {
        renderingEngine = RenderingEngine.getInstance();
        renderingEngine.setViewport(viewportWidth, viewportHeight);
    }

    public void start() {
        renderingThread = new Thread(this);
        initialize();
        renderingThread.start();
        play();
        conclude();
    }

    public void stop() {
        playing = false;
    }

    protected int getViewportWidth() {
        return renderingEngine.getViewportWidth();
    }

    protected int getViewportHeight() {
        return renderingEngine.getViewportHeight();
    }

    protected void setTitle(String title) {
        renderingEngine.setTitle(title);
    }

    protected String getTitle() {
        return renderingEngine.getTitle();
    }

    private void play() {
        gameTime = RenderingGameTime.getInstance();
        while (playing) {
            update();
            gameTime.synchronize();
        }
    }

    public void run() {
        renderingEngine.start();
        while (playing) {
            draw(renderingEngine.getRenderingBuffer());
            renderingEngine.renderBufferOnScreen();
            gameTime.updateFrame();
        }
        renderingEngine.stop();
        renderingThread.stop();
    }
}