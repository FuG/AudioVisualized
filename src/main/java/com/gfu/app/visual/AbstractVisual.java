package com.gfu.app.visual;

import java.awt.*;

public abstract class AbstractVisual {
    public int x, y;
    public Color color;
    public long created, lastUpdated;

    public AbstractVisual(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        created = System.currentTimeMillis();
        lastUpdated = -1;
    }

    public abstract void process();

    public abstract void process(double cFrequency, double cAmplitude);
}
