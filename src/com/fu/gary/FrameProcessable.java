package com.fu.gary;

public abstract class FrameProcessable {
    public int x, y;

    public abstract void process();

    public abstract void process(double cFrequency, double cAmplitude);
}
