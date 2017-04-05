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

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        FaceTracker f = FaceTracker.getInstance();
        if (faces.length>0){
            Camera.Face face = faces[0];
            if(face!=null){
                Filter filter = FilterManager.getInstance().getSelectedFilter();
                f.setFace(face,filter);
            }else{
                f.clearFace();
            }
        }else{
            f.clearFace();
        }
    }

}
