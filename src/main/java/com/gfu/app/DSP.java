package com.gfu.app;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import javax.sound.sampled.AudioInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class DSP {
    AudioFile audioFile;
    Complex[] complexResults;
    List<FFTData> fftDataList;
    public List<double[]> freqSampleList;
    FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.UNITARY);

    class FFTData {
        public Complex complex;
        public double magnitude;
        public double phase;

        public FFTData(Complex complex) {
            this.complex = complex;

            magnitude = complex.abs();
            phase = complex.getArgument();
//            AppletMain.println("Phase: " + phase + "             args: " + complex.getArgument());
        }

        public Complex backToComplex() {
            Complex c = (Complex.I.multiply(Math.sin(phase)).add(Math.cos(phase))).multiply(magnitude);

            return c;
        }

        public void printConversion() throws InterruptedException {
            Complex c = backToComplex();

            AppletMain.println("real (original) : " + complex.getReal());
            AppletMain.println("real (converted): " + c.getReal());

            AppletMain.println("imaginary (original) : " + complex.getImaginary());
            AppletMain.println("imaginary (converted): " + c.getImaginary());
        }
    }

    public DSP(AudioFile audioFile) {
        this.audioFile = audioFile;

//        transform(audioFile.doubleBuffer); // don't do anything with return

//        fftDataList = new ArrayList<>();
//        for (Complex c : complexResults) {
//            fftDataList.add(new FFTData(c));
//        }
    }

    public void applyEQ() { // 297 (bin center for 100hz)
        double[] eqSpec = generateEQSpectrum(4410, 44100);

        for (int i = 0; i < eqSpec.length; i++) {
            System.out.println("(" + i + "): " + eqSpec[i]);
        }

        for (int i = 0; i < eqSpec.length; i++) {
            complexResults[i + 292] = complexResults[i + 292].multiply(eqSpec[i] + 1);
        }
    }

    private double[] generateEQSpectrum(double frequency, double sampleRate) {
        double samplingInterval = sampleRate / frequency;
        double[] sineWaveArray = new double[(int) (samplingInterval)]; // 16-bit audio

        for (int i = 0; i < samplingInterval; i++) {
            double angle = (2.0 * Math.PI * i) / samplingInterval - (Math.PI / 2);
            sineWaveArray[i] = (Math.sin(angle) + 1) / 2;
        }


        return sineWaveArray;
    }

    public double[] inverseTransform() {
//        Complex[] complexes = new Complex[fftDataList.size()];
//
//        int i = 0;
//        for (FFTData d : fftDataList) {
//            complexes[i++] = d.complex;
////            AppletMain.println("real: " + complexes[i-1].getReal() + "         img: " + complexes[i - 1].getImaginary() + "          abs: " + complexes[i-1].abs() + "          arg: " + complexes[i-1].getArgument());
//        }

        double[] tempConversion = null;
        try {
            Complex[] results = transformer.transform(complexResults, TransformType.INVERSE); // TODO: why is this not working???
            tempConversion = new double[results.length];
            for (int j = 0; j < results.length; j++) {

                tempConversion[j] = results[j].getReal();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return tempConversion;
    }

    public static List<double[]> generateFrequencySpectrums(AudioFile audioFile) {
        return null;
    }

    public static List<double[]> generateAmplitudeSpectrums(AudioFile audioFile) {
        return null;
    }

    private double[] decodeToDoubles(AudioInputStream inputStream) {
        // TODO: not needed, done in AudioFile.java
        return null;
    }

    public double[] transform(double[] input) {
//        int paddedLength = 262144;
        int paddedLength = (int) Math.pow(2, 17);
        double[] paddedInput = new double[paddedLength];

        for (int i = 0; i < (input.length < paddedLength ? input.length : paddedLength); i++) {
            paddedInput[i] = input[i];
        }
        double[] tempConversion = new double[paddedLength];
//        double[] tempConversion = new double[input.length];

        try {
            complexResults = transformer.transform(paddedInput, TransformType.FORWARD);
//            complexResults = transformer.transform(input, TransformType.FORWARD);

            for (int i = 0; i < complexResults.length; i++) {
                tempConversion[i] = complexResults[i].abs();

                if (i < 1000) {
                    System.out.println("(" + i + "): " + tempConversion[i]);
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return tempConversion;
    }
}
