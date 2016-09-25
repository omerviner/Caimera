package com.example.viner.erosion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.common.collect.Lists;

//import com.commonsware.cwac.cam2.CameraActivity;
//import com.commonsware.cwac.cam2.CameraView;
//import com.commonsware.cwac.cam2.Facing;
//import com.commonsware.cwac.cam2.FlashMode;
//import com.commonsware.cwac.cam2.FocusMode;
//import com.commonsware.cwac.cam2.ZoomStyle;
//import com.commonsware.cwac.cam2.util.Size;

import com.google.common.collect.Lists;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;

import uk.co.senab.photoview.PhotoViewAttacher;


public class MainActivity extends AppCompatActivity {

    private Camera mCamera; // Should I keep an instance of camera object?
    private Preview mPreview;

    // Reference to the containing view.
    private View mCameraView;
    private File curImage;
    private byte[] imgData;
    private Context mContext;
    File[] imgs;
    ArrayList<File> mImgs;
    String tempImagePath;


    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_PORTRAIT_FFC = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mCameraView = findViewById(android.R.id.content);
        mContext = this;

        Button btn = (Button)((MainActivity)mContext).findViewById(R.id.button_capture);
        btn.setVisibility(View.VISIBLE);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Caimera");

        tempImagePath = mediaStorageDir.getPath() + File.separator + "caimera_chosen_temp.jpg";
        File caimera_chosen_temp = new File(tempImagePath);
        caimera_chosen_temp.delete();

        ///////////////////////////////////////


//
//        FlashMode[] flash_modes = new FlashMode[3];
//        flash_modes[0] = FlashMode.OFF;
//        flash_modes[1] = FlashMode.AUTO;
//        flash_modes[2] = FlashMode.ALWAYS;

//        CameraView cameraView = CameraView(this);

//        CameraView cameraView = (CameraView)findViewById(R.id.camera_view);

//        cameraView.setPreviewSize(new Size(100, 100));


//        cameraView = new CameraView(this);
//        this.findViewById(R.id.camera_view);

//        Intent i = new CameraActivity.IntentBuilder(MainActivity.this)
//                .skipConfirm()
//                .facing(Facing.BACK)
//                .to(new File(mediaStorageDir, "portrait-front.jpg"))
//                .debug()
//                .debugSavePreviewFrame()
//                .flashModes(flash_modes)
//                .updateMediaStore()
//                .build();
//
//        CameraActivity.pl


//        startActivityForResult(i, REQUEST_PORTRAIT_FFC);


        ///////////////////////////////////////

//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "Erosion");
//
//        Intent i=new CameraActivity.IntentBuilder(MainActivity.this)
//                .skipConfirm()
//                .facing(Facing.BACK)
//                .to(new File(mediaStorageDir.getPath(), "portrait-front.jpg"))
//                .debug()
//                .zoomStyle(ZoomStyle.SEEKBAR)
//                .updateMediaStore()
//                .build();
//
//        startActivityForResult(i, GET_IMAGE);


        // Lookup the recyclerview in activity layout
        final RecyclerView rvImgs = (RecyclerView) findViewById(R.id.imgs);
        rvImgs.setHasFixedSize(true);

        // Initialize images path
//        imgs = Img.createImgsList(20);
        // get path of imgs
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "Erosion");

        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }

        // list path files to strings and set imgs
        imgs = mediaStorageDir.listFiles();

        mImgs = new ArrayList<File>();
        for (int i = 0; i < imgs.length; i++){
            mImgs.add(imgs[i]);
        }
//        mImgs = Lists.newArrayList(imgs);
        // Create adapter passing in the sample user data
        ImgsAdapter adapter = new ImgsAdapter(this, mImgs);

        // Attach the adapter to the recyclerview to populate items
        rvImgs.setAdapter(adapter);
        // Set layout manager to position the items
        rvImgs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // That's all!

//        LayoutInflater factory = getLayoutInflater();
//
//        View mainView = factory.inflate(R.layout.activity_main, null);

//        View rec_filler = (View)findViewById(R.id.rec_filler);
//
//        ViewGroup.LayoutParams params = rec_filler.getLayoutParams();
//        params.height = rec_filler.getMeasuredWidth();
//        rec_filler.setLayoutParams(params);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        View rec_filler = findViewById(R.id.rec_filler);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)rec_filler.getLayoutParams();

        int statusBarHeight = (int)Math.ceil(25 * displayMetrics.density);

        params.height = displayMetrics.heightPixels - displayMetrics.widthPixels - statusBarHeight;
        rec_filler.setLayoutParams(params);

        RelativeLayout imgsRelLayout = (RelativeLayout)findViewById(R.id.imgsRelativeLayout);
        RelativeLayout btnsRelLayout = (RelativeLayout)findViewById(R.id.btnsRelativeLayout);
        RelativeLayout.LayoutParams relParams = (RelativeLayout.LayoutParams)imgsRelLayout.getLayoutParams();
        relParams.height = params.height;
        imgsRelLayout.setLayoutParams(relParams);
        btnsRelLayout.setLayoutParams(relParams);

        boolean opened = safeCameraOpenInView();

        if(opened == false){
            Log.d("CameraGuide","Error, Camera failed to open");
            return;
        }

        // Trap the capture button.
        Button captureButton = (Button) mCameraView.findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get an image from the camera
                    mCamera.takePicture(null, null, mPicture);

                }
            }
        );
    }



    /**
     * Recommended "safe" way to open the camera.
     * @return
     */
    private boolean safeCameraOpenInView() {
        boolean qOpened = false;
        releaseCameraAndPreview();
        mCamera = getCameraInstance();
        qOpened = (mCamera != null);

        if(qOpened == true) {
            mPreview = new Preview(this.getApplicationContext(), mCamera, mCameraView);
            FrameLayout preview = (FrameLayout) mCameraView.findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            mPreview.startCameraPreview();
            Log.v("safeCameraOpenInView", "succ");
        }
        return qOpened;
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            Log.v("getCameraInstance", "opened");
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if(mPreview != null){
            mPreview.destroyDrawingCache();
            mPreview.mCamera = null;
        }
    }

    /**
     * Picture Callback for handling a picture capture and saving it out to a file.
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            imgData = data;

            Button captureButton = (Button) mCameraView.findViewById(R.id.button_capture);
            captureButton.setVisibility(View.INVISIBLE);
            ImageButton btn = (ImageButton)((MainActivity)mContext).findViewById(R.id.next);
            btn.setVisibility(View.VISIBLE);

            byte[] croppedData = FileUtils.getCapturedData(mContext, data, mPreview.rotation);
            Log.v("PictureCallback", "Sending files");

            // Close camera
            ((MainActivity) mContext).releaseCameraAndPreview();
            FrameLayout preview = (FrameLayout)((MainActivity)mContext).findViewById(R.id.camera_preview);
            preview.removeAllViews();

            // Create image view with captured image
            ImageView imgPrev = new ImageView(mContext);
            imgPrev.setId(R.id.main_image_frame);

            Bitmap bitmap = BitmapFactory.decodeByteArray(croppedData, 0, croppedData.length);
            Drawable drawable = new BitmapDrawable(bitmap);
            imgPrev.setImageDrawable(drawable);

            // Use PhotoView to view / crop / move / etc.
            PhotoViewAttacher mAttacher = new PhotoViewAttacher(imgPrev);
            mAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mAttacher.getDisplayMatrix(new Matrix());

            // PhotoView params
            RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            viewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();

            viewParams.height = displayMetrics.widthPixels;

            mAttacher.setScale(displayMetrics.widthPixels / bitmap.getWidth());

            imgPrev.setLayoutParams(viewParams);

            // Setting new view
            preview.addView(imgPrev);


//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)preview.getLayoutParams();
////                        imgPrev.setImageBitmap(bitmap);
////                        imgPrev.setLayoutParams(params);
////                        imgPrev.getLayoutParams().height = 1100;
//
//
//            preview.setLayoutParams(params);

        }
    };


    public void onClickImageIsChosen(View view){

//        ((MainActivity) mContext).releaseCameraAndPreview();
//        FrameLayout preview = (FrameLayout)((MainActivity)mContext).findViewById(R.id.camera_preview);
//
//        preview.removeAllViews();
//
//        ImageView imgPrev = new ImageView(mContext);
//        // Set the Drawable displayed
////                        Drawable bitmap = getResources().getDrawable(R.drawable.wallpaper);
//        Drawable drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(mPicture, 0, mPicture.length));
////        Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
//        imgPrev.setImageDrawable(drawable);
//
//        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
//        // (not needed unless you are going to change the drawable later)
//        PhotoViewAttacher mAttacher = new PhotoViewAttacher(imgPrev);
//        mAttacher.getScale();
//        mAttacher.getDisplayMatrix(new Matrix());
//
////                        ImageView imgPrev = new ImageView(mContext);
//        RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//
//        viewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        imgPrev.setLayoutParams(viewParams);
//        preview.addView(imgPrev);

        ///////

        ImageView image = (ImageView) this.findViewById(R.id.main_image_frame);
        image.setDrawingCacheEnabled(true);

        Bitmap cropped = Bitmap.createBitmap(image.getDrawingCache());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cropped.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

//        File file = FileUtils.saveImageToFile(this, byteArray, 0, false);
        new SaveTempImage().execute(byteArray);
        Intent intent = new Intent(this, EffectsActivity.class);
//        intent.putExtra("imageData", byteArray);
        startActivity(intent);

    }




}
