package shuvalov.nikita.mirrormirror;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;

/**
 * Created by NikitaShuvalov on 3/31/17.
 */

public class FaceTracker {
    private Camera.Face mFace;
    private int mXOffset, mYOffset;
    private RectF mFaceRect;


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
        mFaceRect = new RectF(mFace.rect);
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(90);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale((mXOffset*2)/ 2000f, (mYOffset*2) / 2000f);
        matrix.postTranslate(mXOffset, mYOffset);
        matrix.mapRect(mFaceRect);
    }


    public RectF getFaceRect() {
        return mFaceRect;
    }

    public void setScreenOffset(int xOffSet, int yOffSet){
        mXOffset = xOffSet;
        mYOffset = yOffSet;
    }


}
