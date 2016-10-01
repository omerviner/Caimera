package com.example.viner.erosion;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class MainAdapter  extends ImgsAdapter{
    private MainActivity mContext;

    public MainAdapter(Context context, ArrayList<File> imgs, RecyclerView rv) {
        super(context, imgs, rv);
        mContext = (MainActivity) context;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final File img = mImgs.get(position);

        // Set item views based on your views and data model
        ImageButton curImg = viewHolder.img;

        final ImageButton caimeraSign = viewHolder.caimera_sign;
        caimeraSign.setVisibility(View.GONE);
        caimeraSign.setClipToOutline(true);
        caimeraSign.setTag(img.getAbsolutePath());
        curImg.setClipToOutline(true);
        caimeraSign.setTag(img.getAbsolutePath());

        Log.v("imgPath", img.getAbsolutePath());
        if(!img.isDirectory()) {
            Glide
                    .with(mContext)
                    .load(mImgs.get(position))
                    .override(150,150)
                    .centerCrop()
                    .into(curImg);
        }

    }

    public ImageItemClickListener getListener(){

        return new ImageItemClickListener(mContext, rv , new ImageItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {

                Log.v("clicked: ", Integer.toString(position));

                String imgSrc = mImgs.get(position).getAbsolutePath();

                ImageButton btn = (ImageButton) mContext.findViewById(R.id.next);
                btn.setVisibility(View.VISIBLE);

                mContext.releaseCameraAndPreview();
                FrameLayout preview = (FrameLayout) mContext.findViewById(R.id.camera_preview);
                preview.removeAllViews();

                ImageView imgPrev = (ImageView) mContext.findViewById(R.id.main_image_frame);
                if (imgPrev != null){//TODO: organize better -  code dup, Why do we init the preview on every click?!!!!!!
                    Glide.with(mContext)
                            .load(imgSrc)
                            .centerCrop()
                            .override(1000,1000)
                            .into(imgPrev);
                } else {
                    imgPrev = new ImageView(mContext);
                    imgPrev.setId(R.id.main_image_frame);

                    // Set the Drawable displayed
                    Glide.with(mContext)
                            .load(imgSrc)
                            .override(1000,1000)
                            .centerCrop()
                            .into(imgPrev);


                    RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    viewParams.height = mWidthPixels + mStatusBarHeight;
                    viewParams.width = mWidthPixels;
                    viewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    imgPrev.setLayoutParams(viewParams);
                    preview.addView(imgPrev);

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)preview.getLayoutParams();
                    params.height = mWidthPixels + mStatusBarHeight;
                    preview.setLayoutParams(viewParams);
                }
            }

            @Override public void onLongItemClick(View view, int position) {
                Log.v("long clicked: ", Integer.toString(position));
            }
        });
    }

}
