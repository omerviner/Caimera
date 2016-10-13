package com.example.viner.erosion;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import static com.example.viner.erosion.Preview.getCameraInstance;


public class MainActivity extends AppCompatActivity {

    private Camera mCamera;
    private Preview mPreview;
    private View mCameraView;
    public byte[] imageToSend;
    private Context mContext;
    boolean opened;
    int mStatusBarHeight;
    RecyclerView rvImgs;
    private FrameLayout mPreviewFrame;
    private File caimera_chosen_temp;
    int mPreviewState;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int CHOOSE_IMAGE_REQUEST = 2;
    private boolean mAfterTakeImage;
    private boolean mBackFromChoose;
    public String imgUrl;
    private ImageView imgPrev;
    private boolean startCameraOnResume = true;
    private Button mCaptureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mCameraView = findViewById(android.R.id.content);
        mContext = this;
        mPreviewFrame = (FrameLayout) findViewById(R.id.camera_preview);
        mAfterTakeImage = false;
        mBackFromChoose = false;

        // Init temp file saving location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Caimera");
        String tempImagePath = mediaStorageDir.getPath() + File.separator + "caimera_chosen_temp.png";
        caimera_chosen_temp = new File(tempImagePath);
        caimera_chosen_temp.delete();
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }

        // Init RecyclerView
//        RecyclerView rvImgs = (RecyclerView) findViewById(R.id.imgs);
        mCaptureButton = (Button)findViewById(R.id.big_button_capture);
//        rvImgs.setHasFixedSize(true);

//        ArrayList<String> imgsPaths = getAllShownImagesPath(this);//TODO:either ask for permission or cancel this .

//        ArrayList<File> mImgs = new ArrayList<>();
//        for (String imgsPath : imgsPaths) {
//            File file = new File(imgsPath);
//            if (file.isFile()) {
//                mImgs.add(0, file);
//            }
//        }

        // Create adapter passing in the sample user data
//        ImgsAdapter adapter = new MainAdapter(this, mImgs, rvImgs);

        // Attach the adapter to the recyclerview to populate items
//        rvImgs.setAdapter(adapter);
        // Set layout manager to position the items
//        rvImgs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

//        rvImgs.addOnItemTouchListener(adapter.getListener());
        initCaptureButton();//the order between the inits is very important(permission handling)//1
        initCamera();//2
        initMainImage();
    }

    private void initMainImage(){

        imgPrev = new ImageView(mContext);
        imgPrev.setId(R.id.main_image_frame);
        imgPrev.setImageDrawable(null);
        imgPrev.bringToFront();

        RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int mWidthPixels = displayMetrics.widthPixels;
        viewParams.height = mWidthPixels + mStatusBarHeight;
        viewParams.width = mWidthPixels;
        viewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        imgPrev.setLayoutParams(viewParams);
        mPreviewFrame.addView(imgPrev);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mPreviewFrame.getLayoutParams();
        params.height = mWidthPixels + mStatusBarHeight;
        mPreviewFrame.setLayoutParams(viewParams);
    }
    /**
     * Safe way to open the camera.
     * @return
     */
    private boolean safeCameraOpenInView() {

        startCameraOnResume = true;
        boolean qOpened = false;
        releaseCameraAndPreview();

        mCamera = getCameraInstance();
        mPreviewFrame.removeView(mPreview);
        mPreview = new Preview(this, mCamera, mCameraView);
        mPreviewFrame.addView(mPreview);

        qOpened = (mCamera != null);

        if(qOpened) {
            mPreview.bringToFront();
            mCamera.startPreview();
            mPreview.buildDrawingCache();
        } else {
            mCamera = getCameraInstance();
            if(mCamera != null){
                mPreview = new Preview(this, mCamera, mCameraView);
                mPreviewFrame.addView(mPreview);
            }

        }
        Log.v("safeCameraOpenInView", "success");

        return qOpened;
    }

    /*
    Release camera function
     */
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
            mPreview.destroyDrawingCache();
            imageToSend = data;
            // NEXT REMOVED: dont show next button on capture
            ImageButton btn = (ImageButton)((MainActivity)mContext).findViewById(R.id.next);
            btn.setVisibility(View.VISIBLE);
        }
    };

    public void onClickChooseImage(View view) {
        startCameraOnResume = false;
        Intent intent = new Intent(this, ChooseImageActivity.class);
        startActivityForResult(intent, CHOOSE_IMAGE_REQUEST);
        mBackFromChoose = true;
//        releaseCameraAndPreview();
//         NEXT REMOVED: release happens in onPause
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                releaseCameraAndPreview();
                return null;
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Get image url from ChooseActivity
        if (requestCode == CHOOSE_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                imgUrl = data.getStringExtra("chosen_image");

                imgPrev.bringToFront();
                Glide
                        .with(this)
                        .load(imgUrl)
                        .centerCrop()
                        .into(imgPrev);

                ImageButton btn = (ImageButton)((MainActivity)mContext).findViewById(R.id.next);
                btn.setVisibility(View.VISIBLE);
                mCaptureButton.setBackgroundResource(R.drawable.capture_resume);

                imageToSend = null;
            }
        }
    }

    /*
    click on Next button. Send chosen/captured image to EffectsActivity
     */
    public void onClickImageIsChosen(View view){

        if (imageToSend == null){
            new SaveTempImage(new saveCallback(), this).execute(null, 0, caimera_chosen_temp, imgUrl);
        }
        else{
            new SaveTempImage(new saveCallback(), this).execute(imageToSend, mPreview.rotation, caimera_chosen_temp);
        }

        // Async-Saving
        Intent intent = new Intent(this, EffectsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                releaseCameraAndPreview();
                return null;
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (startCameraOnResume){
            initCamera();
            mCaptureButton.setBackgroundResource(R.drawable.capture_img);
        }
    }

    private class saveCallback implements Callable<Integer>{//TODO:the class should be an effects activity nested class and delay the req until the file is saved
        @Override
        public Integer call() throws Exception {
            return null;
        }
    }

    /**
     * Get All Images Path
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

    /*
    Init camera function.
     */
    private void initCamera(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                showSettingsAlert();//could do better
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                //request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            }

        } else {
            safeCameraOpenInView();
        }
    }

    /*
    Init capture & resume camera button.
     */
    private void initCaptureButton(){
        mCaptureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAfterTakeImage || mBackFromChoose){

                            ImageButton btn = (ImageButton)findViewById(R.id.next);
                            btn.setVisibility(View.GONE);
                            mCaptureButton.setBackgroundResource(R.drawable.capture_img);
                            mAfterTakeImage = false;
                            mBackFromChoose = false;
                            imageToSend = null;

                            initCamera();
                        } else {
                            mCamera.takePicture(null, null, mPicture);
                            mAfterTakeImage = true;
                            mCaptureButton.setBackgroundResource(R.drawable.capture_resume);
                        }
                    }
                }
        );
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

                    // permission was granted
                    opened = safeCameraOpenInView();
                    if (!opened) {
                        Log.d("onRequestPermissions", "Error, Camera failed to open");
                        return;
                    }
                    initCaptureButton();

                } else {
                    mCaptureButton.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            showSettingsAlert();
                        }
                    });
                }
        }

    }

    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera to take pictures.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        releaseCameraAndPreview();
    }
}
