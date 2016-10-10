package com.example.viner.erosion;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import static com.google.common.io.Files.toByteArray;

/**
 * Created by Viner on 25/09/2016.
 */
public class SaveTempImage extends AsyncTask<Object, Integer, Boolean> {
    private Callable<Integer> callback;
    private final Context mContext;
    private static final String FILENAME = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES) + "/" + "Caimera/" + "caimera_chosen_temp.jpg";
    private static final int IM_BYTE_ARRAY = 0, ROTATION = 1, IM_PATH = 2;
    SaveTempImage(Callable<Integer> callback, Context context){
        mContext = context;
        this.callback = callback;
    }

    protected Boolean doInBackground(Object... args){
        byte[] im  = getImage(args);
        int rotation = (int)args[ROTATION];

        Bitmap bmp = FileUtils.getCroppedRotatedBitmap(mContext, im, rotation);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(FILENAME);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
                if (out != null) try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }

    private byte[] getImage(Object[] args) {
        try {
            return (args.length < 3) ? (byte[]) args[IM_BYTE_ARRAY] : toByteArray(new File((String) args[IM_PATH]));
        } catch (IOException e) {
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
