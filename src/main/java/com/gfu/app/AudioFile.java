package com.gfu.app;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class AudioFile {
    private static File file;

    public AudioInputStream baseInputStream;
    public AudioInputStream decodedInputStream;

    private AudioFormat baseFormat;
    private AudioFormat decodedFormat;

    public AudioFile(String filepath) throws IOException, UnsupportedAudioFileException {
        file = new File(filepath);

        baseInputStream = AudioSystem.getAudioInputStream(file);
        baseFormat = baseInputStream.getFormat();
        decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                baseFormat.getSampleSizeInBits(),
                baseFormat.getChannels(),
                baseFormat.getChannels() * baseFormat.getFrameSize(),
                baseFormat.getSampleRate(),
                false);
    }

    public AudioFormat getBaseFormat() {
        return baseFormat;
    }

    public AudioFormat getDecodedFormat() {
        return decodedFormat;
    }
}
