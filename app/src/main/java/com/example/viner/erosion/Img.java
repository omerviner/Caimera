package com.example.viner.erosion;

import java.io.File;
import java.util.ArrayList;

public class Img {

    private File mPath;
    private String mFileName;

    public Img(File path, String fileName) {
        mPath = path;
        mFileName = fileName;
    }

    public String getFileName() {
        return mFileName;
    }

    public File getFile() {
        return mPath;
    }

    public static ArrayList<Img> createImgsList(int numContacts) {
        ArrayList<Img> imgs = new ArrayList<Img>();

        String imgsPath = "";
        String imgsAppPath = "";

        String imgsInDir = "";

        for (int i = 1; i <= numContacts; i++) {
            imgs.add(new Img(null, "demo"));
        }

        return imgs;
    }
}
