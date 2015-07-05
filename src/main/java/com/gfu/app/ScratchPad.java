package com.gfu.app;

public class ScratchPad {
    public static void main(String[] args) {
        int val = 0xF0AA;
        byte[] bytes = new byte[2];

        // 11110000 10101010

            // unsigned
        // 11110000     = 16 + 32 + 64 + 128    = 240   = 0xF0
        // 10101010     = 2 + 8 + 32 + 128      = 170   = 0xAA

            // signed

        bytes[0] = (byte)((val >> 8)  & 0xFF);
        bytes[1] = (byte)((val) & 0xFF);

        System.out.println(bytes[0] + ", " + bytes[1]);
    }
}
