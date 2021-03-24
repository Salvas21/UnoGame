package Engine;

public abstract class Game {

    private final RenderingEngine renderingEngine;
    private GameTime gameTime;
    private boolean playing = true;

    protected abstract void initialize();
    protected abstract void update();
    protected abstract void draw(Buffer buffer);
    protected abstract void conclude();

    public Game() {
        renderingEngine = RenderingEngine.getInstance();
        renderingEngine.setViewport(800, 600);
    }

    public Game(int viewportWidth, int viewportHeight) {
        renderingEngine = RenderingEngine.getInstance();
        renderingEngine.setViewport(viewportWidth, viewportHeight);
    }

    public void start() {
        initialize();
        run();
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

    private void run() {
        renderingEngine.start();
        gameTime = GameTime.getInstance();
        while (playing) {
            update();
            draw(renderingEngine.getRenderingBuffer());
            renderingEngine.renderBufferOnScreen();
            gameTime.synchronize();
        }
        renderingEngine.stop();

    }
}






