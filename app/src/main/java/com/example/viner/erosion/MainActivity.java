package com.example.viner.erosion;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import android.content.pm.PackageManager;

import uk.co.senab.photoview.PhotoViewAttacher;


public class MainActivity extends AppCompatActivity {

    private Camera mCamera; // Should I keep an instance of camera object?
    private Preview mPreview;

    // Reference to the containing view.
    private View mCameraView;
    public byte[] imageToSend;
    private Context mContext;
    ArrayList<File> mImgs;
    String tempImagePath;
    boolean opened;
    int mStatusBarHeight;
    RecyclerView rvImgs;
    FrameLayout mPreviewFrame;

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_PORTRAIT_FFC = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mCameraView = findViewById(android.R.id.content);
        mContext = this;
        mPreviewFrame = (FrameLayout) mCameraView.findViewById(R.id.camera_preview);

//        this.overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_left);
//        Button btn = (Button)((MainActivity)mContext).findViewById(R.id.button_capture);
//        btn.setVisibility(View.VISIBLE);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Caimera");

        tempImagePath = mediaStorageDir.getPath() + File.separator + "caimera_chosen_temp.jpg";
        File caimera_chosen_temp = new File(tempImagePath);
        caimera_chosen_temp.delete();

        // Lookup the recyclerview in activity layout
        rvImgs = (RecyclerView) findViewById(R.id.imgs);
        rvImgs.setHasFixedSize(true);

        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }

        ArrayList<String> imgsPaths = getAllShownImagesPath(this);

        mImgs = new ArrayList<File>();

        for (int i = 0; i < imgsPaths.size(); i++){
            File file = new File(imgsPaths.get(i));
            if (file.isFile()){
                mImgs.add(0, file);
            }
        }

//        mImgs = Lists.newArrayList(imgs);
        // Create adapter passing in the sample user data
        ImgsAdapter adapter = new MainAdapter(this, mImgs, rvImgs);

        // Attach the adapter to the recyclerview to populate items
        rvImgs.setAdapter(adapter);
        // Set layout manager to position the items
        rvImgs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvImgs.addOnItemTouchListener(adapter.getListener());
        initCameraFunctionality();

    }

    /**
     * Recommended "safe" way to open the camera.
     * @return
     */
    private boolean safeCameraOpenInView() {

        boolean qOpened = false;
        releaseCameraAndPreview();
        mCamera = Preview.getCameraInstance();
        qOpened = (mCamera != null);

        if(qOpened) {
            mPreview = new Preview(this, mCamera, mCameraView);

            mPreviewFrame.addView(mPreview);
        }
        Log.v("safeCameraOpenInView", "succ");

//        setLayout();
        ImageButton btn = (ImageButton)findViewById(R.id.next);
        btn.setVisibility(View.GONE);

        return qOpened;
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
//        if (mPreviewFrame != null){
//            mPreviewFrame.removeAllViews();
//        }
    }

    /**
     * Picture Callback for handling a picture capture and saving it out to a file.
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            ImageButton btn = (ImageButton)((MainActivity)mContext).findViewById(R.id.next);
            btn.setVisibility(View.VISIBLE);

            // Omer: data is what should be sent to SaveTempImage
                    //  set imageToSend as data variable

            // Omer: original capture
            imageToSend = FileUtils.getCapturedData(mContext, data, mPreview.rotation);

            Log.v("PictureCallback", "Sending files");

            // Close camera
            ((MainActivity) mContext).releaseCameraAndPreview();

        }
    };

    public void onClickImageIsChosen(View view){

        // Current Image capture
        ImageView image = (ImageView) this.findViewById(R.id.main_image_frame);
        if (imageToSend == null){
            // Get image from ImageView
            Bitmap screenShot = ((GlideBitmapDrawable)image.getDrawable().getCurrent()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            screenShot.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageToSend = stream.toByteArray();
        }


        // Async-Saving
        new SaveTempImage(new saveCallback()).execute(imageToSend);
        Intent intent = new Intent(this, EffectsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCameraAndPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (opened){
            safeCameraOpenInView();
        }
    }
    private class saveCallback implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            return null;
        }
    }

    /**
     * Getting All Images Path
     *
     * @param activity
     * @return ArrayList with images Path
     */
    public static ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            listOfAllImages.add(absolutePathOfImage);
        }
        cursor.close();
        return listOfAllImages;
    }

    public void setLayout(){
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        View rec_filler = findViewById(R.id.rec_filler);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)rec_filler.getLayoutParams();
        mStatusBarHeight = getStatusBarHeight();
        params.height = displayMetrics.heightPixels - displayMetrics.widthPixels - mStatusBarHeight;
        rec_filler.setLayoutParams(params);

        RelativeLayout imgsRelLayout = (RelativeLayout)findViewById(R.id.imgsRelativeLayout);
        RelativeLayout btnsRelLayout = (RelativeLayout)findViewById(R.id.btnsRelativeLayout);
        RelativeLayout.LayoutParams relParams = (RelativeLayout.LayoutParams)imgsRelLayout.getLayoutParams();
        relParams.height = params.height;
        imgsRelLayout.setLayoutParams(relParams);
        btnsRelLayout.setLayoutParams(relParams);

        mPreviewFrame.getLayoutParams().height = displayMetrics.heightPixels;
    }

    @SuppressLint("NewApi")
    private int getStatusBarHeight() {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    private void initCameraFunctionality(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            }

        }
        else {
            opened = safeCameraOpenInView();
            if (!opened) {
                Log.d("CameraGuide", "Error, Camera failed to open");
                return;
            }
            final Button captureButton = (Button) mCameraView.findViewById(R.id.button_capture);
            captureButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCamera != null) {
                                // get an image from the camera
                                mCamera.takePicture(null, null, mPicture);
                            } else {
                                if (mPreviewFrame != null) {
                                    mPreviewFrame.removeAllViews();
                                }
                                imageToSend = null;
                                safeCameraOpenInView();
                            }
                        }
                    }
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                Log.d("APPPermissions", "result");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    opened = safeCameraOpenInView();
                    if (!opened) {
                        Log.d("CameraGuide", "Error, Camera failed to open");
                        return;
                    }
                    final Button captureButton = (Button) mCameraView.findViewById(R.id.button_capture);
                    captureButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mCamera != null) {
                                        // get an image from the camera
                                        mCamera.takePicture(null, null, mPicture);
                                    } else {
                                        if (mPreviewFrame != null) {
                                            mPreviewFrame.removeAllViews();
                                        }
                                        safeCameraOpenInView();
                                    }
                                }
                            }
                    );




                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
