package com.gfu.app;

public class Settings {
    // Global / Applet
    public static int APPLET_FPS = 60;
    public static int APPLET_WIDTH = 1280;
    public static int APPLET_HEIGHT = 720;

    // File
    public static String RESOURCE_DIR = "";
    public static String THE_NEXT_EPISODE_8_BR = "the_next_episode_8.wav";
    public static String THE_NEXT_EPISODE_16_BR = "the_next_episode_16.wav";
    public static String THE_NEXT_EPISODE_24_BR = "the_next_episode_24.wav"; // TODO: need to fix StreamPlayer for 24-bit
    public static String AUDIO_FILE_PATH = RESOURCE_DIR + THE_NEXT_EPISODE_16_BR;

    // Visualizer
    public static int VISUALIZER_FPS = 10;
    public static int SMOOTH_SHRINK_MILLIS = 2000;
}
