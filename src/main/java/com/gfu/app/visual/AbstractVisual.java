package com.gfu.app.visual;

import com.gfu.app.Settings;

import java.awt.*;

public abstract class AbstractVisual {
    public int x, y;
    public Color color;
    public long created, lastUpdated;

    public int freqMin, freqMax;
    public int adjFreqMin, adjFreqMax;

    public AbstractVisual() {

    }

    public AbstractVisual(int x, int y, Color color, int freqMin, int freqMax) {
        this.x = x;
        this.y = y;
        this.color = color;
        created = System.currentTimeMillis();
        lastUpdated = -1;

        this.freqMin = freqMin;
        this.freqMax = freqMax;

        adjFreqMin = freqMin / Settings.VISUALIZER_FPS;
        adjFreqMax = freqMax / Settings.VISUALIZER_FPS;
    }

    public abstract void process();

    public abstract void process(double[] normFreqSpectrum);

    public abstract void process(double cFrequency, double cAmplitude);

    protected void updateLastUpdatedTime() {
        lastUpdated = System.currentTimeMillis();
    }

    public abstract void draw(Graphics g);
}
