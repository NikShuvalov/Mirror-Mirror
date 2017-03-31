package shuvalov.nikita.mirrormirror;

import android.hardware.Camera;

/**
 * Created by NikitaShuvalov on 3/31/17.
 */

public class FaceTracker {
    private Camera.Face mFace;


    private FaceTracker() {
    }

    private static FaceTracker sFaceTracker;

    public static FaceTracker getInstance() {
        if(sFaceTracker==null){
            sFaceTracker = new FaceTracker();
        }
        return sFaceTracker;
    }

    public void setFace(Camera.Face face){
        mFace = face;
    }

    public Camera.Face getFace() {
        return mFace;
    }
}
