package com.example.dell.suppercamera;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private  CameraPreview mCameraPreview;
    private Button mButtonCapture,mButtonReverse;
    private BaseLoaderCallback mLoaderCallback= new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.e(TAG, "OpenCV init success");
                    break;
                    default:
                    super.onManagerConnected(status);
                        break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("lv","onCreate");
        setContentView(R.layout.activity_main);


        initOpenCV();
        initCamera();

        hideNavigationBar();


    }

    private void initOpenCV() {

        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV init fail");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.e(TAG, "OpenCV init success");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }


    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }


    private void initCamera() {
        int tempId;
        FrameLayout preview = findViewById(R.id.camera_preview);

        boolean b= CameraUtils.checkCameraHardware(this);
        if (b) {

            mCameraPreview = new CameraPreview(this);
        }else {
            Toast.makeText(this,"there is no camera in your phone",Toast.LENGTH_SHORT).show();
        }

        preview.addView(mCameraPreview);
        //Add camera operation bar under the preview
        LinearLayout cameraOptionBar = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.camera_option_bar, null);
        cameraOptionBar.setGravity(Gravity.BOTTOM);
        preview.addView(cameraOptionBar);

        initView(cameraOptionBar);


    }

    private void initListener() {
        MyListener myListener = new MyListener(mCameraPreview,this);
        mButtonCapture.setOnClickListener(myListener);
        mButtonReverse.setOnClickListener(myListener);
    }

    private void initView(LinearLayout cameraOptionBar) {
        mButtonCapture = cameraOptionBar.findViewById(R.id.button_capture);
        mButtonReverse = cameraOptionBar.findViewById(R.id.button_reverse);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > 19) {
            updateUI();
        }
        initListener();
        Log.e("lv","onResume");
    }
    @SuppressLint("NewAPi")
    public void updateUI() {
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

                }
            }
        });



    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("lv","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("lv","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("lv","onDestroy");
    }
}
