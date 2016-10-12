package com.example.viner.erosion;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by Viner on 13/08/2016.
 */
class Preview extends SurfaceView implements SurfaceHolder.Callback {

    // SurfaceHolder
    SurfaceHolder mHolder;

    // Our Camera.
    Camera mCamera;

    // Parent Context.
    Context mContext;

    // Camera Sizing (For rotation, orientation changes)
    Camera.Size mPreviewSize;

    // List of supported preview sizes
    List<Camera.Size> mSupportedPreviewSizes;

    // Flash modes supported by this camera
    List<String> mSupportedFlashModes;

    // Supported picture sizes
    List<Camera.Size> mSupportedPictureSizes;

    // View holding this camera.
    View mCameraView;

    public int rotation = 0;

    Preview(Context context, Camera camera, View cameraView) {
        super(context);

        setCamera(camera);
        mCamera = camera;
        mContext = context;
        mCameraView = cameraView;

//        mSurfaceView = new SurfaceView(context);
//        addView(mSurfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
//        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setKeepScreenOn(true);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        mHolder.set
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            if (mCamera != null){
                mCamera.setPreviewDisplay(holder);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null){
            mCamera.stopPreview();
        }
    }

    /**
     * React to surface changed events
     * @param holder
     * @param format
     * @param w
     * @param h
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {

            if (mCamera == null){
                return;
            }
            Camera.Parameters parameters = mCamera.getParameters();


            // Set the auto-focus mode to "continuous"
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            // Preview size must exist.
            if(mPreviewSize != null) {
                Camera.Size previewSize = mPreviewSize;
                parameters.setPreviewSize(previewSize.width, previewSize.height);
                parameters.setPictureSize(previewSize.width, previewSize.height);
            }

            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize =
                    getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }


    /**
     * Update the layout based on rotation and orientation changes.
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
        if (changed) {
            final int width = right - left;
            final int height = bottom - top;

            int previewWidth = width;
            int previewHeight = height;

            if (mPreviewSize != null && mCamera != null){
                Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

                switch (display.getRotation())
                {
                    case Surface.ROTATION_0:
                        previewWidth = mPreviewSize.height;
                        previewHeight = mPreviewSize.width;
                        mCamera.setDisplayOrientation(90);
                        rotation = 90;
                        break;
                    case Surface.ROTATION_90:
                        previewWidth = mPreviewSize.width;
                        previewHeight = mPreviewSize.height;
                        break;
                    case Surface.ROTATION_180:
                        previewWidth = mPreviewSize.height;
                        previewHeight = mPreviewSize.width;
                        break;
                    case Surface.ROTATION_270:
                        previewWidth = mPreviewSize.width;
                        previewHeight = mPreviewSize.height;
                        mCamera.setDisplayOrientation(180);
                        rotation = 270;
                        break;
                }
            }

            mCameraView.layout(0, 0, width, height);

            ((MainActivity)mContext).setLayout();

        }
    }


    /**
     * Extract supported preview and flash modes from the camera.
     * @param camera
     */
    private void setCamera(Camera camera)
    {
        // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
        mCamera = camera;

        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        mSupportedFlashModes = mCamera.getParameters().getSupportedFlashModes();
        mSupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();

        // Set the camera to Auto Flash mode.
        if (mSupportedFlashModes != null && mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)){
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            mCamera.setParameters(parameters);
        }

    }

    /**
     * Begin the preview of the camera input.
     */
    public void startCameraPreview()
    {
        try{
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            Log.v("getCameraInstance", "opened");
        }
        catch (Exception e){
            Log.v("getCameraInstance", e.getMessage());
            // Camera is not available (in use or does not exist)
            Log.d("Ca,eraEx", ""+e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }

}