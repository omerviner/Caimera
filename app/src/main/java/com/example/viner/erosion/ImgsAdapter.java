package com.example.viner.erosion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Choreographer;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.viner.erosion.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ImgsAdapter extends
        RecyclerView.Adapter<ImgsAdapter.ViewHolder> {

    // Store a member variable for the images
    public ArrayList<File> mImgs;
//    private ArrayList<File> mImgs;
    // Store the context for easy access
    private Context mContext;

    private PhotoViewAttacher mAttacher;
    private int mStatusBarHeight;
    private int mWidthPixels;
    private int mHeightPixels;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View imgsView = inflater.inflate(R.layout.item_img, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(imgsView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        File img = mImgs.get(position);
        // Set item views based on your views and data model
        ImageButton curImg = viewHolder.img;
        curImg.setClipToOutline(true);
        curImg.setTag(img.getAbsolutePath());

        Picasso
                .with(mContext)
                .load(mImgs.get(position))
                .resize(150,150)
                .centerCrop()
                .into(curImg);
    }

    @Override
    public int getItemCount() {
        return mImgs.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row

        public ImageButton img;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            img = (ImageButton)itemView.findViewById(R.id.imgBtn);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView imageView = (ImageView)v;
                    String imgSrc = (String)imageView.getTag();
                    Bitmap bitmap = BitmapFactory.decodeFile(imgSrc);
//                    Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                    if (mContext instanceof MainActivity) {

                        ImageButton btn = (ImageButton)((MainActivity)mContext).findViewById(R.id.next);
                        btn.setVisibility(View.VISIBLE);

                        ImageView imgPrev = (ImageView)((MainActivity)mContext).findViewById(R.id.main_image_frame);
                        if (imgPrev != null){
//                            if (bitmap.getHeight() > bitmap.getWidth()){
//                                Picasso.with(mContext)
//                                        .load(new File(imgSrc))
//                                        .resize(mWidthPixels,0)
//                                        .centerCrop()
//                                        .into(imgPrev);
//                            } else {
//                                Picasso.with(mContext)
//                                        .load(new File(imgSrc))
//                                        .resize(0,mWidthPixels)
//                                        .centerCrop()
//                                        .into(imgPrev);
//                            }
                            Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
                            imgPrev.setImageDrawable(drawable);
                            mAttacher.setDisplayMatrix(new Matrix());

                        } else {
                            ((MainActivity) mContext).releaseCameraAndPreview();
                            FrameLayout preview = (FrameLayout)((MainActivity)mContext).findViewById(R.id.camera_preview);
                            preview.removeAllViews();
                            imgPrev = new ImageView(mContext);
                            imgPrev.setId(R.id.main_image_frame);

                            // Set the Drawable displayed
//                        Drawable bitmap = getResources().getDrawable(R.drawable.wallpaper);

                            //////////////
                            Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
                            imgPrev.setImageDrawable(drawable);
                            ///////////////
//                            Picasso.with(mContext)
//                                    .load(new File(imgSrc))
//                                    .fit() // will explain later
//                                    .into(imgPrev);

                            // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
                            // (not needed unless you are going to change the drawable later)
                            mAttacher = new PhotoViewAttacher(imgPrev);
                            mAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            mAttacher.getDisplayMatrix(new Matrix());



//                        ImageView imgPrev = new ImageView(mContext);
                            RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            viewParams.height = mWidthPixels;
                            viewParams.width = mWidthPixels;
                            viewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                            imgPrev.setLayoutParams(viewParams);
                            preview.addView(imgPrev);

                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)preview.getLayoutParams();
                            params.height = mWidthPixels;
                            preview.setLayoutParams(params);
                        }


//                        imgPrev.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                        Picasso.with(mContext)
//                                .load(new File(imgSrc))
////                                    .fit() // will explain later
//                                .resize(mWidthPixels, mWidthPixels)
//                                .center()
//
//                                .into(imgPrev);
//                        imgPrev.setAdjustViewBounds(true);
//                        imgPrev.setScaleType(ImageView.ScaleType.CENTER_CROP);

//                        preview.addView(imgPrev);

                    } else if (mContext instanceof EffectsActivity){
//                        Log.v("SRC: ", imgSrc);
//                        File imgFile = new File(imgSrc);
                        String imgWithEffect = null;
                        try {
                            NetInterface.process(new NetCallback(), ((EffectsActivity) mContext).mChosenImage, imgSrc);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ImageView mImageView = (ImageView)((EffectsActivity)mContext).findViewById(R.id.main_image);

                        Picasso.with(mContext)
                                .load(imgWithEffect)
                                .centerCrop()
                                .into(mImageView);

                        // Set the Drawable displayed
//                        Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
//                        mImageView.setImageDrawable(drawable);

                        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
                        // (not needed unless you are going to change the drawable later)
//                        PhotoViewAttacher mAttacher = new PhotoViewAttacher(mImageView);
//                        mAttacher.getScale();
//                        mAttacher.getDisplayMatrix(new Matrix());

                    } else if (mContext instanceof ChooseImageActivity) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("chosen_image", imgSrc);
                        ((ChooseImageActivity)mContext).setResult(Activity.RESULT_OK, returnIntent);
                        ((ChooseImageActivity)mContext).finish();

                    }
                }
            });

            if (mContext instanceof EffectsActivity){
                img.setOnLongClickListener(new View.OnLongClickListener(){

                    @Override
                    public boolean onLongClick(final View v) {
                        v.setVisibility(View.GONE);
                        File file = new File((String)v.getTag());
                        file.delete();

//                        new MaterialDialog.Builder(mContext)
//
//                                .title(R.string.title)
//                                .content(R.string.content)
//                                .positiveText(R.string.agree)
//                                .negativeText(R.string.disagree)
//                                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                    @Override
//                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
////                                        Log.v("Dialog", "Was positive");
//                                        img.setVisibility(View.GONE);
//                                        File file = new File((String)img.getTag());
//                                        file.delete();
//                                        notifyDataSetChanged();
//                                    }
//                                })
//                                .show();

                        return true;
                    }
                });



            }
        }
    }



    // Pass in the contact array into the constructor
    public ImgsAdapter(Context context, ArrayList<File> imgs) {
        mImgs = imgs;
        mContext = context;

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mStatusBarHeight = (int)Math.ceil(25 * displayMetrics.density);
        mWidthPixels = displayMetrics.widthPixels;
        mHeightPixels = displayMetrics.heightPixels;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    public PhotoViewAttacher getPhotoViewAttacher() {
        return mAttacher;
    }

    public Bitmap getCroppedImage(ImageView photoView){
        photoView.setDrawingCacheEnabled(true);
        Bitmap cropped = Bitmap.createBitmap(photoView.getDrawingCache());

        return cropped;
    }

    private class NetCallback implements CallBack{

        @Override
        public int call(InputStream result) {
            if(result == null){
                //op failed
                //TODO:fill with error handling
            }
            //TODO:put anything that needs to happen after receiving the image here
            return 0;
        }
    }
}




