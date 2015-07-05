package com.gfu.app.visual;

import com.gfu.app.Settings;

import java.awt.*;

public class HallEffect extends AbstractVisual {
    Image backBuffer;
    Graphics backGraphics;

    private double frameScalar;
    private int alphaLevel;

    public HallEffect() {
        new HallEffect(0.9, 16);
    }
    
    public HallEffect(double frameScalar, int alphaLevel) {
        super(0, 0, Color.WHITE, 0, 0);

        this.frameScalar = frameScalar;
        this.alphaLevel = alphaLevel;
    }

    @Override
    public void process() {
//        long radius = 10;
//        double timeRatio = (System.currentTimeMillis() % 2000) / 1000.0;
//        double xCircle = radius * Math.cos(timeRatio * Math.PI);
//        double yCircle = radius * Math.sin(timeRatio * Math.PI);
//
//        Color alphaFilter = new Color(0, 0, 0, 16);
//
//        int scaledWidth = (int) (backBuffer.getWidth(this) * 0.9);
//        int scaledHeight = (int) (backBuffer.getHeight(this) * 0.9);
//
//        // apply the alpha filter onto "old" image
//        backGraphics.setColor(alphaFilter);
//        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);
//
//        // get the scaled version of "old" image
//        backBuffer2 = backBuffer.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);
//        backBuffer2.getHeight(this); // needed because getting the scaled instance doesn't have ImageObserver (to await completion)
//
//        backGraphics.setColor(Color.WHITE);
//        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);
//
//        // draw "old" image onto "new" image
//        int x2 = (int) ((Settings.APPLET_WIDTH - scaledWidth) / 2 + xCircle);
//        int y2 = (int) ((Settings.APPLET_HEIGHT - scaledHeight) / 2 + yCircle);
//        backGraphics.drawImage(backBuffer2, x2, y2, this);
    }

    @Override
    public void process(double[] normFreqSpectrum) {

    }

    @Override
    public void process(double cFrequency, double cAmplitude) {

    }

    @Override
    public void draw(Graphics g) {

    }
}
