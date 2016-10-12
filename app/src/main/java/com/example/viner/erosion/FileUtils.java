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
//        try{
//            curImage.createNewFile();
//        } catch (Exception e){
//            Log.v("create new file", "caught exception");
//        }

        //DialogHelper.showDialog( "Success!","Your picture has been saved!",this);
//        Toast.makeText(context, "Your picture has been saved!", Toast.LENGTH_SHORT)
//                .show();
        return curImage;
    }

//    public static byte[] getCapturedData(Context context, byte[] data, int rotation) {
//
//        byte[] croppedData = cropAndRotateImageBytes(context, data, rotation);
//        return croppedData;
//    }
//
//    public static byte[] copyFile(Context context, File file) {
//        File pictureFile = getOutputMediaFile(context, true);
//
//        if (pictureFile == null) {
//            Toast.makeText(context, "Image retrieval failed.", Toast.LENGTH_SHORT)
//                    .show();
//            return null;
//        }
//
//        byte[] data = new byte[(int) file.length()];
//        try {
//            new FileInputStream(file).read(data);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//
//            FileOutputStream fos = new FileOutputStream(pictureFile, true);
//            fos.write(data);
//            fos.close();
//
//        } catch (FileNotFoundException e) {
//            Log.v("Error saving: ", e.toString());
//            e.printStackTrace();
//        } catch (IOException e) {
//            Log.v("Error saving: ", e.toString());
//            e.printStackTrace();
//        }
//        return data;
//
//    }
//
//    public static File saveImageToFile(Context context, byte[] data, int rotation, boolean saveStyle) {
//        File pictureFile = getOutputMediaFile(context, saveStyle);
//        try {
//            pictureFile.createNewFile();
//        } catch (Exception e) {
//
//        }
//
//        if (pictureFile == null) {
//            Toast.makeText(context, "Image retrieval failed.", Toast.LENGTH_SHORT)
//                    .show();
//            return null;
//        }
//        byte[] croppedData = cropAndRotateImageBytes(context, data, rotation);
//
//        try {
//
//            FileOutputStream fos = new FileOutputStream(pictureFile, true);
//            fos.write(croppedData);
//            fos.close();
//
//            // Restart the camera preview.
//        } catch (FileNotFoundException e) {
//            Log.v("Error saving: ", e.toString());
//            e.printStackTrace();
//        } catch (IOException e) {
//            Log.v("Error saving: ", e.toString());
//            e.printStackTrace();
//        }
//        Log.v("saveFileToImage", pictureFile.getAbsolutePath());
//        return pictureFile;
//    }

    public static Bitmap cropAndRotateImageBytes(Context mContext, byte[] data, int rotation) {

//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Matrix matrix = new Matrix();
        if (rotation == 90) {
            matrix.postRotate(90);
        } else if (rotation == 270) {
            matrix.postRotate(270);
        }

        Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, size, size, matrix, true);
        size = Math.min(size, MAX_SIZE);
        cropped = Bitmap.createScaledBitmap(cropped, size,size, false);
//        cropped.compress(Bitmap.CompressFormat.JPEG, 100, bos); // 100 (best quality)
//        byte[] square = bos.toByteArray();
        return cropped;
    }

//    public static Bitmap RotateBitmap(Bitmap source, float angle)
//    {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(angle);
//        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
//    }

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
