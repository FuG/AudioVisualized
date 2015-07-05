package com.gfu.app.visual;

import com.gfu.app.AppletMain;
import com.gfu.app.Settings;

import java.awt.*;

public class Bubble extends AbstractVisual {
    int maxDiameter, currDiameter;

    public Bubble(int x, int y, Color color, int maxDiameter, int freqMin, int freqMax) {
        super(x, y, color, freqMin, freqMax);
        this.maxDiameter = maxDiameter;
        currDiameter = 0;
    }

    @Override
    public void process() {
        double shrinkRatio = (System.currentTimeMillis() - lastUpdated) / Settings.SMOOTH_SHRINK_MILLIS;
        currDiameter = (int) (currDiameter * shrinkRatio);
    }

    @Override
    public void process(double[] normFreqSpectrum) {
        double powerRatio = getAvgPowerForFreqRange(normFreqSpectrum); // TODO: move this to Visualizer.java level so it can be reused w/o recompute

        int newDiameter = (int) (maxDiameter * powerRatio);

//        AppletMain.print("[" + (int) (x - 0.5 * currDiameter) + "," + (int) (y - 0.5 * currDiameter) + "] ");
//        AppletMain.print("[" + powerRatio + "," + newDiameter + "] ");
        if (Settings.SMOOTH_VISUALS) {
            if (newDiameter > currDiameter) {
                updateDiameter(newDiameter);
            }
        } else {
            updateDiameter(newDiameter);
        }
    }

    private void updateDiameter(int newDiameter) {
        currDiameter = newDiameter;
        updateLastUpdatedTime();
    }

    private double getAvgPowerForFreqRange(double[] normFreqSpectrum) {
        double powerSum = 0;

        for (int freqIndex = adjFreqMin; freqIndex < adjFreqMax; freqIndex++) {
            powerSum += normFreqSpectrum[freqIndex];
//            AppletMain.print("[Pow: " + powerSum + "] ");
        }

        return powerSum / (adjFreqMax - adjFreqMin);
    }

    @Override
    public void process(final double cFrequency, final double cAmplitude) {

    }

    @Override
    public void draw(Graphics g) {
//        AppletMain.print("[" + currDiameter + "] ");
        g.setColor(color);
        g.fillOval((int) (x - 0.5 * currDiameter), (int) (y - 0.5 * currDiameter),
                (int) (currDiameter), (int) (currDiameter));
    }
}
