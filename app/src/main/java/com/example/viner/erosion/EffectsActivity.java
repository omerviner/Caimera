package com.example.viner.erosion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.effects);

//        Bundle extras = getIntent().getExtras();

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
        mAdapter = new ImgsAdapter(this, mImgs);

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


}