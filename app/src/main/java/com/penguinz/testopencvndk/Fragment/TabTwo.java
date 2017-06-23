package com.penguinz.testopencvndk.Fragment;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.penguinz.testopencvndk.OpencvNativeClass;
import com.penguinz.testopencvndk.R;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by PenguinZ on 22/6/2560.
 */

public class TabTwo extends Fragment {

    private static final String TAG = "TabTwo";
    public Mat mRgba, mGray, mYuv;
    Camera mCamera;
    SurfaceView mPreview;
    public int height,width;
    View rootView;

    static{
        System.loadLibrary("MyOpencvLibs");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_one, container, false);

//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//        mPreview = (SurfaceView)rootView.findViewById(R.id.preview1);
//        mPreview.getHolder().addCallback(this);
//        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        return rootView;
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        mCamera.setPreviewCallback(null);
//        mCamera.release();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if(OpenCVLoader.initDebug()){
//            Log.i(TAG,"Opencv loaded successfully");
////            if(mCamera != null){
////                mCamera.release();
////            }
////            mCamera = Camera.open();
//        }
//        else{
//            Log.i(TAG,"Opencv not loaded");
//        }
//    }
//
//    @Override
//    public void onPreviewFrame(byte[] data, Camera camera) {
//        Log.d(TAG+" Camera", "onPreviewFrame");
//        if( mYuv != null ) {
//            mYuv.release();
//        }
//
//        // Initial Mat YUV(from android camera)
//        mYuv = new Mat( height+height/2, width, CvType.CV_8UC1 );
//        mYuv.put( 0, 0, data);
//
//        // Initial Mat RGBA image
//        mRgba = new Mat();
//
//        //Convert YUV to RGBA mat image
//        Imgproc.cvtColor( mYuv, mRgba, Imgproc.COLOR_YUV2RGBA_NV21, 4 );
//
//        // Rotate mat image by 90 degree
//        Core.transpose(mRgba, mRgba);
//        Core.flip(mRgba, mRgba, 1);
//
//        // Initial Mat grayscale image
//        mGray = new Mat( mRgba.rows(), mRgba.cols(), CvType.CV_8UC1 );
//
//        // Call Native Class
//        con_opencv();
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        Log.d(TAG+" CameraSystem","surfaceCreated");
//        try {
//            mCamera.setPreviewDisplay(mPreview.getHolder());
//            mCamera.setDisplayOrientation(90);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        Log.d(TAG+" CameraSystem","surfaceChanged");
//        Camera.Parameters params = mCamera.getParameters();
//        Camera.Size size = params.getPreviewSize();
//        this.height = size.height;
//        this.width = size.width;
//        mCamera.setParameters(params);
//        mCamera.setPreviewCallback(this);
//        try {
//            mCamera.setPreviewDisplay(mPreview.getHolder());
//            mCamera.startPreview();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        mRgba.release();
//        mGray.release();
//    }
//
//    public void con_opencv(){
//        new AsyncTask<Void, Void, Integer>() {
//            @Override
//            protected Integer doInBackground(Void... params) {
//                // Call Native Class
//                OpencvNativeClass.donothing(mRgba.getNativeObjAddr(), mGray.getNativeObjAddr());
//                return 1;
//            }
//
//            @Override
//            protected void onPostExecute(Integer result) {
////                super.onPostExecute(aVoid);
//                if(result == 1){
//                    // Initial bitmap
//                    Bitmap bitmap = Bitmap.createBitmap(mGray.cols(), mGray.rows(), Bitmap.Config.ARGB_8888);
//
//                    // Convert Mat image to bitmap for display result
//                    Utils.matToBitmap(mGray, bitmap);
//
//                    // Display result on imageview
//                    ImageView iv = (ImageView) rootView.findViewById(R.id.result_image1);
//                    iv.setImageBitmap(bitmap);
//                }
//            }
//        }.execute();
//    }
}
