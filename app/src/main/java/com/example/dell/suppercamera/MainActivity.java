package com.example.dell.suppercamera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private  Camera mCamera;
    private  CameraPreview mCameraPreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        FrameLayout preview = findViewById(R.id.camera_preview);
        initCamera();
        preview.addView(mCameraPreview);
    }

    private void initCamera() {
        boolean b= CameraUtils.checkCameraHardware(this);
        mCamera=CameraUtils.getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
    }


}
