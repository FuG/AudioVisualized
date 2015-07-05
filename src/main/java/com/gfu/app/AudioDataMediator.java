package com.gfu.app;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AudioDataMediator {
    private AudioFile audioFile;

    public byte[] inputByteArray;
    public byte[] outputByteArray;

    private double[] inputNormalizedArray;
    private double[] outputNormalizedArray;

    // TODO: implement asynchronous pipes

    public AudioDataMediator(AudioFile audioFile) {
        this.audioFile = audioFile;

        try {
            loadInputByteArray();
            loadNormalizedArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        addReverb(0.5, 15000, audioFile.getBytesPerSample());
    }

    private void loadInputByteArray() throws IOException, UnsupportedAudioFileException {
        if (audioFile == null) {
            throw new IllegalStateException("Could not load inputByteArray: audioFile was null");
        }

        AudioInputStream inputStream = audioFile.getAudioInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = new byte[4096];

        int nBytesRead = 0;
        while(nBytesRead != -1) {
            nBytesRead = inputStream.read(data, 0, data.length);
            if (nBytesRead != -1) {
                out.write(data, 0, nBytesRead);
            }
        }

        inputByteArray = out.toByteArray();
    }

    private void loadNormalizedArray() {
        if (inputByteArray == null || inputByteArray.length == 0) {
            throw new IllegalStateException("Could not load inputNormalizedArray: inputByteArray was null/empty");
        }

        int sampleSizeInBytes = audioFile.getBytesPerSample();
        boolean isSigned = audioFile.encodingIsSigned();

        inputNormalizedArray = new double[inputByteArray.length / sampleSizeInBytes];

//        int dBufferIndex = 0;
//        for (int i = 0; i < inputByteArray.length; i += sampleSizeInBytes) {
//            // TODO: add multi-channel support (need to use frameSize instead of sampleSizeInBytes)
//            byte[] tempBuffer = new byte[sampleSizeInBytes];
//
//            // fill with actual byte data
//            for (int j = 0; j < sampleSizeInBytes; j++) {
//                tempBuffer[j] = inputByteArray[i + j];
//            }
//
//            // normalize the double value and store it
//            inputNormalizedArray[dBufferIndex++] = getNormalizedFloat64(tempBuffer, sampleSizeInBytes, isSigned);
//        }
        inputNormalizedArray = bytesToDoubles(inputByteArray, sampleSizeInBytes);
    }

    public static byte[] getBytes(double normalizedValue, int sampleSizeInBytes, boolean signed) {
        // sampleSizeInBytes = 1
        if (sampleSizeInBytes == 1) {
            return new byte[] { (byte) (normalizedValue * 255) };
        }

        // sampleSizeInBytes = (2, 3, 4)
        byte[] bytes = new byte[sampleSizeInBytes];
        int maxValue = (int) Math.pow(2, sampleSizeInBytes * 8);
        int offset = 0;
        if (signed) {
            int baseValue = (int) (normalizedValue * (maxValue / 2 - 1));
            for (int i = 0; i < sampleSizeInBytes; i++) {
                bytes[i] = (byte) ((baseValue >> offset) & 0xFF);
                offset += 8;
            }
        } else {
            // TODO: figure this out
            // need to & 0xFF or something like that
        }

        return bytes;
    }

    public static double getNormalizedFloat64(byte[] bytes, int sampleSizeInBytes, boolean signed) {
        if (bytes.length != sampleSizeInBytes) {
            throw new IllegalArgumentException("Could not parse double from byte[]: bytes.length did not math sampleSizeInBytes");
        }

        // sampleSizeInBytes = 1
        if (sampleSizeInBytes == 1) {
            return (double) bytes[0] / 255;
        }

        // sampleSizeInBytes = (2, 3, 4)
        double normalizedValue = 0;
        int maxValue = (int) Math.pow(2, sampleSizeInBytes * 8);
        int offset = 0;
        if (signed) {
            int baseValue = 0;
            for (int i = 0; i < sampleSizeInBytes; i++) {
                if (i == sampleSizeInBytes - 1) {
                    baseValue |= (bytes[i] << offset);
                } else {
                    baseValue |= ((bytes[i] & 0xFF) << offset) ;
                }
                offset += 8;
            }

            normalizedValue = (double) baseValue / (maxValue / 2);
        } else {
            // TODO: figure this out
            // need to & 0xFF or something like that
//                    baseValue = (double)(((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff));
//                    normalizedValue = baseValue / 65536;
        }

        return normalizedValue;
    }

    /* normalized (e.g. - 0 <= x <= 1.0 */
    private static double[] bytesToDoubles(byte[] data, int bytesPerDouble) {
        checkBytesPerDoubleArg(bytesPerDouble);

        int doubleArraySize = data.length / bytesPerDouble;
        double[] doubleArray = new double[doubleArraySize];
        for (int i = 0; i < doubleArraySize; i++) {
            byte[] sampleBytes = new byte[bytesPerDouble];
            for (int j = 0; j < bytesPerDouble; j++) {
                int dataIndex = i * bytesPerDouble + j;
                sampleBytes[j] = data[dataIndex];
            }
            doubleArray[i] = getNormalizedFloat64(sampleBytes, bytesPerDouble, true /* need to pass this in dynamically or get from audioFile */);
        }

        return doubleArray;
    }

    private static byte[] doublesToBytes(double[] data, int bytesPerDouble) {
        checkBytesPerDoubleArg(bytesPerDouble);

        int byteArraySize = data.length * bytesPerDouble;
        byte[] byteArray = new byte[byteArraySize];
        for (int i = 0; i < data.length; i++) {
            byte[] doubleAsBytes = getBytes(data[i], bytesPerDouble, true /* need to pass this in dynamically or get from audioFile */);
            for (int j = 0; j < bytesPerDouble; j++) {
                int byteArrayIndex = i * bytesPerDouble + j;
                byteArray[byteArrayIndex] = doubleAsBytes[j];
            }
        }

        return byteArray;
    }

    private static void checkBytesPerDoubleArg(int bytesPerDouble) {
        if (bytesPerDouble < 1 || bytesPerDouble > 4) {
            throw new IllegalArgumentException("bytesPerDouble must be one of the following values: (1, 2, 3, 4)");
        }
    }

    /* Experimental Functionality */
    public void addReverb(double initialPower, int offset, int bytesPerDouble) {
        int rings = 5;
        double[] reverbNormalArray = new double[inputNormalizedArray.length];
        int i;
        for (i = 0; i < offset; i++) {
            reverbNormalArray[i] = inputNormalizedArray[i] / (1 + rings);
        }

        for (/* i */; i < inputNormalizedArray.length; i++) {
            double sampleAmplitude = inputNormalizedArray[i];
            for (int j = 0; j < rings; j++) {
                if (i - j * offset >= 0) {
                    double currentPower = initialPower * (1.0 / rings) * (rings - j);
                    sampleAmplitude += currentPower * inputNormalizedArray[i - j * offset];
                } else {
                    break;
                }
            }
            reverbNormalArray[i] = sampleAmplitude / (1 + rings);
        }

        for (int j = 0; j < reverbNormalArray.length; j++) {
            if (reverbNormalArray[j] > 1.0) {
                System.out.println("Clipped: [" + j + ", " + reverbNormalArray[j] + "]");
            }
        }

        outputByteArray = doublesToBytes(reverbNormalArray, bytesPerDouble);
    }
}
