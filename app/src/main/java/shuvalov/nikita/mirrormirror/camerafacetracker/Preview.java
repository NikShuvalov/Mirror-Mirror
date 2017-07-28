package shuvalov.nikita.mirrormirror.camerafacetracker;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;

import java.io.IOException;


/**
 * Created by NikitaShuvalov on 3/24/17.
 */

public class Preview extends SurfaceView implements SurfaceHolder.Callback {
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
        getHolder().addCallback(this);
    }

    //Only if we created the view without using the above constructor
    public void setCameraSource(CameraSource cameraSource){
        mCameraSource = cameraSource;
        getHolder().addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCameraSource.start(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
