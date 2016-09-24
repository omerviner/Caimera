package com.example.viner.erosion;

import android.graphics.Bitmap;
import java.io.InputStream;
/**
 * interface for network comm callback
 */
public interface CallBack {
    int call(InputStream image);
}
