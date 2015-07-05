package com.gfu.app;

import com.gfu.app.visual.AbstractVisual;

import java.applet.Applet;
import java.awt.*;
import java.util.*;

public class AppletMain extends Applet implements Runnable {

    Random rand = new Random();

    Visualizer visualizer;
    AudioFile audioFile;
    FrameRegulator frameRegulator;
    StreamPlayer streamPlayer;
    DSP dsp;

    Image backBuffer, backBuffer2;
    Graphics backGraphics, backGraphics2;

    Thread t, spt;

    public static void print(String s) {
        System.out.print(s);
    }

    public static void println() {
        System.out.println();
    }

    public static void println(String s) {
        System.out.println(s);
    }

    @Override
    public void init() {
        setSize(Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

//        runSandbox();

        try {
            audioFile = new AudioFile(Settings.AUDIO_FILE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        backBuffer2 = createImage(getWidth(), getHeight());
        backGraphics2 = backBuffer2.getGraphics();
        backGraphics2.setColor(Color.BLACK);

        backBuffer = createImage(getWidth(), getHeight());
        backGraphics = backBuffer.getGraphics();
        backGraphics.setColor(Color.BLACK);
        initRenderHints(backGraphics);
        visualizer = new Visualizer(backBuffer);
        frameRegulator = new FrameRegulator();
        streamPlayer = new StreamPlayer(audioFile);

        dsp = new DSP(audioFile);
//        dsp.applyEQ();
//        double[] doubleBuf = dsp.inverseTransform();
//        System.out.println("done transform!");

//        for (int i = 0; i < 100000; i++) {
//            if (doubleBuf[i] != 0d) {
//                AppletMain.println("[" + i + "] Not 0 : " + doubleBuf[0]);
//            }
////            AppletMain.println(i + ": " + audioFile.doubleBuffer[i] + " / " + doubleBuf[i]);
//        }
        AudioDataMediator mediator = new AudioDataMediator(audioFile);
        audioFile.byteBufferEQ = mediator.outputByteArray;
    }

    private void runSandbox() {
        byte[] bytes = new byte[] { 42 };
        double baseValue = AudioDataMediator.getNormalizedFloat64(bytes, 1, true);

        System.out.println("[" + (int)(baseValue * 255) + "]");

        bytes = new byte[] { -107, 42 };
        baseValue = AudioDataMediator.getNormalizedFloat64(bytes, 2, true);

        System.out.println("[" + (int)(baseValue * 32768) + "]");

        System.exit(1);
    }

    @Override
    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }

        if (spt == null) {
            spt = new Thread(streamPlayer);
            spt.start();
        }
    }

    public void run() {
        frameRegulator.start();
        int i = 0;
        while (true) {
            try {
                long frameStart = System.currentTimeMillis();

//                if (frameRegulator.hasNextUpdate()) {
//                    double[] normalizedFreqSpectrum = dsp.transform(audioFile.nextSample());
//
//                    visualizer.update(normalizedFreqSpectrum);
//                } else {
//                    visualizer.update();
//                }
                long frameEnd = System.currentTimeMillis();

//                AppletMain.println("(" + (i++) + ") Frame Time: " + (frameEnd - frameStart) + "ms");

                frameRegulator.waitForNextFrame();
                repaint();
//                frameRegulator.waitForNextFrame();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    @Override
    public void update(Graphics g) {
//        visualizer.draw(g, this);
//        java.util.List<VisualCollection> vcList = visualizer.visuals;
//
//        backGraphics.setColor(Color.BLACK);
//        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);
//        backGraphics.setColor(Color.WHITE);
//        backGraphics.drawLine(0, Settings.APPLET_HEIGHT / 2, Settings.APPLET_WIDTH - 1, Settings.APPLET_HEIGHT / 2);
//
//        for (VisualCollection vc : vcList) {
//            for (AbstractVisual v : vc) {
//                v.draw(backGraphics);
//            }
//        }
//        int cDiam = rand.nextInt(100);
//        backGraphics.drawOval(Settings.APPLET_WIDTH / 2 - cDiam / 2 , Settings.APPLET_HEIGHT / 2 - cDiam / 2, cDiam, cDiam);

//        hallEffectRevolvingGrayScaleRandomEffects();
//        backBuffer.getHeight(this);
        g.drawImage(backBuffer, 0, 0, this);
        getToolkit().sync();
    }

    private void hallEffectRandomColors() {
        Color alphaFilter = new Color(0, 0, 0, 1);

        int scaledWidth = (int) (backBuffer.getWidth(this) * 0.9);
        int scaledHeight = (int) (backBuffer.getHeight(this) * 0.9);

        // apply the alpha filter onto "old" image
        backGraphics.setColor(alphaFilter);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        // get the scaled version of "old" image
        backBuffer2 = backBuffer.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);
        backBuffer2.getHeight(this); // needed because getting the scaled instance doesn't have ImageObserver (to await completion)

//        Image tmpBuffer = createImage(backBuffer2.getWidth(this), backBuffer2.getHeight(this));
//        Graphics tmpGraphics = tmpBuffer.getGraphics();
//        tmpGraphics.drawImage(backBuffer2, 0, 0, this);

        // fill "new" image with random b/w shade
        int randR = rand.nextInt(256);
        int randG = rand.nextInt(256);
        int randB = rand.nextInt(256);
        backGraphics.setColor(new Color(randR, randG, randB));
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        // draw "old" image onto "new" image
        int x2 = (Settings.APPLET_WIDTH - scaledWidth) / 2;
        int y2 = (Settings.APPLET_HEIGHT - scaledHeight) / 2;
        backGraphics.drawImage(backBuffer2, x2, y2, this);
    }

    private void hallEffectGrayScaleRandomThickLinesSlow() {
        Color alphaFilter = new Color(0, 0, 0, rand.nextInt(2));

        int scaledWidth = (int) (backBuffer.getWidth(this) * 0.98);
        int scaledHeight = (int) (backBuffer.getHeight(this) * 0.98);

        // apply the alpha filter onto "old" image
        backGraphics.setColor(alphaFilter);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);
        backBuffer.getHeight(this);

        // get the scaled version of "old" image
        backBuffer2 = backBuffer.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);
        backBuffer2.getHeight(this); // needed because getting the scaled instance doesn't have ImageObserver (to await completion)

        backGraphics.setColor(Color.WHITE);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        // draw "old" image onto "new" image
        int x2 = (int) ((Settings.APPLET_WIDTH - scaledWidth) / 2);
        int y2 = (int) ((Settings.APPLET_HEIGHT - scaledHeight) / 2);
        backGraphics.drawImage(backBuffer2, x2, y2, this);
        backBuffer.getHeight(this);

        if (rand.nextInt() % 5 == 0) {
            int xRand1 = rand.nextInt(Settings.APPLET_WIDTH);
            int xRand2 = rand.nextInt(Settings.APPLET_WIDTH);
            int yRand1 = rand.nextInt(Settings.APPLET_HEIGHT);
            int yRand2 = rand.nextInt(Settings.APPLET_HEIGHT);
            backGraphics.setColor(Color.BLUE);
            backGraphics.drawLine(xRand1, yRand1, xRand2, yRand2);
            backGraphics.drawLine(xRand1-1, yRand1, xRand2-1, yRand2);
            backGraphics.drawLine(xRand1-2, yRand1, xRand2-2, yRand2);
            backGraphics.drawLine(xRand1-3, yRand1, xRand2-3, yRand2);
            backGraphics.drawLine(xRand1-4, yRand1, xRand2-4, yRand2);
            backGraphics.drawLine(xRand1-5, yRand1, xRand2-5, yRand2);

            backGraphics.drawLine(xRand1+1, yRand1, xRand2+1, yRand2);
            backGraphics.drawLine(xRand1+2, yRand1, xRand2+2, yRand2);
            backGraphics.drawLine(xRand1+3, yRand1, xRand2+3, yRand2);
            backGraphics.drawLine(xRand1+4, yRand1, xRand2+4, yRand2);
            backGraphics.drawLine(xRand1+5, yRand1, xRand2+5, yRand2);
        }
    }

    private void hallEffectGrayScaleRandomEffects() {
        Color alphaFilter = new Color(0, 0, 0, 8);

        int scaledWidth = (int) (backBuffer.getWidth(this) * 0.92);
        int scaledHeight = (int) (backBuffer.getHeight(this) * 0.92);

        // apply the alpha filter onto "old" image
        backGraphics.setColor(alphaFilter);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);
        backBuffer.getHeight(this);

        // get the scaled version of "old" image
        backBuffer2 = backBuffer.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);
        backBuffer2.getHeight(this); // needed because getting the scaled instance doesn't have ImageObserver (to await completion)

        backGraphics.setColor(Color.WHITE);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        // draw "old" image onto "new" image
        int x2 = (int) ((Settings.APPLET_WIDTH - scaledWidth) / 2);
        int y2 = (int) ((Settings.APPLET_HEIGHT - scaledHeight) / 2);
        backGraphics.drawImage(backBuffer2, x2, y2, this);
        backBuffer.getHeight(this);

        if (rand.nextInt() % 5 == 0) {
            int xRand1 = rand.nextInt(Settings.APPLET_WIDTH);
            int xRand2 = rand.nextInt(Settings.APPLET_WIDTH);
            int yRand1 = rand.nextInt(Settings.APPLET_HEIGHT);
            int yRand2 = rand.nextInt(Settings.APPLET_HEIGHT);
            backGraphics.setColor(Color.BLUE);
            backGraphics.drawLine(xRand1, yRand1, xRand2, yRand2);
            backGraphics.drawLine(xRand1-1, yRand1, xRand2-1, yRand2);
            backGraphics.drawLine(xRand1-2, yRand1, xRand2-2, yRand2);
            backGraphics.drawLine(xRand1-3, yRand1, xRand2-3, yRand2);
            backGraphics.drawLine(xRand1-4, yRand1, xRand2-4, yRand2);
            backGraphics.drawLine(xRand1-5, yRand1, xRand2-5, yRand2);

            backGraphics.drawLine(xRand1+1, yRand1, xRand2+1, yRand2);
            backGraphics.drawLine(xRand1+2, yRand1, xRand2+2, yRand2);
            backGraphics.drawLine(xRand1+3, yRand1, xRand2+3, yRand2);
            backGraphics.drawLine(xRand1+4, yRand1, xRand2+4, yRand2);
            backGraphics.drawLine(xRand1+5, yRand1, xRand2+5, yRand2);
        }

        if (rand.nextInt() % 7 == 0) {
            int xRand1 = rand.nextInt(Settings.APPLET_WIDTH);
            int yRand1 = rand.nextInt(Settings.APPLET_HEIGHT);
            int radiusMax = 40;
            int radius = rand.nextInt(radiusMax) + 1;

            backGraphics.setColor(Color.RED);
            backGraphics.fillOval(xRand1 + radiusMax, yRand1 + radiusMax, radius * 2, radius * 2);
        }
    }
    
    private void hallEffectRevolvingGrayScale() {
        long radius = 10;
        double timeRatio = (System.currentTimeMillis() % 2000) / 1000.0;
        double xCircle = radius * Math.cos(timeRatio * Math.PI);
        double yCircle = radius * Math.sin(timeRatio * Math.PI);

        Color alphaFilter = new Color(0, 0, 0, 16);

        int scaledWidth = (int) (backBuffer.getWidth(this) * 0.9);
        int scaledHeight = (int) (backBuffer.getHeight(this) * 0.9);

        // apply the alpha filter onto "old" image
        backGraphics.setColor(alphaFilter);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        // get the scaled version of "old" image
        backBuffer2 = backBuffer.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);
        backBuffer2.getHeight(this); // needed because getting the scaled instance doesn't have ImageObserver (to await completion)

        backGraphics.setColor(Color.WHITE);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        // draw "old" image onto "new" image
        int x2 = (int) ((Settings.APPLET_WIDTH - scaledWidth) / 2 + xCircle);
        int y2 = (int) ((Settings.APPLET_HEIGHT - scaledHeight) / 2 + yCircle);
        backGraphics.drawImage(backBuffer2, x2, y2, this);
    }

    private void hallEffectRevolvingGrayScaleRandomEffects() {
        long radius = 10;
        double timeRatio = (System.currentTimeMillis() % 2000) / 1000.0;
        double xCircle = radius * Math.cos(timeRatio * Math.PI);
        double yCircle = radius * Math.sin(timeRatio * Math.PI);

        Color alphaFilter = new Color(0, 0, 0, 8);

        int scaledWidth = (int) (backBuffer.getWidth(this) * 0.92);
        int scaledHeight = (int) (backBuffer.getHeight(this) * 0.92);

        // apply the alpha filter onto "old" image
        backGraphics.setColor(alphaFilter);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        // get the scaled version of "old" image
        backBuffer2 = backBuffer.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_AREA_AVERAGING);
        backBuffer2.getHeight(this); // needed because getting the scaled instance doesn't have ImageObserver (to await completion)

        backGraphics.setColor(Color.WHITE);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        // draw "old" image onto "new" image
        int x2 = (int) ((Settings.APPLET_WIDTH - scaledWidth) / 2 + xCircle);
        int y2 = (int) ((Settings.APPLET_HEIGHT - scaledHeight) / 2 + yCircle);
        backGraphics.drawImage(backBuffer2, x2, y2, this);

//        if (rand.nextInt() % 5 == 0) {
//            int xRand1 = rand.nextInt(Settings.APPLET_WIDTH);
//            int xRand2 = rand.nextInt(Settings.APPLET_WIDTH);
//            int yRand1 = rand.nextInt(Settings.APPLET_HEIGHT);
//            int yRand2 = rand.nextInt(Settings.APPLET_HEIGHT);
//            backGraphics.setColor(Color.BLUE);
//            backGraphics.drawLine(xRand1, yRand1, xRand2, yRand2);
//            backGraphics.drawLine(xRand1-1, yRand1, xRand2-1, yRand2);
//            backGraphics.drawLine(xRand1-2, yRand1, xRand2-2, yRand2);
//            backGraphics.drawLine(xRand1-3, yRand1, xRand2-3, yRand2);
//            backGraphics.drawLine(xRand1-4, yRand1, xRand2-4, yRand2);
//            backGraphics.drawLine(xRand1-5, yRand1, xRand2-5, yRand2);
//
//            backGraphics.drawLine(xRand1+1, yRand1, xRand2+1, yRand2);
//            backGraphics.drawLine(xRand1+2, yRand1, xRand2+2, yRand2);
//            backGraphics.drawLine(xRand1+3, yRand1, xRand2+3, yRand2);
//            backGraphics.drawLine(xRand1+4, yRand1, xRand2+4, yRand2);
//            backGraphics.drawLine(xRand1+5, yRand1, xRand2+5, yRand2);
//        }
//
//        if (rand.nextInt() % 7 == 0) {
//            int xRand1 = rand.nextInt(Settings.APPLET_WIDTH);
//            int yRand1 = rand.nextInt(Settings.APPLET_HEIGHT);
//            int radiusMax = 40;
//            int radiusOfEffect = rand.nextInt(radiusMax) + 1;
//
//            backGraphics.setColor(Color.RED);
//            backGraphics.fillOval(xRand1 + radiusMax, yRand1 + radiusMax, radiusOfEffect * 2, radiusOfEffect * 2);
//        }

        if (rand.nextInt() % 3 == 0) {
            int size = 4;
            int[] xPoints = new int[size];
            int[] yPoints = new int[size];
            for (int i = 0; i < size; i++) {
                xPoints[i] = rand.nextInt(Settings.APPLET_WIDTH);
                yPoints[i] = rand.nextInt(Settings.APPLET_HEIGHT);
            }
            Polygon poly = new Polygon(xPoints, yPoints, size);

            backGraphics.setColor(Color.BLUE);
            backGraphics.fillPolygon(poly);
        }
    }

    private void hallEffectRevolvingGrayScaleRandomLines() {
        long radius = 10;
        double timeRatio = (System.currentTimeMillis() % 2000) / 1000.0;
        double xCircle = radius * Math.cos(timeRatio * Math.PI);
        double yCircle = radius * Math.sin(timeRatio * Math.PI);

        Color alphaFilter = new Color(0, 0, 0, 16);

        int scaledWidth = (int) (backBuffer.getWidth(this) * 0.9);
        int scaledHeight = (int) (backBuffer.getHeight(this) * 0.9);

        // apply the alpha filter onto "old" image
        backGraphics.setColor(alphaFilter);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        // get the scaled version of "old" image
        backBuffer2 = backBuffer.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);
        backBuffer2.getHeight(this); // needed because getting the scaled instance doesn't have ImageObserver (to await completion)

        backGraphics.setColor(Color.WHITE);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        // draw "old" image onto "new" image
        int x2 = (int) ((Settings.APPLET_WIDTH - scaledWidth) / 2 + xCircle);
        int y2 = (int) ((Settings.APPLET_HEIGHT - scaledHeight) / 2 + yCircle);
        backGraphics.drawImage(backBuffer2, x2, y2, this);

        int xRand1 = rand.nextInt(Settings.APPLET_WIDTH);
        int xRand2 = rand.nextInt(Settings.APPLET_WIDTH);
        int yRand1 = rand.nextInt(Settings.APPLET_HEIGHT);
        int yRand2 = rand.nextInt(Settings.APPLET_HEIGHT);
        backGraphics.drawLine(xRand1, yRand1, xRand2, yRand2);
    }

    private void hallEffectRevolvingGrayScaleLines() {
        // double power
        long radius = 15;
        double timeRatio = (System.currentTimeMillis() % 2000) / 1000.0;
        double xCircle = radius * Math.cos(timeRatio * Math.PI);
        double yCircle = radius * Math.sin(timeRatio * Math.PI);

        Color alphaFilter = new Color(0, 0, 0, 16);

        int scaledWidth = (int) (backBuffer.getWidth(this) * 0.9);
        int scaledHeight = (int) (backBuffer.getHeight(this) * 0.9);

        // apply the alpha filter onto "old" image
        backGraphics.setColor(alphaFilter);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        // get the scaled version of "old" image
        backBuffer2 = backBuffer.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);
        backBuffer2.getHeight(this); // needed because getting the scaled instance doesn't have ImageObserver (to await completion)

        backGraphics.setColor(Color.WHITE);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        // draw "old" image onto "new" image
        int x2 = (int) ((Settings.APPLET_WIDTH - scaledWidth) / 2 + xCircle);
        int y2 = (int) ((Settings.APPLET_HEIGHT - scaledHeight) / 2 + yCircle);
        backGraphics.drawImage(backBuffer2, x2, y2, this);

        // draw lines on final image
        int xLine = (int) (Settings.APPLET_WIDTH / 2 + xCircle);
        int yLine = (int) (Settings.APPLET_HEIGHT / 2 + yCircle);
        backGraphics.setColor(Color.WHITE);
        backGraphics.drawLine(0, 0, xLine, yLine);
        backGraphics.drawLine(Settings.APPLET_WIDTH - 1, 0, xLine, yLine);
        backGraphics.drawLine(0, Settings.APPLET_HEIGHT - 1, xLine, yLine);
        backGraphics.drawLine(Settings.APPLET_WIDTH - 1, Settings.APPLET_HEIGHT - 1, xLine, yLine);
    }

    static double rVal = 0, rDelta = Math.PI;
    static double gVal = 0, gDelta = Math.PI / 2;
    static double bVal = 0, bDelta = Math.PI / 3;
    private void hallEffectRevolvingGradualColors() {
        // double power
        long radius = 10;
        double timeRatio = (System.currentTimeMillis() % 2000) / 1000.0;
        double xCircle = radius * Math.cos(timeRatio * Math.PI);
        double yCircle = radius * Math.sin(timeRatio * Math.PI);

        Color alphaFilter = new Color(0, 0, 0, 16);

        int scaledWidth = (int) (backBuffer.getWidth(this) * 0.9);
        int scaledHeight = (int) (backBuffer.getHeight(this) * 0.9);

        // apply the alpha filter onto "old" image
        backGraphics.setColor(alphaFilter);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        // get the scaled version of "old" image
        backBuffer2 = backBuffer.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);
        backBuffer2.getHeight(this); // needed because getting the scaled instance doesn't have ImageObserver (to await completion)

        double rTmp = rVal + rDelta;
        if (rTmp > 255 || rTmp < 0) {
            rDelta *= -1;
        }
        rVal += rDelta;

        double gTmp = gVal + gDelta;
        if (gTmp > 255 || gTmp < 0) {
            gDelta *= -1;
        }
        gVal += gDelta;

        double bTmp = bVal + bDelta;
        if (bTmp > 255 || bTmp < 0) {
            bDelta *= -1;
        }
        bVal += bDelta;

        backGraphics.setColor(new Color((int)rVal, (int)gVal, (int)bVal));
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        // draw "old" image onto "new" image
        int x2 = (int) ((Settings.APPLET_WIDTH - scaledWidth) / 2 + xCircle);
        int y2 = (int) ((Settings.APPLET_HEIGHT - scaledHeight) / 2 + yCircle);
        backGraphics.drawImage(backBuffer2, x2, y2, this);
    }

    @Override
    public void paint(Graphics g) {
        update(g);
    }

    private void initRenderHints(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        Map<RenderingHints.Key, Object> renderingHintsMap = new HashMap<>();
        renderingHintsMap.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderingHintsMap.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        renderingHintsMap.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        renderingHintsMap.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        renderingHintsMap.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        renderingHintsMap.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2.setRenderingHints(renderingHintsMap);
    }
}
