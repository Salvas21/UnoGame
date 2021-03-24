package Engine;

import java.util.concurrent.TimeUnit;

public class RenderingGameTime {

    private static final int FPS_TARGET = 60;

    private static RenderingGameTime instance;
    private static int currentTick;
    private static int tickCount;
    private static long tickTimeDelta;
    private static long gameStartTime;
    private long syncTime;

    private static int fpsCount;
    private static int currentFps;
    private static long nextFpsUpdateTime;

    public static Long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static int getCurrentTick() {
        return (currentTick > 0) ? currentTick : tickCount;
    }

    public static long getElapsedTime() {
        return System.currentTimeMillis() - gameStartTime;
    }

    public static String getElapsedFormattedTime() {
        long time = System.currentTimeMillis() - gameStartTime;

        long hours = TimeUnit.MILLISECONDS.toHours(time);
        time -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static RenderingGameTime getInstance() {
        if (instance == null) {
            instance = new RenderingGameTime();
        }
        return instance;
    }

    protected void synchronize() {
        update();
        try {
            Thread.sleep(getSleepTime());
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        updateSynchronizationTime();
    }

    private long getSleepTime() {
        long targetTime = 1000L / FPS_TARGET;
        long sleep = targetTime -
                (System.currentTimeMillis() - syncTime);
        if (sleep < 0) {
            sleep = 4;
        }
        return sleep;
    }

    private void updateSynchronizationTime() {
        syncTime = System.currentTimeMillis();
    }

    private void update() {
        fpsCount++;
        long currentSecond = TimeUnit.MILLISECONDS.toSeconds(getElapsedTime());
        if (tickTimeDelta != currentSecond) {
            currentTick = tickCount;
            tickCount = 0;
        }
        tickTimeDelta = currentSecond;
    }

    private RenderingGameTime() {
        updateSynchronizationTime();
        gameStartTime = System.currentTimeMillis();
        tickTimeDelta = 0;
        currentTick = 0;
    }

    public static int getCurrentFps() {
        return (currentFps > 0) ? currentFps : fpsCount;
    }

    public void updateFrame() {
        ++fpsCount;
        long currentSecond = TimeUnit.MILLISECONDS.toSeconds(getElapsedTime());
        if (currentSecond >= nextFpsUpdateTime) {
            currentFps = fpsCount;
            fpsCount = 0;
            nextFpsUpdateTime = currentSecond + 1;
        }
    }
}