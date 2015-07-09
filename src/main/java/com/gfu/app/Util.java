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
}
