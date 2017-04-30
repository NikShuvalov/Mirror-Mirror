package shuvalov.nikita.mirrormirror.camerafacetracker;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

import shuvalov.nikita.mirrormirror.camerafacetracker.MyFaceDetection;

/**
 * Created by NikitaShuvalov on 3/24/17.
 */

public class Preview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mSurfaceHolder;
    CameraSource mCameraSource;


    public Preview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Preview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Preview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public Preview(Context context) {
        super(context);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    //Only if we created the view without using the above constructor
    public void setCameraSource(CameraSource cameraSource){
        mCameraSource = cameraSource;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        MyFaceDetection faceDetector = new MyFaceDetection();
//        mCamera.setFaceDetectionListener(faceDetector);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCameraSource.start(getHolder());
//            startFaceDetection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void startFaceDetection(){
//        // Try starting Face Detection
//        Camera.Parameters params = mCamera.getParameters();
//
//        // start face detection only *after* preview has started
//        if (params.getMaxNumDetectedFaces() > 0){
//            // camera supports face detection, so can start it:
//            mCamera.startFaceDetection();
//        }
//    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

}
