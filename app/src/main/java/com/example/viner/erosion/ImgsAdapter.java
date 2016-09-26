package com.example.viner.erosion;

import android.annotation.SuppressLint;
import android.app.*;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
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

//    private PhotoViewAttacher mAttacher;
    private int mStatusBarHeight;
    private int mWidthPixels;
    private int mHeightPixels;
    private SpinKitView loadingIcon;


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
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
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
        public ViewHolder(final View itemView) {
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
                        Log.d("CHOSSESTYLE","PRESET : " + ((EffectsActivity) mContext).mProcessingImage);
                        imgSrc = imgSrc.replaceAll("[^\\d.]", "");
                        try {
                            if (((EffectsActivity) mContext).mProcessingImage){
                                return;
                            } else {
                                ((EffectsActivity) mContext).mProcessingImage = true;
                            }
                            Log.d("CHOSSESTYLE","ABOUT TO SEND");
                            loadingIcon = (SpinKitView) ((EffectsActivity)mContext).findViewById(R.id.spin_kit);
                            loadingIcon.setVisibility(View.VISIBLE);
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

//                            mAttacher = new PhotoViewAttacher(imgPrev);
//                            mAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                            mAttacher.setDisplayMatrix(new Matrix());

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
                                    .override(1000,1000)
                                    .centerCrop()
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
//                            mAttacher = new PhotoViewAttacher(imgPrev);
//                            mAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                            mAttacher.getDisplayMatrix(new Matrix());

//                        ImageView imgPrev = new ImageView(mContext);
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
                        if (((EffectsActivity) mContext).mProcessingImage == true){
                            return;
                        } else {
                            ((EffectsActivity) mContext).mProcessingImage = true;
                        }

                        String imgWithEffect = null;
                        try {
                            loadingIcon = (SpinKitView) ((EffectsActivity)mContext).findViewById(R.id.spin_kit);
                            NetInterface.process(new NetCallback(), ((EffectsActivity) mContext).mChosenImage, imgSrc);
                            loadingIcon.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ImageView mImageView = (ImageView)((EffectsActivity)mContext).findViewById(R.id.main_image);


                        View.OnLongClickListener imgButtonOnLongClick = new View.OnLongClickListener(){

                            @Override
                            public boolean onLongClick(final View v) {

                                ImageButton caimeraBtn = (ImageButton)v.findViewById(R.id.caimera_sign);
                                String src = (String)caimeraBtn.getTag();

                                if (src.length() < 5){
                                    return false;
                                }

                                mImgs.remove(getPosition()); // 8 ready-made effects

//                        File file = new File(src);
//                        file.delete();



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
                                        notifyDataSetChanged();
//                                    }
//                                })
//                                .show();

                                return true;
                            }
                        };

                        itemView.setOnLongClickListener(imgButtonOnLongClick);


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

//                img.setOnLongClickListener(imgButtonOnLongClick);
//                caimera_sign.setOnLongClickListener(imgButtonOnLongClick);
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

        mStatusBarHeight = getStatusBarHeight();
        mWidthPixels = displayMetrics.widthPixels;
        mHeightPixels = displayMetrics.widthPixels;

    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

//    public PhotoViewAttacher getPhotoViewAttacher() {
//        return mAttacher;
//    }

    public Bitmap getCroppedImage(ImageView photoView){
        photoView.setDrawingCacheEnabled(true);
        Bitmap cropped = Bitmap.createBitmap(photoView.getDrawingCache());

        return cropped;
    }

    private class NetCallback implements CallBack{

        @Override
        public int call(final InputStream result) {
            final EffectsActivity activity = (EffectsActivity) mContext;
            Log.v("NetCallback", "in call function");
            if(result == null){
                //op failed
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Connection Error. Try Again", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

                //TODO:fill with error handling
            }
            final Bitmap bmp = BitmapFactory.decodeStream(result);
            final ImageView mImageView = (ImageView)(activity).findViewById(R.id.main_image);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mImageView.setImageBitmap(bmp);
                    loadingIcon.setVisibility(View.GONE);

                    ImageButton btn = (ImageButton)(activity.findViewById(R.id.share));
                    btn.setVisibility(View.VISIBLE);
                    activity.mProcessingImage = false;
                }
            });
            if(!activity.active){
                int color = ContextCompat.getColor(mContext, R.color.light_yellow);
                android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Custom Effect Ready!")
                            .setContentText("Tap to share/try more")
                        .setColor(color);
                Intent resultIntent = new Intent(activity, EffectsActivity.class);
                //Set flags to resume and not create a new instance
                Notification notification = mBuilder.build();
                notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext.getApplicationContext(), (int)System.currentTimeMillis(), resultIntent, 0);
                mBuilder.setContentIntent(resultPendingIntent);

                // mId allows you to update the notification later on.
                activity.mNotificationManager.notify(0, mBuilder.build());
            }

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

    @SuppressLint("NewApi")
    private int getStatusBarHeight() {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            ((Activity)mContext).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }


}




