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

    private byte[] byteBufferEQ = byteBuffer;

    private double[] doubleBuffer;

    public AudioFile(String filepath) throws IOException, UnsupportedAudioFileException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        file = new File(classLoader.getResource(filepath).getFile());

        init();
        loadByteBuffer();
        loadNormalizedDoubleBuffer();
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

    private void loadNormalizedDoubleBuffer() {
        int sampleSizeInBytes = baseFormat.getSampleSizeInBits() / 8;
        int frameSize = baseFormat.getFrameSize();// currently only works for single channel audio

        doubleBuffer = new double[byteBuffer.length / sampleSizeInBytes];

        int dBufferIndex = 0;
        for (int i = 0; i < byteBuffer.length; i += frameSize) {
            // TODO: add multi-channel support

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
            // pad with 0s
            for ( ; j < 8; j++) {
                tempBuffer[j] = 0;
            }
            // normalize the double value and store it
            boolean isSigned = baseFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) ? true : false;
            doubleBuffer[dBufferIndex++] = getNormalizedFloat64(tempBuffer, sampleSizeInBytes, isSigned);

            if (i > 100000 && i < 101000) {
                System.out.println(((tempBuffer[1]) << 8) | (tempBuffer[0]));
                System.out.println("Double Val: " + doubleBuffer[dBufferIndex - 1]);
            }
        }

        System.out.println("done!");
    }

    private void convertDoublesToBytes() {

    }

    private static double getNormalizedFloat64(byte[] bytes, int sampleSizeInBytes, boolean signed) {
        double baseValue, normalizedValue = 0;
        // TODO: fix unsigned/signed bit parsing

        switch (sampleSizeInBytes) {
            case 1: // always unsigned
                normalizedValue = (double) bytes[0] / 255;
                break;
            case 2:
                if (signed) {
                    baseValue = (double)(((bytes[1]) << 8) | (bytes[0]));
                    if (baseValue < 0) {
                        normalizedValue = baseValue / 32768;
                    } else {
                        normalizedValue = baseValue / 32767;
                    }
                } else {
                    baseValue = (double)(((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff));
                    normalizedValue = baseValue / 65536;
                }
                break;
            case 3:
                if (signed) {
                    baseValue = (double)(((bytes[0]) << 16) | ((bytes[1]) << 8) | (bytes[2]));
                    if (baseValue < 0) {
                        normalizedValue = baseValue / 8388608;
                    } else {
                        normalizedValue = baseValue / 8388607;
                    }
                } else {
                    baseValue = (double)(((bytes[0] & 0xFF) << 16) | ((bytes[1] & 0xFF) << 8) | (bytes[2]) & 0xFF);
                    normalizedValue = baseValue / 16777215;
                }
                break;
            case 4:
                if (signed) {
                    baseValue = (double)(((bytes[0]) << 24) | ((bytes[1]) << 16) | ((bytes[2]) << 8) | (bytes[3]));
                    if (baseValue < 0) {
                        normalizedValue = baseValue / 2147423648;
                    } else {
                        normalizedValue = baseValue / 2147423647;
                    }
                } else {
                    baseValue = (double)(((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16) | ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff));
                    normalizedValue = baseValue / 2147423647 / 2;
                }
                break;
        }

        return normalizedValue;
    }

    public AudioInputStream getBaseInputStream() throws IOException, UnsupportedAudioFileException {
        return AudioSystem.getAudioInputStream(file);
    }

    public AudioFormat getBaseFormat() {
        return baseFormat;
    }

    public byte[] getByteBufferEQ() {
        return byteBufferEQ;
    }
}
