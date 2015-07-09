package com.gfu.app;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class InputDataMediatorTest {
    double[] testInputData;
    final int TEST_DATA_LENGTH = 100000;
    final int PADDED_LENGTH = 65536;

    @Before
    public void setup() {
        testInputData = new double[TEST_DATA_LENGTH];

        for (int i = 0; i < TEST_DATA_LENGTH; i++) {
            testInputData[i] = i;
        }
    }

    @Test
    public void testSampleCollectionAndPutAndGet() throws InterruptedException {
        InputDataMediator inputDataMediator = new InputDataMediator(testInputData);
        Thread t = new Thread(inputDataMediator);

        t.start();

        Thread.sleep(10); // wait for mediator to populate vector

        double[] sample1 = inputDataMediator.getSample();

        assertNotNull(sample1);
        assertEquals(PADDED_LENGTH, sample1.length);
        arrayContentVerification(testInputData, sample1, 44100, 0);

        double[] sample2 = inputDataMediator.getSample();

        assertNotNull(sample2);
        assertEquals(PADDED_LENGTH, sample2.length);
        arrayContentVerification(testInputData, sample2, 44100, 44100);

        double[] sample3 = inputDataMediator.getSample();

        assertNotNull(sample3);
        assertEquals(PADDED_LENGTH, sample3.length);
        arrayContentVerification(testInputData, sample3, TEST_DATA_LENGTH % 44100, 44100 * 2);
    }

    private void arrayContentVerification(double[] original, double[] testContent, int size, int offset) {
        for (int i = 0; i < size; i++) {
            assertEquals(original[offset + i], testContent[i], 0);
        }
    }
}
