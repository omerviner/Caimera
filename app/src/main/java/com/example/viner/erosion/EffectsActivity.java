package com.example.viner.erosion;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Viner on 15/08/2016.
 */
public class EffectsActivity extends AppCompatActivity{

    static final int CHOOSE_IMAGE_REQUEST = 1;
    ArrayList<File> mImgs;
    ImgsAdapter mAdapter;
    String mChosenImage;
    String mChosenStyle;
    Context mContext;
    boolean mProcessingImage;
    public static boolean active;
    public NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.effects);
        mContext = this;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//        Bundle extras = getIntent().getExtras();

//        this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);

        byte[] chosenImage = getIntent().getByteArrayExtra("imageData");
//        File file = FileUtils.saveImageToFile(this, chosenImage, 0, false);
//        mChosenImage = extras.getString("image");

//        mChosenImage = file.getAbsolutePath();

        File mediaTempImgStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Caimera");
        mChosenImage = mediaTempImgStorageDir.getPath() + File.separator + "caimera_chosen_temp.jpg";

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Caimera" + File.separator + "styles");
        if (!mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        }
//        String path = mediaStorageDir.getPath() + File.separator + "erosion_tmp" + ".jpg";
//        File curImage = new File(path);

        ImageView imgView = (ImageView) findViewById(R.id.main_image);

        Bitmap img = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        try {
//            img = BitmapFactory.decodeStream(new FileInputStream(curImage), null, options);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        imgView.setImageBitmap(img);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        View effect_scroller = findViewById(R.id.effectsRelativeLayout);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)effect_scroller.getLayoutParams();
        int statusBarHeight = (int)Math.ceil(25 * displayMetrics.density);

        params.height = displayMetrics.heightPixels - displayMetrics.widthPixels - statusBarHeight;
        effect_scroller.setLayoutParams(params);

        View effect_buttons = findViewById(R.id.btnsRelativeLayout);
        effect_buttons.setLayoutParams(params);

        // Lookup the recyclerview in activity layout
        final RecyclerView rvImgs = (RecyclerView) findViewById(R.id.effects);

        rvImgs.setHasFixedSize(true);

        File[] files = mediaStorageDir.listFiles();

        mImgs = new ArrayList<File>();

        for (int i = 0; i < files.length; i++){
            mImgs.add(files[i]);
        }

        // Create adapter passing in the sample user data
        mAdapter = new EffectsAdapter(this, mImgs);

        // Attach the adapter to the recyclerview to populate items
        rvImgs.setAdapter(mAdapter);
        // Set layout manager to position the items
        rvImgs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // That's all!
    }

    public void onClickImageIsChosen(View view){
        ImageView image = (ImageView) this.findViewById(R.id.main_image);
        image.setDrawingCacheEnabled(true);
        Bitmap cropped = Bitmap.createBitmap(image.getDrawingCache());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cropped.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        FileUtils.saveImageToFile(this, byteArray, 0, true);
    }

    public void onClickChooseEffect(View view){
        Intent intent = new Intent(this, ChooseImageActivity.class);
        startActivityForResult(intent, CHOOSE_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CHOOSE_IMAGE_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                String imgUrl = data.getStringExtra("chosen_image");
                File newEffect = new File(imgUrl);
                FileUtils.copyFile(this, newEffect);
                mAdapter.mImgs.add(newEffect);
                mAdapter.notifyItemInserted(mImgs.size() - 1);
//                Log.v("ChooseImageActivity: ", imgUrl);
                mAdapter.notifyDataSetChanged();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    public void onClickShareButton(View v){
        try {
            ImageView mImageView = (ImageView)((EffectsActivity)mContext).findViewById(R.id.main_image);
            mImageView.buildDrawingCache();
            mImageView.setDrawingCacheEnabled(true);
            Bitmap bmp = mImageView.getDrawingCache();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = "IMG_" + timeStamp;
            File file = new File(this.getCacheDir(), filename + ".png");
            FileOutputStream fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            mImageView.setDrawingCacheEnabled(false);
            file.setReadable(true, false);
            final Intent intent = new Intent(     android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        active = false;
        mNotificationManager.cancelAll();

    }


}