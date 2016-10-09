package com.example.viner.erosion;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Created by Viner on 25/09/2016.
 */
public class SaveTempImage extends AsyncTask<Object, Integer, Boolean> {
    private Callable<Integer> callback;
    private final Context mContext;
    private static final String FILENAME = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES) + "/" + "Caimera/" + "caimera_chosen_temp.jpg";
    SaveTempImage(Callable<Integer> callback, Context context){
        mContext = context;
        this.callback = callback;
    }

    protected Boolean doInBackground(Object... args) throws IOException {
        byte[] im  = (byte[])args[0];
        int rotation = (int)args[1];
        Bitmap bmp = FileUtils.getCropped(mContext, im, rotation);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(FILENAME);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
                if (out != null) out.close();
        }
        return true;
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
