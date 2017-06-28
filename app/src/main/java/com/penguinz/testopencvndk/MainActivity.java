package com.penguinz.testopencvndk;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.penguinz.testopencvndk.Adapter.PageAdapter;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.Highgui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

//CameraBridgeViewBase.CvCameraViewListener2
//SurfaceHolder.Callback , Camera.PreviewCallback
public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback , Camera.PreviewCallback {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
//        tabLayout.addTab(tabLayout.newTab().setText("Grayscale"));
//        tabLayout.addTab(tabLayout.newTab().setText("Color"));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//
//        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
//        final PageAdapter adapter = new PageAdapter
//                (getSupportFragmentManager(), tabLayout.getTabCount());
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//    }

    private static final String TAG = "MainActivity";
    public Mat mRgba, mGray, mYuv;
    Camera mCamera;
    SurfaceView mPreview;
    public int height,width, mode = 0, max_mode = 1;
    public String str_mode[];
    private AsyncTask mTask;

    static{
        System.loadLibrary("MyOpencvLibs");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CameraSystem", "onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);


        mPreview = (SurfaceView)findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        str_mode = new String[2];
        str_mode[0] = "Grayscale";
        str_mode[1] = "Color";

        final TextView tvMode = (TextView)findViewById(R.id.tvMode);
        tvMode.setText(str_mode[mode]);
        mPreview.setOnTouchListener(new onSwipeListener(MainActivity.this) {
            public void onSwipeTop() {
                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
                if(mode != max_mode){
                    mode = mode + 1;
                    tvMode.setText(str_mode[mode]);
                }
            }
            public void onSwipeBottom() {
                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                if(mode != 0){
                    mode = mode - 1;
                    tvMode.setText(str_mode[mode]);
                }
            }

        });

        final TextView result_text = (TextView)findViewById(R.id.result_text);


    }

    @Override
    protected void onPause() {
        Log.d("CameraSystem", "onPause");
        super.onPause();
        mCamera.setPreviewCallback(null);
        mCamera.release();
        // stop asynctask
        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED){
            mTask.cancel(true);
        }
        surfaceDestroyed(mPreview.getHolder());

    }

    @Override
    protected void onDestroy() {
        Log.d("CameraSystem", "onDestroy");
        super.onDestroy();
        RelativeLayout main = (RelativeLayout)findViewById(R.id.main_layout);
        main.removeView(mPreview);
        if(mCamera != null){
            mCamera.setPreviewCallback(null);
            mCamera.release();
        }
    }

    @Override
    protected void onResume() {
        Log.d("CameraSystem", "onResume");

        super.onResume();
        mPreview = (SurfaceView)findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if(OpenCVLoader.initDebug()){
            Log.i(TAG,"Opencv loaded successfully");
            mCamera = Camera.open();

            Log.d("CameraSystem","surfaceCreated");
            try {
                mCamera.setPreviewDisplay(mPreview.getHolder());
                mCamera.setDisplayOrientation(90);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("CameraSystem","surfaceChanged");
            Camera.Parameters params = mCamera.getParameters();
            Camera.Size size = params.getPreviewSize();
            this.height = size.height;
            this.width = size.width;
            mCamera.setParameters(params);
            mCamera.setPreviewCallback(this);
            try {
                mCamera.setPreviewDisplay(mPreview.getHolder());
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else{
            Log.i(TAG,"Opencv not loaded");
        }

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("CameraSystem","surfaceCreated");
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
            mCamera.setDisplayOrientation(90);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("CameraSystem","surfaceChanged");
        Camera.Parameters params = mCamera.getParameters();
        Camera.Size size = params.getPreviewSize();
        this.height = size.height;
        this.width = size.width;
//        List<Camera.Size> previewSize = params.getSupportedPreviewSizes();
//        List<Camera.Size> pictureSize = params.getSupportedPictureSizes();
//        params.setPictureSize(pictureSize.get(0).width,pictureSize.get(0).height);
//        params.setPreviewSize(previewSize.get(0).width,previewSize.get(0).height);
//        params.setJpegQuality(100);
        mCamera.setParameters(params);
        mCamera.setPreviewCallback(this);
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("CameraSystem","surfaceDestroyed");
        mRgba.release();
        mGray.release();
    }

    @Override
    public void onPreviewFrame(byte[] arg0, Camera camera) {
        Log.d("Camera", "onPreviewFrame");
        if( mYuv != null ) {
            mYuv.release();
        }

        // Initial Mat YUV(from android camera)
        mYuv = new Mat( height+height/2, width, CvType.CV_8UC1 );
        mYuv.put( 0, 0, arg0);

        // Initial Mat RGBA image
        mRgba = new Mat();

        //Convert YUV to RGBA mat image
        Imgproc.cvtColor( mYuv, mRgba, Imgproc.COLOR_YUV2RGBA_NV21, 4 );

        // Rotate mat image by 90 degree
        Core.transpose(mRgba, mRgba);
        Core.flip(mRgba, mRgba, 1);

        // Initial Mat grayscale image
        mGray = new Mat( mRgba.rows(), mRgba.cols(), CvType.CV_8UC1 );

        // Call Native Class
        con_opencv(mode);
    }

    public void con_opencv(final int mode){
        mTask = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                // Call Native Class
                if(mode == 0){
                    Log.d("doInBackground","Mode = "+mode);
                    OpencvNativeClass.convertGray(mRgba.getNativeObjAddr(), mGray.getNativeObjAddr());
                }
                else if(mode == 1){
                    Log.d("doInBackground","Mode = "+mode);
                    OpencvNativeClass.donothing(mRgba.getNativeObjAddr(), mGray.getNativeObjAddr());
                }
                return 1;
            }

            @Override
            protected void onPostExecute(Integer result) {
//                super.onPostExecute(aVoid);
                if(result == 1){
                    // Initial bitmap
                    Bitmap bitmap = Bitmap.createBitmap(mGray.cols(), mGray.rows(), Bitmap.Config.ARGB_8888);

                    // Convert Mat image to bitmap for display result
                    Utils.matToBitmap(mGray, bitmap);

                    // Display result on imageview
                    ImageView iv = (ImageView) findViewById(R.id.result_image);
                    iv.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }


}
