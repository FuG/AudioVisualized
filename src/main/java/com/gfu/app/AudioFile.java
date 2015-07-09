package com.gfu.app;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class AudioFile {
    private static File file;

    private AudioFormat baseFormat;

    private byte[] byteBuffer;

    public byte[] byteBufferEQ = byteBuffer;

    public double[] doubleBuffer;

    private List<double[]> freqSamples;
    private ListIterator<double[]> freqSamplesIter;

    public AudioFile(String filepath) throws IOException, UnsupportedAudioFileException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        file = new File(classLoader.getResource(filepath).getFile());

        init();
        loadByteBuffer();
        loadNormalizedDoubleBuffer();
        loadFreqSamplesList();

        printFormatInfo();
    }

    private void loadFreqSamplesList() {
        int sampleCount = doubleBuffer.length / Settings.INPUT_BUFFER_SIZE;// + 1;
        int lastSampleLength = doubleBuffer.length % Settings.INPUT_BUFFER_SIZE;
        freqSamples = new ArrayList<>();

        for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
            double[] sample = new double[Settings.INPUT_BUFFER_SIZE];

            if (sampleIndex == sampleCount - 1) {
                for (int bufferIndex = 0; bufferIndex < lastSampleLength; bufferIndex++) {
                    sample[bufferIndex] = doubleBuffer[sampleIndex * Settings.INPUT_BUFFER_SIZE + bufferIndex];
                }
            } else {
                for (int bufferIndex = 0; bufferIndex < Settings.INPUT_BUFFER_SIZE; bufferIndex++) {
                    sample[bufferIndex] = doubleBuffer[sampleIndex * Settings.INPUT_BUFFER_SIZE + bufferIndex];
                }
            }

            freqSamples.add(sample);
        }

        freqSamplesIter = freqSamples.listIterator();
    }

    public double[] nextSample() {
        if (freqSamplesIter.hasNext()) {
            return freqSamplesIter.next();
        }

        return null;
    }

    private void init() throws IOException, UnsupportedAudioFileException {
        baseFormat = AudioSystem.getAudioInputStream(file).getFormat();
    }

    public AudioInputStream getAudioInputStream() throws IOException, UnsupportedAudioFileException {
        return AudioSystem.getAudioInputStream(file);
    }

    public int getBytesPerSample() {
        return baseFormat.getSampleSizeInBits() / 8;
    }

    public AudioFormat.Encoding getEncoding() {
        return baseFormat.getEncoding();
    }

    public int getChannels() {
        return baseFormat.getChannels();
    }

    public boolean encodingIsSigned() {
        return getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) ? true : false;
    }

    public float getSampleRate() {
        return baseFormat.getSampleRate();
    }

    private int getFrameSize() {
        return baseFormat.getFrameSize();
    }

    private float getFrameRate() {
        return baseFormat.getFrameRate();
    }

    private boolean isLittleEndian() {
        return !baseFormat.isBigEndian();
    }

    public void printFormatInfo() {
        System.out.println("\nChannels: " + getChannels());
        System.out.println("Encoding: " + getEncoding());
        System.out.println("Sample Size (bytes): " + getBytesPerSample());
        System.out.println("Sample Rate: " + getSampleRate());
        System.out.println("Frame Size: " + getFrameSize());
        System.out.println("Frame Rate: " + getFrameRate());
        System.out.println("Little Endian: " + isLittleEndian() + "\n");
    }

    /* TODO: remove (deprecated) */
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

    /* TODO: remove (deprecated) */
    private void loadNormalizedDoubleBuffer() {
        int sampleSizeInBytes = baseFormat.getSampleSizeInBits() / 8;
        int frameSize = baseFormat.getFrameSize();// currently only works for single channel audio
        boolean isSigned = baseFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) ? true : false;

        doubleBuffer = new double[byteBuffer.length / sampleSizeInBytes];

        int dBufferIndex = 0;
        for (int i = 0; i < byteBuffer.length; i += frameSize) {
            // TODO: add multi-channel support

            if (i > 100000 && i < 101000) { // TODO: clean this up
//                AppletMain.println();
            }
            byte[] tempBuffer = new byte[8 /* byte size of double */];
            int j;
            // fill with actual byte data
            for (j = 0; j < frameSize; j++) {
                tempBuffer[j] = byteBuffer[i + j];
                if (i > 100000 && i < 101000) {
//                    AppletMain.println("Byte[" + j + "]: " + tempBuffer[j]);
                }
            }
            // pad with 0s
            for ( ; j < 8; j++) {
                tempBuffer[j] = 0;
            }
            // normalize the double value and store it
            if (i > 100000 && i < 101000) {
                doubleBuffer[dBufferIndex++] = getNormalizedFloat64(tempBuffer, sampleSizeInBytes, isSigned, true);
            } else {
                doubleBuffer[dBufferIndex++] = getNormalizedFloat64(tempBuffer, sampleSizeInBytes, isSigned, false);
            }

        }

//        AppletMain.println("done!");
    }

    /* TODO: remove (deprecated) */
    public static double getNormalizedFloat64(byte[] bytes, int sampleSizeInBytes, boolean signed, boolean print) {
        double baseValue, normalizedValue = 0;
        // TODO: fix unsigned/signed bit parsing

        switch (sampleSizeInBytes) {
            case 1: // always unsigned
                normalizedValue = (double) bytes[0] / 255;
                break;
            case 2:
                if (signed) {
                    baseValue = (double)(((bytes[0]) << 8) | (bytes[1]));
                    if (baseValue < 0) {
                        normalizedValue = baseValue / 32768;
                    } else {
                        normalizedValue = baseValue / 32767;
                    }
                    if (print) {
//                        AppletMain.println("Base: " + baseValue);
//                        AppletMain.println("Norm: " + normalizedValue);
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
