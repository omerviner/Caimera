package com.example.viner.erosion;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import okhttp3.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * Created by omer on 21/09/2016.
 */

public class NetInterface {
    private static final String TAG = "NetInterface-------";
    private static final String BASE_URL = "http://52.54.68.110:3000/api/";//TODO:this is an ex2 elastic ip, check!
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final String presetExtension = "presets/";
    private static final String newExtension = "process/";
    private static final int STYLE_NUM = 3;
    private static final int STYLE_PATH = 2;
    private static final int CONTENT_PATH = 1;
    private static final int BASE_ARG_NUM = STYLE_NUM;



    public static void process(final Object... args) throws Exception {
        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.MINUTES)
                .writeTimeout(20, TimeUnit.MINUTES)
                .readTimeout(20, TimeUnit.MINUTES)
                .build();
        String contentPath = (String)args[CONTENT_PATH];
        String uploadUrl = BASE_URL + newExtension;
        RequestBody requestBody;
        final CallBack callback = (CallBack)args[0];
//        uncomment this to activate only the callback**********************
//        callback.call(null);
        if(args.length > BASE_ARG_NUM) {
            String styleNum = (String)args[STYLE_NUM];
            requestBody = buildBodyBase(contentPath).addFormDataPart("style", styleNum).build();
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

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, e.getMessage());

                callback.call(null);

            }

            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Log.d(TAG, "RESPONSE");
                    callback.call(response.body().byteStream());
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.call(null);
                }
            }
        });
    }

    public static MultipartBody.Builder buildBodyBase(String contentPath){
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("content", "content.png", RequestBody.create(MEDIA_TYPE_PNG, new File(contentPath)));
    }
}
