package com.example.viner.erosion;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

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
    private ProgressBar loadingIcon;

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

        ImageButton caimeraSign = viewHolder.caimera_sign;
        caimeraSign.setVisibility(View.GONE);
        caimeraSign.setClipToOutline(true);
        caimeraSign.setTag(img.getAbsolutePath());
        curImg.setClipToOutline(true);
        curImg.setTag(img.getAbsolutePath());
        caimeraSign.setTag(img.getAbsolutePath());

        String imgPath = img.getAbsolutePath();
        Log.v("imgPath", imgPath);
        if (imgPath.length() < 4){
            if (imgPath.equals("/e1")){
                Picasso.with(mContext).load(R.drawable.e1).into(curImg);
            } else if (imgPath.equals("/e2")){
                Picasso.with(mContext).load(R.drawable.e2).into(curImg);
            } else if (imgPath.equals("/e3")){
                Picasso.with(mContext).load(R.drawable.e3).into(curImg);
            } else if (imgPath.equals("/e4")){
                Picasso.with(mContext).load(R.drawable.e4).into(curImg);
            } else if (imgPath.equals("/e5")){
                Picasso.with(mContext).load(R.drawable.e5).into(curImg);
            } else if (imgPath.equals("/e6")){
                Picasso.with(mContext).load(R.drawable.e6).into(curImg);
            } else if (imgPath.equals("/e7")){
                Picasso.with(mContext).load(R.drawable.e7).into(curImg);
            } else if (imgPath.equals("/e8")){
                Picasso.with(mContext).load(R.drawable.e8).into(curImg);
            }
            return;
        } else {
            if (img.isDirectory()){
                return;
            }

            Picasso
                    .with(mContext)
                    .load(mImgs.get(position))
                    .resize(150,150)
                    .centerCrop()
                    .into(curImg);

            if (mContext instanceof EffectsActivity){
                caimeraSign.setVisibility(View.VISIBLE);
            }

        }
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
        public ImageButton caimera_sign;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            caimera_sign = (ImageButton)itemView.findViewById(R.id.caimera_sign);
            img = (ImageButton)itemView.findViewById(R.id.imgBtn);

            View.OnClickListener imgButtonOnClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView imageView = (ImageView)v;
                    String imgSrc = (String)imageView.getTag();

                    if (imgSrc.length() < 5){
                        imgSrc = imgSrc.replaceAll("[^\\d.]", "");
                        try {
                            if (((EffectsActivity) mContext).mProcessingImage = true){
                                return;
                            } else {
                                ((EffectsActivity) mContext).mProcessingImage = true;
                            }
                            loadingIcon = (ProgressBar)((EffectsActivity)mContext).findViewById(R.id.spin_kit);
                            DoubleBounce doubleBounce = new DoubleBounce();
                            loadingIcon.setIndeterminateDrawable(doubleBounce);
                            NetInterface.process(new NetCallback(), ((EffectsActivity) mContext).mChosenImage, null, imgSrc);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
//                        ImageView mImageView = (ImageView)((EffectsActivity)mContext).findViewById(R.id.main_image);
                    }
//                    Bitmap bitmap = BitmapFactory.decodeFile(imgSrc);
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
//                            Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
//                            imgPrev.setImageDrawable(drawable);
                            Glide.with(mContext)
                                    .load(imgSrc)
                                    .centerCrop()
                                    .override(1000,1000)
                                    .into(imgPrev);

                            mAttacher = new PhotoViewAttacher(imgPrev);
                            mAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
                            Glide.with(mContext)
                                    .load(imgSrc)
                                    .centerCrop()
                                    .override(1000,1000)
                                    .into(imgPrev);
//                            Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
//                            imgPrev.setImageDrawable(drawable);
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
                        if (((EffectsActivity) mContext).mProcessingImage = true){
                            return;
                        } else {
                            ((EffectsActivity) mContext).mProcessingImage = true;
                        }

                        String imgWithEffect = null;
                        try {
                            loadingIcon = (ProgressBar)((EffectsActivity)mContext).findViewById(R.id.spin_kit);
                            NetInterface.process(new NetCallback(), ((EffectsActivity) mContext).mChosenImage, imgSrc);

                            DoubleBounce doubleBounce = new DoubleBounce();
                            loadingIcon.setIndeterminateDrawable(doubleBounce);
                            loadingIcon.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ImageView mImageView = (ImageView)((EffectsActivity)mContext).findViewById(R.id.main_image);

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
            };

            img.setOnClickListener(imgButtonOnClick);
            caimera_sign.setOnClickListener(imgButtonOnClick);


            if (mContext instanceof EffectsActivity){

                View.OnLongClickListener imgButtonOnLongClick = new View.OnLongClickListener(){

                    @Override
                    public boolean onLongClick(final View v) {
                        if (((String)(v.getTag())).length() < 5){
                            return false;
                        }

                        RelativeLayout parentView = (RelativeLayout)(v.getParent());
                        parentView.setVisibility(RelativeLayout.GONE);
                        parentView.removeAllViews();

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
                };

                img.setOnLongClickListener(imgButtonOnLongClick);
                caimera_sign.setOnLongClickListener(imgButtonOnLongClick);
            }
        }
    }



    // Pass in the contact array into the constructor
    public ImgsAdapter(Context context, ArrayList<File> imgs) {

        if(context instanceof EffectsActivity){

            imgs.add(0, new File("e1"));
            imgs.add(1, new File("e2"));
            imgs.add(2, new File("e3"));
            imgs.add(3, new File("e4"));
            imgs.add(4, new File("e5"));
            imgs.add(5, new File("e6"));
            imgs.add(6, new File("e7"));
            imgs.add(7, new File("e8"));
        }

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
        public int call(final InputStream result) {
            Log.v("NetCallback", "in call function");
            if(result == null){
                //op failed
                Toast.makeText(mContext, "Connection Error. Try Again", Toast.LENGTH_SHORT)
                .show();
                //TODO:fill with error handling
            }
            final Bitmap bmp = BitmapFactory.decodeStream(result);
            final ImageView mImageView = (ImageView)((EffectsActivity)mContext).findViewById(R.id.main_image);
            ((EffectsActivity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mImageView.setImageBitmap(bmp);
                    loadingIcon.setVisibility(View.GONE);

                    ImageButton btn = (ImageButton)((MainActivity)mContext).findViewById(R.id.share);
                    btn.setVisibility(View.VISIBLE);
                    ((EffectsActivity) mContext).mProcessingImage = false;


                }
            });

            //TODO:put anything that needs to happen after receiving the image here
            return 0;
        }
    }

    /**
     * get uri to any resource type
     * @param context - context
     * @param resId - resource id
     * @throws Resources.NotFoundException if the given ID does not exist.
     * @return - Uri to resource by given id
     */

    public static final Uri getUriToResource(@NonNull Context context, @AnyRes int resId) throws Resources.NotFoundException {
        /** Return a Resources instance for your application's package. */
        Resources res = context.getResources();
        /**
         * Creates a Uri which parses the given encoded URI string.
         * @param uriString an RFC 2396-compliant, encoded URI
         * @throws NullPointerException if uriString is null
         * @return Uri for this given uri string
         */
        Uri resUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(resId)
                + '/' + res.getResourceTypeName(resId)
                + '/' + res.getResourceEntryName(resId));
        /** return uri */
        return resUri;
    }



}




