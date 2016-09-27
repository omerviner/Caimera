package com.example.viner.erosion;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.InputStream;
/**
 * interface for network comm callback
 */
public interface CallBack {
        int call(Bitmap image, String StyleNum);
}
