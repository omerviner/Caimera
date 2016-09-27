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
abstract class ImgsAdapter extends
        RecyclerView.Adapter<ViewHolder> {

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
    abstract public void onBindViewHolder(final ViewHolder viewHolder, final int position);

    @Override
    public int getItemCount() {
        return mImgs.size();
    }


    // Pass in the contact array into the constructor
    public ImgsAdapter(Context context, ArrayList<File> imgs) {

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

    abstract ImageItemClickListener getListener();


}




