package com.gfu.app;

import java.applet.Applet;
import java.awt.*;

public class AppletMain extends Applet implements Runnable {

    Visualizer visualizer;
    AudioFile audioFile;
    FrameRegulator frameRegulator;
    StreamPlayer streamPlayer;

    Thread t, spt;

    @Override
    public void init() {
        setSize(Settings.APPLET_WIDTH, Settings.APPLET_HEIGHT);

        try {
            audioFile = new AudioFile(Settings.AUDIO_FILE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        visualizer = new Visualizer(createImage(getWidth(), getHeight()));
        frameRegulator = new FrameRegulator();
        streamPlayer = new StreamPlayer(audioFile);
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

        while (true) {
            try {
                // do stuff

                frameRegulator.waitForNextFrame();
                repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    @Override
    public void update(Graphics g) {

    }

    @Override
    public void paint(Graphics g) {
        update(g);
    }
}
