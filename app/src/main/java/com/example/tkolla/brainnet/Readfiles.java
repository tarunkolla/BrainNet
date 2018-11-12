package com.example.tkolla.brainnet;

import android.os.Environment;

import java.io.File;

public class Readfiles {

    public Readfiles() {
        File sdcard = Environment.getExternalStorageDirectory().getAbsoluteFile();

        File file = new File(sdcard, "");
    }
}
