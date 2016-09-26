package com.example.viner.erosion;

import android.graphics.Bitmap;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.FileOutputStream;
import java.io.IOException;

public class FileTarget extends SimpleTarget<Bitmap> {
    public FileTarget(String fileName, int width, int height) {
        this(fileName, width, height, Bitmap.CompressFormat.JPEG, 70);
    }
    public FileTarget(String fileName, int width, int height, Bitmap.CompressFormat format, int quality) {
        super(width, height);
        this.fileName = fileName;
        this.format = format;
        this.quality = quality;
    }
    String fileName;
    Bitmap.CompressFormat format;
    int quality;
    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
        try {
            FileOutputStream out = new FileOutputStream(fileName);
            bitmap.compress(format, quality, out);
            out.flush();
            out.close();
            onFileSaved();
        } catch (IOException e) {
            e.printStackTrace();
            onSaveException(e);
        }
    }
    public void onFileSaved() {
        // do nothing, should be overriden (optional)
    }
    public void onSaveException(Exception e) {
        // do nothing, should be overriden (optional)
    }

}
