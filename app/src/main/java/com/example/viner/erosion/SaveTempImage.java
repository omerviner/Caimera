package com.example.viner.erosion;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Viner on 25/09/2016.
 */
public class SaveTempImage extends AsyncTask<byte[], Integer, Boolean> {
    protected Boolean doInBackground(byte[]... data) {
        int count = data.length;

        for (int i = 0; i < count; i++) {
            FileUtils.saveImageToFile(null, data[i], 0, false);
        }
        return true;
    }

    protected void onPostExecute(Boolean result) {
        Log.v("SaveTempImage:", " finished running");
    }
}
