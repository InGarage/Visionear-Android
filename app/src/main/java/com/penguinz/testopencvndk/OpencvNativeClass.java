package com.penguinz.testopencvndk;

/**
 * Created by PenguinZ on 15/5/2560.
 */

public class OpencvNativeClass {
    public native static int convertGray(long MatAddrRgba, long MatAddrGray);

    public native static int donothing(long MatAddrRgba, long MatAddrGray);
}
