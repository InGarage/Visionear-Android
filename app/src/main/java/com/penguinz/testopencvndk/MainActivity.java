package com.penguinz.testopencvndk;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

//CameraBridgeViewBase.CvCameraViewListener2
//SurfaceHolder.Callback , Camera.PreviewCallback
public class MainActivity extends Activity implements SurfaceHolder.Callback , Camera.PreviewCallback{


    static{
        System.loadLibrary("MyOpencvLibs");
    }

    private static final String TAG = "MainActivity";
    Mat mRgba, mGray, mYuv;

    Camera mCamera;
    SurfaceView mPreview;
    public int height,width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);


        mPreview = (SurfaceView)findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        final TextView result_text = (TextView)findViewById(R.id.result_text);
//        mPreview.setOnTouchListener(new onSwipeListener(MainActivity.this) {
//            public void onSwipeTop() {
////                result_text.setText("Top");
////                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
//            }
//            public void onSwipeRight() {
//
////                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
//            }
//            public void onSwipeLeft() {
////                result_text.setText(String.valueOf(count));
////                if(count > 0){
////                    count = count - 1;
////                }
//
////                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
//            }
//            public void onSwipeBottom() {
////                result_text.setText("Bottom");
////                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.setPreviewCallback(null);
        mCamera.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(OpenCVLoader.initDebug()){
            Log.i(TAG,"Opencv loaded successfully");
            mCamera = Camera.open();
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
        OpencvNativeClass.convertGray(mRgba.getNativeObjAddr(), mGray.getNativeObjAddr());

        // Initial bitmap
        Bitmap bitmap = Bitmap.createBitmap(mGray.cols(), mGray.rows(), Bitmap.Config.ARGB_8888);

        // Convert Mat image to bitmap for display result
        Utils.matToBitmap(mGray, bitmap);

        // Display result on imageview
        ImageView iv = (ImageView) findViewById(R.id.result_image);
        iv.setImageBitmap(bitmap);

//        if(arg0 != null) {
//            final byte[] finalArg = arg0;
//
//            connectOpencv concv = new connectOpencv();
//            if(concv.getStatus() != AsyncTask.Status.RUNNING){
//                int w = mCamera.getParameters().getPreviewSize().width;
//                int h = mCamera.getParameters().getPreviewSize().height;
//                concv.connectOpencv(finalArg, w, h);
////                concv.onPreExecute();
//                concv.doInBackground();
//            }
//        }
    }



//    public class connectOpencv extends AsyncTask<Void, Void, Void> {
//
//        public int[] rgbs;
//
//        public Bitmap bitmap;
//        public int retVal;
//        public int w, h;
//        public byte[] arg;
//
//        public void connectOpencv(final byte[] arg, final int w, final int h) {
//            this.arg = arg;
//            this.w = w;
//            this.h = h;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            System.out.println("w = " + w + " h = " + h);
//            rgbs = new int[w * h];
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            System.out.println("w = " + w + " h = " + h);
//            rgbs = new int[w * h];
//            decodeYUV420(rgbs, rotateYUV420Degree90(arg, w, h), w, h);
//            bitmap = Bitmap.createBitmap(rgbs, h, w, Bitmap.Config.ARGB_8888);
//            Utils.bitmapToMat(bitmap, mRgba);
//            retVal = OpencvNativeClass.convertGray(mRgba.getNativeObjAddr(), mGray.getNativeObjAddr());
//            if(retVal == 1){
//                Log.d("doInBackground","Convert to gray sucessful");
//            }
//
//            return null;
//        }

//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//        }
//
//    }

}
