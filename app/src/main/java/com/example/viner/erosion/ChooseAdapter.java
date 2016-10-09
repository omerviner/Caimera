package com.example.viner.erosion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by omer on 27/09/2016.
 */
public class ChooseAdapter extends
        RecyclerView.Adapter<ViewHolderColumn> {

    private ChooseImageActivity mContext;
    private RecyclerView mRvImgs;
    protected ArrayList<File> mImgs;


    @Override
    public ViewHolderColumn onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        View imgsView = inflater.inflate(R.layout.item_imgs_column, parent, false);

        // Return a new holder instance
        return new ViewHolderColumn(imgsView);
    }

    @Override
    public int getItemCount() {
        return (int)Math.ceil(mImgs.size()/4);
    }

    public ChooseAdapter(Context context, ArrayList<File> imgs, RecyclerView rvImgs) {
        mContext = (ChooseImageActivity)context;
        mRvImgs = rvImgs;
        mImgs = imgs;
    }


    public void onBindViewHolder(final ViewHolderColumn viewHolder, final int position) {
        // Get the data model based on position
        File imgFile1 = mImgs.get(position * 4);
        File imgFile2 = mImgs.get(position * 4 + 1);
        File imgFile3 = mImgs.get(position * 4 + 2);
        File imgFile4 = mImgs.get(position * 4 + 2);


        // Set item views based on your views and data model
        ImageButton img1 = viewHolder.img1;
        ImageButton img2 = viewHolder.img2;
        ImageButton img3 = viewHolder.img3;
        ImageButton img4 = viewHolder.img4;

        img1.setClipToOutline(true);
        img2.setClipToOutline(true);
        img3.setClipToOutline(true);
        img4.setClipToOutline(true);

//        img1.setTag(imgFile1.getAbsolutePath());
//        img2.setTag(imgFile2.getAbsolutePath());
//        img3.setTag(imgFile3.getAbsolutePath());
//        img4.setTag(imgFile4.getAbsolutePath());

//        String imgPath1 = imgFile1.getAbsolutePath();
//        String imgPath2 = imgFile2.getAbsolutePath();
//        String imgPath3 = imgFile3.getAbsolutePath();
//        String imgPath4 = imgFile4.getAbsolutePath();
//        Log.v("imgPath", imgPath);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView)v;
                int realPosition = position * 4;

                if (v.getId() == R.id.imgBtn1){
                } else if (imageView.getId() == R.id.imgBtn2){
                    realPosition = realPosition + 1;
                } else if (imageView.getId() == R.id.imgBtn3){
                    realPosition = realPosition + 2;
                } else if (imageView.getId() == R.id.imgBtn4){
                    realPosition = realPosition + 3;
                }

                Log.d("onClickPosition: ", Integer.toString(realPosition));
                String imgSrc = mImgs.get(realPosition).toString();

                Log.d("CHANGE", imgSrc);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("chosen_image", imgSrc);
                mContext.setResult(Activity.RESULT_OK, returnIntent);
                mContext.finish();
            }
        };

        if (!imgFile1.isDirectory() && imgFile1.isFile()) {
            Picasso
                    .with(mContext)
                    .load(mImgs.get(position * 4))
                    .resize(150, 150)
                    .centerCrop()
                    .into(img1);
            viewHolder.img1.setOnClickListener(listener);
        }

        if (!imgFile2.isDirectory() && imgFile2.isFile()) {
            Glide
                    .with(mContext)
                    .load(mImgs.get(position * 4 + 1))
                    .override(150, 150)
                    .centerCrop()
                    .into(img2);
            viewHolder.img2.setOnClickListener(listener);
        }

        if (!imgFile3.isDirectory() && imgFile3.isFile()) {
            Glide
                    .with(mContext)
                    .load(mImgs.get(position * 4 + 2))
                    .override(150, 150)
                    .centerCrop()
                    .into(img3);
            viewHolder.img3.setOnClickListener(listener);
        }

        if (!imgFile4.isDirectory() && imgFile4.isFile()) {
            Glide
                    .with(mContext)
                    .load(mImgs.get(position * 4 + 3))
                    .override(150, 150)
                    .centerCrop()
                    .into(img4);
            viewHolder.img4.setOnClickListener(listener);
        }

    }


}
