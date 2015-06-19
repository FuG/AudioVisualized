package com.gfu.app;

public class FrameRegulator {

    private int desiredFPS;
    private double millisBetweenFrames;
    private long frameStartMillis;

    public FrameRegulator() {
        desiredFPS = 60;
        millisBetweenFrames = 1.0 / desiredFPS;
    }

    public FrameRegulator(int fps) {
        desiredFPS = fps;
        millisBetweenFrames = 1.0 / desiredFPS;
    }

    public void start() {
        frameStartMillis = System.currentTimeMillis();
    }

    public void waitForNextFrame() throws InterruptedException {
        frameStartMillis += millisBetweenFrames;
        long timeDelta = (long) (millisBetweenFrames - (System.currentTimeMillis() - frameStartMillis));

        if (timeDelta > 0) {
            Thread.sleep(timeDelta);
        }
    }
}
