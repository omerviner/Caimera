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
        mImgs = imgs;
    }

    public void onBindViewHolder(final ViewHolderColumn viewHolder, final int position) {
        // Set item views based on your views and data model
        for(int i = 0; i < 4; i++){
            final int realPosition = (position * viewHolder.COULMN_SIZE) + i;
            final File imageFile = mImgs.get(realPosition);
            final ImageButton imageButton = viewHolder.imageButtons[i];
            imageButton.setClipToOutline(true);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("onClickPosition: ", Integer.toString(realPosition));
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("chosen_image", imageFile.toString());
                    mContext.setResult(Activity.RESULT_OK, returnIntent);
                    mContext.finish();
                }
            };

            if(imageFile.isFile()){
                Glide
                        .with(mContext)
                        .load(imageFile)
                        .override(150, 150)
                        .centerCrop()
                        .into(imageButton);
                imageButton.setOnClickListener(listener);
            }
        }

    }

}
