package com.example.viner.erosion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import okhttp3.*;

import java.io.*;
import java.util.concurrent.TimeUnit;


/**
 * Created by omer on 21/09/2016.
 */

public class NetInterface {
    private static final String TAG = "NetInterface-------";
    private static final String BASE_URL = "http://54.158.35.104:3000/api/";//TODO:changed
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_GIF = MediaType.parse("image/gif");
    private static final String presetExtension = "presets/", newExtension = "process/";
    private static final int CALLBACK = 0, CONTENT_PATH = 1, STYLE_PATH = 2, STYLE_NUM = 3, CONTEXT = 4,
            QUICK_STYLE_NUM = 8;


    public static void process(final Object... args) throws Exception {
        final String styleNum = (String)args[STYLE_NUM];
        final CallBack callback = (CallBack)args[CALLBACK];
        final Context mContext = (Context)args[CONTEXT];
        final File resultCacheFile = new File(mContext.getExternalCacheDir(), "results/" + styleNum + ".png");

        if(resultCacheFile.exists()){
            callback.call(BitmapFactory.decodeFile(resultCacheFile.getPath()), styleNum);
            return;
        }
        String contentPath = (String)args[CONTENT_PATH];
        String uploadUrl = BASE_URL + newExtension;
        RequestBody requestBody;


        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.MINUTES)
                .writeTimeout(20, TimeUnit.MINUTES)
                .readTimeout(20, TimeUnit.MINUTES)
                .build();

        //divide to either preset or custom style
        if(Integer.parseInt(styleNum) < QUICK_STYLE_NUM) {
            requestBody = buildBodyBase(contentPath).addFormDataPart("model", styleNum).build();
            uploadUrl = BASE_URL + presetExtension;
        }else{
            String stylePath = (String)args[STYLE_PATH];
            requestBody = buildBodyBase(contentPath).addFormDataPart("style", "style.png",
                    RequestBody.create(MEDIA_TYPE_PNG, new File(stylePath))).build();
        }

        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build();
        Log.d(TAG, "preSend");
        //perform the request async.
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(e.getMessage() != null) Log.d(TAG, e.getMessage());
                callback.call(null, null);

            }

            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bitmap im = null;
                try {
                    Log.d(TAG, "RESPONSE");
                    im = BitmapFactory.decodeStream(response.body().byteStream());
                    callback.call(im, styleNum);
                    //cache Effect result

                } catch (Exception e) {
                    e.printStackTrace();
                    callback.call(null, null);
                }
                response.close();

                //cache the result.
                if(im != null) {
                    cacheResult(im, resultCacheFile);
                }
                ((EffectsActivity)mContext).currentPath = resultCacheFile.getPath();
            }
        });
    }

    public static MultipartBody.Builder buildBodyBase(String contentPath){
        MediaType mediaType = contentPath.endsWith("gif") ? MEDIA_TYPE_GIF : MEDIA_TYPE_PNG;//TODO:take another look at the filetype vs mediatype
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("content", "content.png", RequestBody.create(mediaType, new File(contentPath)));
    }

    private static void cacheResult(Bitmap im, File target){
        OutputStream os = null;
        try {
            os = new FileOutputStream(target);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        im.compress(Bitmap.CompressFormat.PNG, 100, os);
    }
}
