package com.gfu.app;

import java.util.Vector;

public class InputDataMediator implements Runnable {
    static final int MAX_ELEMENTS = 5;

    private double[] inputData;
    private int inputDataIndex;
    private Vector inputQueue;

    public InputDataMediator(double[] inputData) {
        this.inputData = inputData;
        inputDataIndex = 0;
        inputQueue = new Vector();
    }

    @Override
    public void run() {
        try {
            while (true) {
                double[] inputSample = nextSample();

                if (inputSample != null) {
                    // enqueue sample
                    putSample(inputSample);
                } else {
                    // no more samples from audioFile, so we sleep
                    Thread.sleep(10);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private double[] nextSample() {
        int remainingBins = inputData.length - inputDataIndex;

        if (remainingBins <= 0) {
            return null; // nothing left to copy
        }

        int sampleSize = remainingBins > Settings.ENFORCED_SAMPLE_RATE ? Settings.ENFORCED_SAMPLE_RATE : remainingBins;
        double[] sample = new double[65536]; // early padding to avoid an extra array copy later

        System.arraycopy(inputData, inputDataIndex, sample, 0, sampleSize);
        inputDataIndex += sampleSize;

        return sample;
    }

    private synchronized void putSample(double[] sample) throws InterruptedException {
        while (inputQueue.size() == MAX_ELEMENTS) {
            wait();
        }
        inputQueue.add(sample);
        notify();
    }

    public synchronized double[] getSample() throws InterruptedException {
        notify();
        while (inputQueue.size() == 0) {
            wait();
        }
        double[] sample = (double[]) inputQueue.firstElement();
        inputQueue.removeElement(sample);
        return sample;
    }
}
