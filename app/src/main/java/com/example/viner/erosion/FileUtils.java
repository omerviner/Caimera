package com.example.viner.erosion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

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


    /**
     * Used to return the camera File output.
     * @return
     */
    public static File getOutputMediaFile(Context context){
        File mediaStorageDir;
        if (context instanceof EffectsActivity){
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "Erosion" + File.separator + "styles");
        } else {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "Erosion");
        }


        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Camera Guide", "Required media storage does not exist");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
        String path = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        Log.v("getOutputMediaFile:", path);
        File curImage = new File(path);
//        try{
//            curImage.createNewFile();
//        } catch (Exception e){
//            Log.v("create new file", "caught exception");
//        }

        //DialogHelper.showDialog( "Success!","Your picture has been saved!",this);
        Toast.makeText(context, "Your picture has been saved!", Toast.LENGTH_SHORT)
                .show();
        return curImage;
    }

    public static byte[] saveImageToFile(Context context, byte[] data, int rotation){
        File pictureFile = getOutputMediaFile(context);
        if (pictureFile == null){
            Toast.makeText(context, "Image retrieval failed.", Toast.LENGTH_SHORT)
                    .show();
            return null;
        }
        byte[] croppedData = cropAndRotateImageBytes(data, rotation);

//        try {
//
////            FileOutputStream fos = new FileOutputStream(pictureFile, true);
////            fos.write(croppedData);
////            fos.close();
//
//            // Restart the camera preview.
//        } catch (FileNotFoundException e) {
//            Log.v("Error saving: ", e.toString());
//            e.printStackTrace();
//        } catch (IOException e) {
//            Log.v("Error saving: ", e.toString());
//            e.printStackTrace();
//        }

        return croppedData;
    }

    public static byte[] copyFile(Context context, File file){
        File pictureFile = getOutputMediaFile(context);

        if (pictureFile == null){
            Toast.makeText(context, "Image retrieval failed.", Toast.LENGTH_SHORT)
                    .show();
            return null;
        }

        byte[] data = new byte[(int) file.length()];
        try {
            new FileInputStream(file).read(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            FileOutputStream fos = new FileOutputStream(pictureFile, true);
            fos.write(data);
            fos.close();

        } catch (FileNotFoundException e) {
            Log.v("Error saving: ", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.v("Error saving: ", e.toString());
            e.printStackTrace();
        }

        return data;

    }

    public static byte[] saveImageToFileReally(Context context, byte[] data, int rotation){
        File pictureFile = getOutputMediaFile(context);
        if (pictureFile == null){
            Toast.makeText(context, "Image retrieval failed.", Toast.LENGTH_SHORT)
                    .show();
            return null;
        }
        byte[] croppedData = cropAndRotateImageBytes(data, rotation);

        try {

            FileOutputStream fos = new FileOutputStream(pictureFile, true);
            fos.write(croppedData);
            fos.close();

            // Restart the camera preview.
        } catch (FileNotFoundException e) {
            Log.v("Error saving: ", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.v("Error saving: ", e.toString());
            e.printStackTrace();
        }

        return croppedData;

    }

    public static byte[] cropAndRotateImageBytes(byte[] data, int rotation)
    {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap cropped = Bitmap.createBitmap(bitmap, 0,0,size, size);

        if (rotation == 90){
            cropped = RotateBitmap(cropped, 90);
        } else if (rotation == 270){
            cropped = RotateBitmap(cropped, 270);
        }

        cropped.compress(Bitmap.CompressFormat.JPEG, 100, bos);//100 is the best quality possible
        byte[] square = bos.toByteArray();

        return square;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
