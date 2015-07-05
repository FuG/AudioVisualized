package com.gfu.app;

import com.gfu.app.visual.AbstractVisual;
import com.gfu.app.visual.Bubble;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VisualCollection extends ArrayList<AbstractVisual> {
    List<AbstractVisual> visualList;
//    int[] freqBoundaries = { 20, 30, 40, 50, 60, 70, 80, 90, 100,
//                             200, 300, 400, 500, 600, 700, 800, 900, 1000,
//                             2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 15000, 20000 }; // 29 boundaries
    int[] freqBoundaries = { 100, 200, 500, 1000, 2000, 5000, 10000, 20000 }; // 8 boundaries

    public VisualCollection() {
        visualList = new ArrayList<AbstractVisual>();

        // TODO: support multiple AbstractVisual types

        int xDelta = Settings.APPLET_WIDTH / (freqBoundaries.length + 1);
        int x = 0, y = Settings.APPLET_HEIGHT / 2;

        // Add first
        x += xDelta;
        visualList.add(new Bubble(x, y, Color.WHITE, xDelta / 2, 0, freqBoundaries[0]));

        // Add remaining
        for (int boundIndex = 1; boundIndex < freqBoundaries.length; boundIndex++) {
            x += xDelta;

            int freqMin = freqBoundaries[boundIndex-1];
            int freqMax = freqBoundaries[boundIndex];
            visualList.add(new Bubble(x, y, Color.WHITE, xDelta, freqMin, freqMax));
        }
    }

    public void update() {
        for (AbstractVisual v : visualList) {
            v.process();
        }
    }

    public void update(double[] normFreqSpectrum) {
//        AppletMain.print("Updating Bubbles: ");
        for (AbstractVisual v : visualList) {
            v.process(normFreqSpectrum);
        }
//        AppletMain.println();
    }

    public void draw(Graphics g) {
//        AppletMain.print("Drawing Bubbles: ");
        for (AbstractVisual v : visualList) {
            v.draw(g);
        }
//        AppletMain.println();
    }
}
