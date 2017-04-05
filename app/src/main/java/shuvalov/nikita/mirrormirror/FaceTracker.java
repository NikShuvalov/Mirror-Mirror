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
        resizeFaceRect();
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

    public void resizeFaceRect(){
        float centerY = mFaceRect.centerY();
        float yDelta = Math.abs(centerY - mFaceRect.top);
        float newTop = centerY - (yDelta * 1.5f);
        float newBottom = centerY + (yDelta * 1.5f);
        mFaceRect.top = newTop;
        mFaceRect.bottom = newBottom;

        float centerX = mFaceRect.centerX();
        float xDelta = Math.abs(centerX - mFaceRect.left);
        float newRight = centerX + (xDelta * 1.25f);
        float newLeft = centerX - (xDelta * 1.25f);
        mFaceRect.right = newRight;
        mFaceRect.left = newLeft;
    }

    public RectF getFaceRect() {
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
