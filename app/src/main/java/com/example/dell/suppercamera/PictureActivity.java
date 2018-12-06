package com.example.dell.suppercamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PictureActivity extends AppCompatActivity {
    private static final String TAG = "PictureActivity";
    private ImageView mOriginImage,mDestImage;
    private Bitmap mOriginBitmap,mDestBitmap;
    private  Mat mOriginMat,mDestMat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        intView();
        initData();
    }

    private void intView() {
        mOriginImage = findViewById(R.id.origin_image);
        mDestImage = findViewById(R.id.dest_image);

    }

    private void initData() {
        File file = new File(getCacheDir(), "text.jpg");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        mOriginBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
        mOriginImage.setImageBitmap(rotateBitmapByDegree(mOriginBitmap,getBitmapDegree(file.getAbsolutePath())));

    }
    public int getBitmapDegree(String path){
        int degree=0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case 0:
                    Log.e(TAG, "rotate=" + 0);
                    degree=270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    Log.e(TAG, "rotate=" + 90);
                    degree=90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    Log.e(TAG, "rotate=" + 180);
                    degree=180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    Log.e(TAG, "rotate=" + 270);
                    degree=180;
                    break;

            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return degree;
    }

    public Bitmap rotateBitmapByDegree(Bitmap bm,int degree){
        Bitmap returnBm = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError error) {

        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();

        }
        return returnBm;
    }

    public void onGaussianBlur(View view) {
        mOriginMat = new Mat(mOriginBitmap.getHeight(), mOriginBitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(mOriginBitmap,mOriginMat);
        mDestMat=new Mat(mOriginBitmap.getHeight(), mOriginBitmap.getWidth(), CvType.CV_8UC4);
        Imgproc.GaussianBlur(mOriginMat,mDestMat,new Size(3,3),0);
        Utils.matToBitmap(mDestMat,mDestBitmap);
        mDestImage.setImageBitmap(mDestBitmap);
    }
}
