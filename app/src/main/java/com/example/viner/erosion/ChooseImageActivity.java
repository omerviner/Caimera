package com.example.viner.erosion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.common.collect.Lists;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Viner on 15/08/2016.
 */
public class ChooseImageActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_image);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Erosion");
        if (!mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        }


        // Lookup the recyclerview in activity layout
        final RecyclerView rvImgs = (RecyclerView) findViewById(R.id.imgs);
        rvImgs.setHasFixedSize(true);


        ArrayList<String> imgsPaths = MainActivity.getAllShownImagesPath(this);
        ArrayList<File> mImgs = new ArrayList<File>();
        for (int i = 0; i < imgsPaths.size(); i++){
            File file = new File(imgsPaths.get(i));
            if (file.isFile()){
                mImgs.add(0, file);
            }
        }


        // Create adapter passing in the sample user data
        ChooseAdapter adapter = new ChooseAdapter(this, mImgs, rvImgs);

        // Attach the adapter to the recyclerview to populate items
        rvImgs.setAdapter(adapter);
        // Set layout manager to position the items
        rvImgs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // That's all!

    }




}