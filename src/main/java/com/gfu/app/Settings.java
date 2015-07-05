package com.gfu.app;

public class Settings {
    // Global / Applet
    public static int APPLET_FPS = 60;
    public static int APPLET_WIDTH = 800;
    public static int APPLET_HEIGHT = 600;

    // File
    public static String RESOURCE_DIR = "";
    public static String THE_NEXT_EPISODE_8_BR = "the_next_episode_8.wav";
    public static String THE_NEXT_EPISODE_16_BR = "the_next_episode_16.wav";
    public static String THE_NEXT_EPISODE_24_BR = "the_next_episode_24.wav"; // TODO: need to fix StreamPlayer for 24-bit
    public static String AUDIO_FILE_PATH = RESOURCE_DIR + THE_NEXT_EPISODE_16_BR;
    public static int ENFORCED_SAMPLE_RATE = 44100;

    // Visualizer
    public static int VISUALIZER_FPS = 10;
    public static int FRAMES_BETWEEN_REAL_UPDATE = APPLET_FPS / VISUALIZER_FPS;
    public static int SMOOTH_SHRINK_MILLIS = 2000;
    public static boolean SMOOTH_VISUALS = true;
    
    // Equalizer
    public static boolean EQUALIZER_ENABLED = true; /* leave false most of time */

    // DSP
    public static int INPUT_BUFFER_SIZE = ENFORCED_SAMPLE_RATE / VISUALIZER_FPS; // 44.1 khz / 10 samples per sec.
}
