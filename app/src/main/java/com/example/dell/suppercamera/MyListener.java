package com.example.dell.suppercamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyListener implements View.OnClickListener {
    private static final String TAG = "MyListener";
    private CameraPreview mCameraPreview;
    private Camera mCamera;
    private Context mContext;

    public MyListener(CameraPreview cameraPreview, Context context) {
        this.mCameraPreview = cameraPreview;
        this.mCamera= mCameraPreview.getCamera();
        this.mContext=context;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {

            case R.id.button_capture:
                mCameraPreview.takePicture();
                break;
            case R.id.button_reverse:

                mCameraPreview.reverserCamera();
                break;
        }
    }

}
