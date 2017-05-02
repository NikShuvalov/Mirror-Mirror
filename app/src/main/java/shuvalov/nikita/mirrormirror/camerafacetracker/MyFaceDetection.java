package shuvalov.nikita.mirrormirror.camerafacetracker;

import android.hardware.Camera;

import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.filters.FilterManager;

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
            }else{
                f.clearFace();
            }
        }else{
            f.clearFace();
        }
    }
}
