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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
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

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import uk.co.senab.photoview.PhotoViewAttacher;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ImgsAdapter extends
        RecyclerView.Adapter<ImgsAdapter.ViewHolder> {

    private static final int PRESET_STYLES_NUM = 8;
    // Store a member variable for the images
    public ArrayList<File> mImgs;
//    private ArrayList<File> mImgs;
    // Store the context for easy access
    private Context mContext;

//    private PhotoViewAttacher mAttacher;
protected int mStatusBarHeight;
    protected int mWidthPixels;
    private int mHeightPixels;
    private SpinKitView loadingIcon;
    private static HashMap<String, Integer> presetMap = new HashMap<>();
    static{
        presetMap.put("/e1",R.drawable.e1);
        presetMap.put("/e2",R.drawable.e2);
        presetMap.put("/e3",R.drawable.e3);
        presetMap.put("/e4",R.drawable.e4);
        presetMap.put("/e5",R.drawable.e5);
        presetMap.put("/e6",R.drawable.e6);
        presetMap.put("/e7",R.drawable.e7);
    }

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

        final ImageButton caimeraSign = viewHolder.caimera_sign;
        caimeraSign.setVisibility(View.GONE);
        caimeraSign.setClipToOutline(true);
        caimeraSign.setTag(img.getAbsolutePath());
        curImg.setClipToOutline(true);
        curImg.setTag(img.getAbsolutePath());
        caimeraSign.setTag(img.getAbsolutePath());

        String imgPath = img.getAbsolutePath();
        Log.v("imgPath", imgPath);

        if(img.isDirectory()){
            return;
        }
        else if (position < 8  && mContext instanceof EffectsActivity){
            Picasso.with(mContext).load(presetMap.get(imgPath)).into(curImg);
        }
        else {
            Picasso
                    .with(mContext)
                    .load(mImgs.get(position))
                    .resize(150,150)
                    .centerCrop()
                    .into(curImg);

            if (mContext instanceof EffectsActivity){
                caimeraSign.setVisibility(View.VISIBLE);

                View.OnLongClickListener onLongClick = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String src = (String)caimeraSign.getTag();
                        Log.d("ImgsAdapter", "LOngClick");

                        if (src.length() < 5){
                            return false;
                        }
                        File img = mImgs.remove(position);
                        img.delete();
                        notifyDataSetChanged();
                        return false;
                    }
                };
                viewHolder.img.setOnLongClickListener(onLongClick);
                viewHolder.caimera_sign.setOnLongClickListener(onLongClick);


            }
        }
        View.OnClickListener imgButtonOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView)v;
                String imgSrc = (String)imageView.getTag();
                Log.d("CHANGE", String.valueOf(position));



                if (mContext instanceof MainActivity) {
                    mainOnClick(v);
                }
                if (mContext instanceof EffectsActivity){
                    if (position < PRESET_STYLES_NUM){
                        Log.d("CHOSSESTYLE","PRESET : " + ((EffectsActivity) mContext).mChosenStyle);
                        try {
                            if (((EffectsActivity) mContext).mProcessingImage){
                                return;
                            } else {
                                ((EffectsActivity) mContext).mProcessingImage = true;
                            }
                            Log.d("CHOSSESTYLE","ABOUT TO SEND");
                            loadingIcon = (SpinKitView) ((EffectsActivity)mContext).findViewById(R.id.spin_kit);
                            loadingIcon.setVisibility(View.VISIBLE);
                            NetInterface.process(new NetCallback(), ((EffectsActivity) mContext).mChosenImage, null, String.valueOf(position));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    if (((EffectsActivity) mContext).mProcessingImage){
                        return;
                    } else {
                        ((EffectsActivity) mContext).mProcessingImage = true;
                    }

                    String imgWithEffect = null;
                    try {
                        loadingIcon = (SpinKitView) ((EffectsActivity)mContext).findViewById(R.id.spin_kit);
                        NetInterface.process(new NetCallback(), ((EffectsActivity) mContext).mChosenImage, imgSrc, String.valueOf(position));
                        loadingIcon.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ImageView mImageView = (ImageView)((EffectsActivity)mContext).findViewById(R.id.main_image);


                } else if (mContext instanceof ChooseImageActivity) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("chosen_image", imgSrc);
                    ((ChooseImageActivity)mContext).setResult(Activity.RESULT_OK, returnIntent);
                    ((ChooseImageActivity)mContext).finish();
                }
            }
        };


//            img.setOnLongClickListener(imgButtonOnLongClick);
        viewHolder.img.setOnClickListener(imgButtonOnClick);
        viewHolder.caimera_sign.setOnClickListener(imgButtonOnClick);
    }

    private void mainOnClick(View v) {
        ImageView imageView = (ImageView)v;
        String imgSrc = (String)imageView.getTag();

        ImageButton btn = (ImageButton)((MainActivity)mContext).findViewById(R.id.next);
        btn.setVisibility(View.VISIBLE);

        ImageView imgPrev = (ImageView)((MainActivity)mContext).findViewById(R.id.main_image_frame);
        if (imgPrev != null){
            Glide.with(mContext)
                    .load(imgSrc)
                    .centerCrop()
                    .override(1000,1000)
                    .into(imgPrev);


        } else {
            ((MainActivity) mContext).releaseCameraAndPreview();
            FrameLayout preview = (FrameLayout)((MainActivity)mContext).findViewById(R.id.camera_preview);
            preview.removeAllViews();
            imgPrev = new ImageView(mContext);
            imgPrev.setId(R.id.main_image_frame);

            // Set the Drawable displayed
            //////////////
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
            public int call(final Bitmap bmp, final String styleNum) {
            final EffectsActivity activity = (EffectsActivity) mContext;
            Log.v("NetCallback", "in call function");
            if(bmp == null){
                //op failed
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Connection Error. Try Again", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                return -1;
                //TODO:fill with error handling
            }
            final ImageView mImageView = (ImageView)(activity).findViewById(R.id.main_image);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mImageView.setImageBitmap(bmp);
                    loadingIcon.setVisibility(View.GONE);
                    ImageButton btn = (ImageButton)(activity.findViewById(R.id.share));
                    btn.setVisibility(View.VISIBLE);
                    activity.mProcessingImage = false;
                    //TODO: implement cache
//                    new AsyncTask<Void,Void,Void>(){
//                        @Override
//                        protected Void doInBackground(Void... params) {
//                            File file = new File(Environment.getExternalStoragePublicDirectory(
//                                    Environment.DIRECTORY_PICTURES) ,"Caimera/results/" + styleNum);
//                            OutputStream os = null;
//
//                            try {
//                                os = new FileOutputStream(file);
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            }
//
//                            bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
//                            return null;
//                        }
//                    }.execute();
                }
            });

            if(!EffectsActivity.active){
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




