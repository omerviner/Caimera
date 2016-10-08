package com.example.viner.erosion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
abstract class ImgsAdapter extends
        RecyclerView.Adapter<ViewHolder> {

    // Store a member variable for the images
    protected ArrayList<File> mImgs;
    // Store the context for easy access
    private Context mContext;
    protected RecyclerView rv;
    protected int mStatusBarHeight;
    protected int mWidthPixels;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View imgsView = inflater.inflate(R.layout.item_img, parent, false);

        // Return a new holder instance
        return new ViewHolder(imgsView);

    }

//    @Override
//    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position){};



    @Override
    public int getItemCount() {
        return mImgs.size();
    }


    // Pass in the contact array into the constructor
    public ImgsAdapter(Context context, ArrayList<File> imgs, RecyclerView recyclerView) {

        mImgs = imgs;
        mContext = context;
        rv = recyclerView;

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();

        mStatusBarHeight = getStatusBarHeight();
        mWidthPixels = displayMetrics.widthPixels;

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




