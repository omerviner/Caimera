package com.example.viner.erosion;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;

/**
 * Created by Viner on 18/09/2016.
 */
public class FileUtils {

    private static final int MAX_SIZE = 512;

    /**
     * Used to return the camera File output.
     *
     * @return
     */
    public static File getOutputMediaFile(Context context, boolean saveStyle) {
        File mediaStorageDir;
        if (saveStyle == true) {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "Caimera" + File.separator + "styles");
        } else {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "Caimera");
        }

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Camera Guide", "Required media storage does not exist");
                return null;
            }
        }

        String timeStamp;
        String path;
        if (saveStyle) {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            path = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        } else {
            path = mediaStorageDir.getPath() + File.separator + "caimera_chosen_temp.jpg";
        }

        Log.v("getOutputMediaFile:", path);
        File curImage = new File(path);

        return curImage;
    }

    public static Bitmap cropAndRotateImageBytes(Context mContext, byte[] data, int rotation) {

        Bitmap bitmap = null;
        try {
            bitmap = Glide
                    .with(mContext)
                    .load(data)
                    .asBitmap()
                    .centerCrop()
                    .override(MAX_SIZE,MAX_SIZE)
                    .into(MAX_SIZE,MAX_SIZE)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rotation == 0){
            return bitmap;
        }

        Matrix matrix = new Matrix();
        if (rotation == 90) {
            matrix.postRotate(90);
        } else if (rotation == 270) {
            matrix.postRotate(270);
        }

        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, MAX_SIZE, MAX_SIZE, matrix, true);
        return rotated;
    }


    public static Bitmap getCroppedRotatedBitmap(Context context, byte[] data, int rotation) {

        Bitmap b = null;
        try {
            b = Glide
                    .with(context)
                    .load(data)
                    .asBitmap()
                    .transform(new RotateTransformation(context, rotation))
                    .into(500, 500)
                    .get();

        } catch (Exception e) {
            e.printStackTrace();
            Log.v("FileUtils:", " c&r failure");
        }

        return b;
    }

    public static class RotateTransformation extends BitmapTransformation {

        private float rotateRotationAngle = 0f;

        public RotateTransformation(Context context, float rotateRotationAngle) {
            super( context );

            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateRotationAngle);
            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        @Override
        public String getId() {
            return "rotate" + rotateRotationAngle;
        }
    }

}
