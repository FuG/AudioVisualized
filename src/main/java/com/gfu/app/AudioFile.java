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

    private AudioFormat baseFormat;

    private byte[] byteBuffer;

    private double[] doubleBuffer;

    public AudioFile(String filepath) throws IOException, UnsupportedAudioFileException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        file = new File(classLoader.getResource(filepath).getFile());

        init();
        loadByteBuffer();
        loadDoubleBuffer();
    }

    private void init() throws IOException, UnsupportedAudioFileException {
        baseFormat = AudioSystem.getAudioInputStream(file).getFormat();
    }

    private void loadByteBuffer() throws IOException, UnsupportedAudioFileException {
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = new byte[4096];

        int nBytesRead = 0;
        while(nBytesRead != -1) {
            nBytesRead = inputStream.read(data, 0, data.length);
            if (nBytesRead != -1) {
                out.write(data, 0, nBytesRead);
            }

        }

        byteBuffer = out.toByteArray();
    }

    private void loadDoubleBuffer() { // currently only works for single channel audio
        int frameSize = baseFormat.getFrameSize();

        for (int i = 0; i < byteBuffer.length; i += frameSize) {
            if (i > 100000 && i < 101000) { // TODO: clean this shit up
                System.out.println();
            }
            byte[] tempBuffer = new byte[8 /* byte size of double */];
            int j;
            // fill with actual byte data
            for (j = 0; j < frameSize; j++) {
                tempBuffer[j] = byteBuffer[i + j];
                if (i > 100000 && i < 101000) {
                    System.out.println("Byte[" + j + "]: " + tempBuffer[j]);
                }
            }
            int val = 0;
            if (i > 100000 && i < 101000) {
                val = ((tempBuffer[0] & 0xff) << 8) | (tempBuffer[1] & 0xff);
//                System.out.println("Int Val: " + val);
            }
            // pad with 0s
            for ( ; j < 8; j++) {
                tempBuffer[j] = 0;
            }

            if (i > 100000 && i < 101000) {
                System.out.println("Double Val: " + getFloat64(tempBuffer, frameSize));
            }
        }

        System.out.println("done!");
    }

    private static double getFloat64(byte[] bytes, int byteRate) {
        double value = 0;

        switch (byteRate) {
            case 1:
                value = (double) bytes[0];
                break;
            case 2:
                value = (double)(((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff));
                break;
            case 3:
                value = (double)(((bytes[0] & 0xff) << 16) | ((bytes[1] & 0xff) << 8) | (bytes[2] & 0xff));
                break;
            case 4:
                value = (double)(((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16) | ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff));
                break;
        }

        return value;
    }

    public AudioInputStream getBaseInputStream() throws IOException, UnsupportedAudioFileException {
        return AudioSystem.getAudioInputStream(file);
    }

    public AudioFormat getBaseFormat() {
        return baseFormat;
    }
}
