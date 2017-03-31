package shuvalov.nikita.mirrormirror;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by NikitaShuvalov on 3/24/17.
 */

public class MyFaceDetection implements Camera.FaceDetectionListener {
    private Preview mPreview;

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        if (faces.length>0){
            Camera.Face face = faces[0];
            if(face!=null){
                FaceTracker.getInstance().setFace(face);
                Rect rect = face.rect;
                int x = rect.centerX();
                int y = rect.centerY();
                Log.d(TAG, "onFaceDetection: "+ "score: " +face.score + x +","+ y);
                mPreview.drawSomething(Math.abs(x),Math.abs(y));
            }
        }
    }

    public void setPreview(Preview preview) {
        mPreview = preview;
    }
}
