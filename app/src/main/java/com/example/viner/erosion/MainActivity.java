package com.example.viner.erosion;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
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

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Callable;
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
    boolean opened;
    int mStatusBarHeight;
    RecyclerView rvImgs;

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
        // That's all!
//        LayoutInflater factory = getLayoutInflater();

//        View mainView = factory.inflate(R.layout.activity_main, null);

//        View rec_filler = (View)findViewById(R.id.rec_filler);
//
//        ViewGroup.LayoutParams params = rec_filler.getLayoutParams();
//        params.height = rec_filler.getMeasuredWidth();
//        rec_filler.setLayoutParams(params);
        setLayout();

        opened = safeCameraOpenInView();

        if(!opened){
            Log.d("CameraGuide","Error, Camera failed to open");
            return;
        }

        // Trap the capture button.
        final Button captureButton = (Button) mCameraView.findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCamera != null){
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    } else {
                        safeCameraOpenInView();
                        setLayout();
                        captureButton.bringToFront();
                        rvImgs.bringToFront();
                    }
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
        mPreview = new Preview(this.getApplicationContext(), mCamera, mCameraView);
        FrameLayout preview = (FrameLayout) mCameraView.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        rvImgs.bringToFront();
//           mPreview.startCameraPreview();TODO: BEWARE this was removed and made the passed null surface go away(it might be since we are replacing an existing preview with a new one thus eliminating all refrences to it without releasing it in some way)
            Log.v("safeCameraOpenInView", "succ");


        ImageButton btn = (ImageButton)findViewById(R.id.next);
        btn.setVisibility(View.GONE);

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

//            Bitmap bitmap = BitmapFactory.decodeByteArray(croppedData, 0, croppedData.length);

            Glide.with(mContext)
                    .load(croppedData)
                    .asBitmap()
                    .override(1000, 1000)
                    .centerCrop()
                    .into(imgPrev);


//
            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();

            RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            viewParams.height = displayMetrics.widthPixels + mStatusBarHeight;
            viewParams.width = displayMetrics.widthPixels;
            viewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            imgPrev.setLayoutParams(viewParams);
            imgPrev.setId(R.id.main_image_frame);
            // Setting new view
            preview.addView(imgPrev);

        }
    };


    public void onClickImageIsChosen(View view){


        ImageView image = (ImageView) this.findViewById(R.id.main_image_frame);
        image.setDrawingCacheEnabled(true);

        Bitmap cropped = Bitmap.createBitmap(image.getDrawingCache());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cropped.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        new SaveTempImage(new saveCallback()).execute(byteArray);
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
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                File dir = new File(mContext.getExternalCacheDir() ,"results");
                if (dir.isDirectory())
                {
                    String[] children = dir.list();
                    for (String aChildren : children) {
                        new File(dir, aChildren).delete();
                    }
                }
                return null;
            }
        }.execute();//cache TODO:debug!
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
        cursor.close();//TODO: omer added since is the correct way to work with a cursor.Check!!
        return listOfAllImages;
    }

    private void setLayout(){
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

}
