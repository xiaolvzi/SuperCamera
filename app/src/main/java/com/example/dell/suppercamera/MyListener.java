package com.example.dell.suppercamera;

import android.view.View;

public class MyListener implements View.OnClickListener {
    private CameraPreview mCameraPreview;
    public MyListener(CameraPreview cameraPreview) {
        this.mCameraPreview=cameraPreview;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id){

            case  R.id.button_capture:

                break;
            case R.id.button_reverse:

                mCameraPreview.reverserCamera();
                break;
        }
    }
}
