package com.example.dell.suppercamera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.nfc.Tag;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback,IReverseCamera,Camera.PreviewCallback {
    private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context mContext;
    private int currentCameraId=-1;//front or back Camera
    private IOrientationEventListener mIOrientationEventListener;

    public Camera getCamera(){

        return mCamera==null?Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT):mCamera;
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
        mIOrientationEventListener = new IOrientationEventListener(context);

    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("lv","surfaceCreated");
        mIOrientationEventListener.enable();

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
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
            mCamera.cancelAutoFocus();
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
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

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
        mIOrientationEventListener.disable();
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

    public void takePicture() {
        mCamera.takePicture(null, null, mPicture);
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.e(TAG, "Getting output media file");
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.e(TAG, "Error creating output file");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                mContext.startActivity(new Intent(mContext,PictureActivity.class));
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }


        }
    };

    private  File getOutputMediaFile() {
        File path = mContext.getCacheDir();
        return new File(path, "text.jpg");
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {


    }


    public class IOrientationEventListener extends OrientationEventListener {
        public IOrientationEventListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (ORIENTATION_UNKNOWN == orientation) {
                return;
            }
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(currentCameraId, info);
            orientation = (orientation + 45) / 90 * 90;
            int rotation = 0;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                rotation = (info.orientation - orientation + 360) % 360;
            } else {
                rotation = (info.orientation + orientation) % 360;
            }
            if (null != mCamera) {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setRotation(rotation);
                mCamera.setParameters(parameters);
            }
        }
    }

}
