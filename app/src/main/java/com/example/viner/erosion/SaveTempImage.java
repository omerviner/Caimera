package com.example.viner.erosion;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.google.common.base.Strings;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import static com.google.common.io.Files.toByteArray;

public class SaveTempImage extends AsyncTask<Object, Integer, Boolean> {
    private Callable<Integer> callback;
    private final Context mContext;
    private static final int IM_BYTE_ARRAY = 0, ROTATION = 1, TARGET = 2, IM_PATH = 3;
    SaveTempImage(Callable<Integer> callback, Context context){
        mContext = context;
        this.callback = callback;
    }

    protected Boolean doInBackground(Object... args){
        byte[] im  = getImage(args);
        int rotation = (int)args[ROTATION];
        File target = (File)args[TARGET];
        Bitmap bmp = FileUtils.cropAndRotateImageBytes(mContext, im, rotation);
        FileOutputStream out = null;
        try {
            out = new  FileOutputStream(target, false);
//            out.write(im);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
                if (out != null) try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }

    private byte[] getImage(Object[] args) {
        try {
            if(args.length < 4){
                Log.d("SaveImage", "Camera Pic");
                return (byte[]) args[IM_BYTE_ARRAY];
            }
            Log.d("SaveImage", "Device Image");
            return toByteArray(new File((String) args[IM_PATH]));
        } catch (IOException e) {
            Log.d("SaveImage", " NO image");
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(Boolean result) {
        try {
            callback.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("SaveTempImage:", " finished running");
    }
}
