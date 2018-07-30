package com.example.dell.suppercamera;

import android.content.Context;
import android.hardware.Camera;
import android.nfc.Tag;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback,IReverseCamera {
    private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context mContext;
    private int currentCameraId=-1;//front or back Camera

    public Camera getCamera(){

        return mCamera;
    }

    public int getCurrentCameraId(){

        return currentCameraId;
    }
    public void setCurrentCameraId(int id){
        this.currentCameraId=id;
    }
    public CameraPreview(Context context) {
        super(context);
        this.mContext=context;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("lv","surfaceCreated");

        int numberOfCameras = Camera.getNumberOfCameras();
        if (mCamera!=null&&currentCameraId!=-1){
            mCamera=Camera.open(currentCameraId);
        }else {
            if (numberOfCameras == 2) {
                mCamera=Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                currentCameraId=Camera.CameraInfo.CAMERA_FACING_FRONT;
            }else {
                mCamera=Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                currentCameraId=Camera.CameraInfo.CAMERA_FACING_BACK;
            }
        }

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "Cannot start preview", e);
        }

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        Log.e("lv","surfaceChanged");
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        rotatePreview();


        // start preview with new settings

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "Cannot start preview", e);
        }
    }

    private void rotatePreview() {

        Camera.Parameters parameters = mCamera.getParameters();

        List<Camera.Size> allSizes = parameters.getSupportedPictureSizes();
        Camera.Size size = allSizes.get(0); // get top size
        for (int i = 0; i < allSizes.size(); i++) {
            if (allSizes.get(i).width > size.width)
                size = allSizes.get(i);
        }

        parameters.setPictureSize(size.width, size.height);

        Display display = ((WindowManager)mContext.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        if(display.getRotation() == Surface.ROTATION_0) {
            mCamera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_270) {

            mCamera.setDisplayOrientation(180);
        }

        mCamera.setParameters(parameters);
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        Log.e("lv","surfaceDestroyed");
        mCamera.stopPreview();
        mCamera.release();
    }


    @Override
    public void reverserCamera() {
        mCamera.stopPreview();
        mCamera.release();

        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            this.setCurrentCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }else if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT){
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            this.setCurrentCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        try {
            rotatePreview();
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
