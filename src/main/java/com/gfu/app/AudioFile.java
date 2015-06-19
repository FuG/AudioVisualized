package com.gfu.app;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class AudioFile {
    private static File file;

    private AudioInputStream baseInputStream;

    private AudioFormat baseFormat;

    private byte[] byteBuffer;

    private double[] doubleBuffer;

    public AudioFile(String filepath) throws IOException, UnsupportedAudioFileException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        file = new File(classLoader.getResource(filepath).getFile());

        init();

        loadByteBuffer();
    }

    private void loadByteBuffer() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = new byte[4096];

        int nBytesRead = 0;
        while(nBytesRead != -1) {
            nBytesRead = baseInputStream.read(data, 0, data.length);
            if (nBytesRead != -1) {
                out.write(data, 0, nBytesRead);
            }

        }

        byteBuffer = out.toByteArray();
    }

    private void init() throws IOException, UnsupportedAudioFileException {
        baseInputStream = AudioSystem.getAudioInputStream(file);
        baseInputStream.reset();
        baseFormat = baseInputStream.getFormat();
    }

    public AudioInputStream getBaseInputStream() {
        return baseInputStream;
    }

    public AudioFormat getBaseFormat() {
        return baseFormat;
    }
}
