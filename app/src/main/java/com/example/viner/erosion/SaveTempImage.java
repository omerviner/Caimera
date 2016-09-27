package com.example.viner.erosion;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Created by Viner on 25/09/2016.
 */
public class SaveTempImage extends AsyncTask<byte[], Integer, Boolean> {
    private Callable<Integer> callback;
    SaveTempImage(Callable<Integer> callback){
        this.callback = callback;
    }

    protected Boolean doInBackground(byte[]... data) {
        int count = data.length;

        for (byte[] aData : data) {
            FileUtils.saveImageToFile(null, aData, 0, false);
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
