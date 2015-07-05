package com.gfu.app;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Visualizer {
    public List<VisualCollection> visuals;
    Image backBuffer;
    Graphics backGraphics;

    public Visualizer(Image backBuffer) {
        visuals = new ArrayList<>();

        this.backBuffer = backBuffer;
        backGraphics = backBuffer.getGraphics();
        backGraphics.setColor(Color.BLACK);

        initRenderHints(backGraphics);

        visuals.add(new VisualCollection());
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

    public void update() {
        for (VisualCollection vc : visuals) {
            vc.update();
        }
    }

    public void update(double[] normFreqSpectrum) {
        for (VisualCollection vc : visuals) {
            vc.update(normFreqSpectrum);
        }
    }

    public void draw(Graphics g, ImageObserver observer) {
        resetBackground();

        for (VisualCollection vc : visuals) {
            vc.draw(backGraphics);
        }

        g.drawImage(backBuffer, 0, 0, observer);
    }

    private void resetBackground() {
        backGraphics.setColor(Color.BLACK);
        backGraphics.fillRect(0, 0, Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);
        backGraphics.setColor(Color.WHITE);
        backGraphics.drawLine(0, Settings.APPLET_HEIGHT / 2, Settings.APPLET_WIDTH - 1, Settings.APPLET_HEIGHT / 2);
    }
}
