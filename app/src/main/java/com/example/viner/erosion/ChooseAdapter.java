package com.example.viner.erosion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by omer on 27/09/2016.
 */
public class ChooseAdapter extends ImgsAdapter {
    private ChooseImageActivity mContext;
    private RecyclerView mRvImgs;
    public ChooseAdapter(Context context, ArrayList<File> imgs, RecyclerView rvImgs) {
        super(context, imgs);
        mContext = (ChooseImageActivity)context;
        mRvImgs = rvImgs;
    }

    @Override
    ImageItemClickListener getListener() {
        ImageItemClickListener listener = new ImageItemClickListener(mContext, mRvImgs ,new ImageItemClickListener.OnItemClickListener() {

            @Override public void onItemClick(View view, int position) {

                Log.v("chosen clicked: ", Integer.toString(position));
                ImageView imageView = (new ViewHolder(view)).img;
                String imgSrc = (String) imageView.getTag();
                Log.d("CHANGE",String.valueOf(position));
                Intent returnIntent = new Intent();
                returnIntent.putExtra("chosen_image", imgSrc);
                mContext.setResult(Activity.RESULT_OK, returnIntent);
                mContext.finish();
            }

            @Override public void onLongItemClick(View view, int position) {
                Log.v("chosen long clicked: ", Integer.toString(position));
            }
        });

        return listener;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        File img = mImgs.get(position);


        // Set item views based on your views and data model
        ImageButton curImg = viewHolder.img;

        final ImageButton caimeraSign = viewHolder.caimera_sign;
        caimeraSign.setVisibility(View.GONE);
        caimeraSign.setClipToOutline(true);
        caimeraSign.setTag(img.getAbsolutePath());
        curImg.setClipToOutline(true);
        curImg.setTag(img.getAbsolutePath());
        caimeraSign.setTag(img.getAbsolutePath());

        String imgPath = img.getAbsolutePath();
        Log.v("imgPath", imgPath);

        if (img.isDirectory()) {
            return;
        } else {
            Picasso
                    .with(mContext)
                    .load(mImgs.get(position))
                    .resize(150, 150)
                    .centerCrop()
                    .into(curImg);


            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView imageView = (ImageView)v;
                    String imgSrc = (String) imageView.getTag();
                    Log.d("CHANGE",String.valueOf(position));
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("chosen_image", imgSrc);
                    mContext.setResult(Activity.RESULT_OK, returnIntent);
                    mContext.finish();

                }
            };
//            viewHolder.img.setOnClickListener(listener);
//            viewHolder.caimera_sign.setOnClickListener(listener);



        }
        }
    }
