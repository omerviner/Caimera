package com.example.viner.erosion;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class EffectsActivity extends AppCompatActivity {

    static final int CHOOSE_IMAGE_REQUEST = 1;
    ImgsAdapter mAdapter;
    String mChosenImage;
    Context mContext;
    private RecyclerView rvImgs;
    boolean mProcessingImage;
    public static boolean active;
    public NotificationManager mNotificationManager;
    File cacheDir;
    private String mediaStorageDirPath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getAbsolutePath();
    private File mediaStorageDir;
    private ImageView mImageView;
    public String currentPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.effects);
        mContext = this;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mChosenImage = mediaStorageDirPath + File.separator + "Caimera/caimera_chosen_temp.png";

        mediaStorageDir = new File(mediaStorageDirPath, "Caimera" + File.separator + "styles");

        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }
        cacheDir = new File(getExternalCacheDir() ,"results/");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        View effect_scroller = findViewById(R.id.effectsRelativeLayout);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) effect_scroller.getLayoutParams();
        int statusBarHeight = (int) Math.ceil(25 * displayMetrics.density);

        params.height = displayMetrics.heightPixels - displayMetrics.widthPixels - statusBarHeight;
        effect_scroller.setLayoutParams(params);

        View effect_buttons = findViewById(R.id.btnsRelativeLayout);
        effect_buttons.setLayoutParams(params);

        rvImgs = (RecyclerView) findViewById(R.id.effects);
        rvImgs.setHasFixedSize(true);
        mAdapter = new EffectsAdapter(this, new ArrayList<>(Arrays.asList(mediaStorageDir.listFiles())), rvImgs);
        rvImgs.setAdapter(mAdapter);
        rvImgs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImgs.addOnItemTouchListener(mAdapter.getListener());
    }

    public void onClickChooseEffect(View view) {
        Intent intent = new Intent(this, ChooseImageActivity.class);
        startActivityForResult(intent, CHOOSE_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CHOOSE_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String imgUrl = data.getStringExtra("chosen_image");
                try {
                    File target = File.createTempFile("custom", ".png", mediaStorageDir);
                    new SaveTempImage(new  AddCallback(target), this).execute(null, 0, target, imgUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onClickShareButton(View v) {
        try {
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(currentPath));
            intent.setType("image/png");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
        cleanCache();
    }

    @Override
    public void onResume() {
        super.onResume();
        active = true;
        mNotificationManager.cancelAll();
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
        mNotificationManager.cancelAll();
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        cleanCache();
    }

    private void cleanCache() {
        String[] children = cacheDir.list();
        for (String aChildren : children) {
            Log.d("deleteCache", "" + new File(cacheDir, aChildren).delete());
        }
    }

    class AddCallback  implements Callable<Integer>{
        private File addFile;

        AddCallback(File addFile){
            this.addFile = addFile;
        }
        @Override
        public Integer call() throws Exception {
            mAdapter.mImgs.add(addFile);
            mAdapter.notifyItemInserted(mAdapter.mImgs.size() - 1);
            rvImgs.smoothScrollToPosition(mAdapter.mImgs.size() - 1);
            return null;
        }
    }

}