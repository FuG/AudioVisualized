package com.gfu.app;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Util {
    public static void printArrayToFile(double[] array, String fileName) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("C:\\Users\\Gary\\Desktop\\" + fileName, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < array.length; i++) {
            writer.println(array[i]);
        }
        writer.close();
    }

    public static double[] copyArray(double[] array, int length, double volumeFactor) {
        double[] newArray = new double[length];
        for (int i = 0; i < length; i++) {
            newArray[i] = array[i] * volumeFactor;
        }

        return newArray;
    }
}
