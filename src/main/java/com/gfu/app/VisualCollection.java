package com.gfu.app;

import com.gfu.app.visual.AbstractVisual;

import java.util.ArrayList;
import java.util.List;

public class VisualCollection extends ArrayList<AbstractVisual> {
    List<AbstractVisual> visualList;

    public VisualCollection() {
        visualList = new ArrayList<AbstractVisual>();
    }
}
