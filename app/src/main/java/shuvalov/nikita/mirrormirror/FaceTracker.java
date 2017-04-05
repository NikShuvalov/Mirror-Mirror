package shuvalov.nikita.mirrormirror;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.Log;

/**
 * Created by NikitaShuvalov on 3/31/17.
 */


//ToDO: Add a variable to hold the position above the head?
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

    public void setOnTopOfHead(Camera.Face face){
        mFace = face;
        mFaceRect = new RectF(mFace.rect);
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(90);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale((mXOffset*2)/ 2000f, (mYOffset*2) / 2000f);
        matrix.postTranslate(mXOffset, mYOffset-(int)(mFace.rect.height()*0.75));
        matrix.mapRect(mFaceRect);
    }

    public void setOnHair(Camera.Face face){
        mFace = face;
        mFaceRect = new RectF(mFace.rect);
        float centerY = mFaceRect.centerY();
        float delta = Math.abs(centerY - mFaceRect.top);
        float newTop = centerY-(delta*1.5f);
        float newBottom = centerY+(delta*1.5f);

        mFaceRect.top = newTop;
        mFaceRect.bottom = newBottom;

        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(90);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale((mXOffset*2)/ 2000f, (mYOffset*2) / 2000f);
        matrix.postTranslate(mXOffset, mYOffset-(int)(mFace.rect.height()*0.50));
        matrix.mapRect(mFaceRect);
    }

    public RectF getFaceRect() {
//        float x = mFaceRect.centerX();
//        float y = mFaceRect.centerY();
//        float width = mFaceRect.width();
//        float height = mFaceRect.height();

//        mFaceRect.set
        return mFaceRect;
    }

    //Takes into account that the camera driver coordinates have 0,0 at center of screen.
    public void setScreenOffset(int xOffSet, int yOffSet){
        mXOffset = xOffSet;
        mYOffset = yOffSet;
    }

    public Point[] getEyes(){
        Point rEye = mFace.mouth;
        if(rEye== null){
            Log.d("FaceTracker", "getEyes: Not Supported"+ mFace.id);
        }
        Point lEye = mFace.leftEye;
        return new Point[]{lEye, rEye};
    }

    public void clearFace(){
        mFace = null;
        mFaceRect = null;
    }


}
