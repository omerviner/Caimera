package com.example.viner.erosion;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class EffectsAdapter extends ImgsAdapter {
    private static final int QUICK_STYLES_NUM = 8;
    private SpinKitView loadingIcon;
    private List<String> presets = Arrays.asList("/e1", "/e2", "/e3", "/e4", "/e5", "/e6", "/e7", "/e8");
    private EffectsActivity mContext;
    private static HashMap<String, Integer> presetMap = new HashMap<>();
    static{
        presetMap.put("/e1", R.drawable.e1);
        presetMap.put("/e2", R.drawable.e2);
        presetMap.put("/e3", R.drawable.e3);
        presetMap.put("/e4", R.drawable.e4);
        presetMap.put("/e5", R.drawable.e5);
        presetMap.put("/e6", R.drawable.e6);
        presetMap.put("/e7", R.drawable.e7);
        presetMap.put("/e8", R.drawable.e8);
    }

    public EffectsAdapter(Context context, ArrayList<File> imgs, RecyclerView rv) {
        super(context, imgs, rv);
        for (int i = 0;i < presets.size();  i++) {
            mImgs.add(i, new File(presets.get(i)));
        }
        mContext = (EffectsActivity)context;
        loadingIcon = (SpinKitView) mContext.findViewById(R.id.spin_kit);
    }

    @Override
    ImageItemClickListener getListener() {

        return new ImageItemClickListener(mContext, rv, new ImageItemClickListener.OnItemClickListener() {

            @Override public void onItemClick(View view, int position) {
                String imgSrc = mImgs.get(position).getAbsolutePath();
                Log.d("CHANGE", String.valueOf(position));

                if (position < QUICK_STYLES_NUM){
                    Log.d("CHOSSESTYLE","PRESET : " + mContext.mProcessingImage);
                    try {
                        if (mContext.mProcessingImage){
                            return;
                        } else {
                            mContext.mProcessingImage = true;
                        }
                        Log.d("CHOSSESTYLE","ABOUT TO SEND");
                        loadingIcon = (SpinKitView) mContext.findViewById(R.id.spin_kit);
                        loadingIcon.setVisibility(View.VISIBLE);
                        NetInterface.process(new NetCallback(), mContext.mChosenImage, null, String.valueOf(position));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }

                if (mContext.mProcessingImage){
                    return;
                } else {
                    mContext.mProcessingImage = true;
                }

                try {
                    NetInterface.process(new NetCallback(), mContext.mChosenImage, imgSrc, String.valueOf(position));
                    loadingIcon.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override public void onLongItemClick(View view, int position) {

                Log.v("effect long clicked: ", Integer.toString(position));

                if (position >= QUICK_STYLES_NUM){
                    File img = mImgs.remove(position);
                    img.delete();
                    notifyDataSetChanged();
                }

            }
        });
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
        curImg.setClipToOutline(true);

        String imgPath = img.getAbsolutePath();
        Log.v("imgPath", imgPath);

        if (!img.isDirectory()) {
            if (position < QUICK_STYLES_NUM){
                Glide.with(mContext).load(presetMap.get(imgPath)).into(curImg);
            }
            else {
                Glide
                        .with(mContext)
                        .load(img)
                        .override(150,150)
                        .centerCrop()
                        .into(curImg);

                caimeraSign.setVisibility(View.VISIBLE);
            }
        }
    }

    private class NetCallback implements CallBack{

        @Override
        public int call(final Bitmap bmp, final String styleNum) {
            final EffectsActivity activity = mContext;
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
}
