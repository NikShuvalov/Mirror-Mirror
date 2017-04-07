package shuvalov.nikita.mirrormirror.camerafacetracker;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;

import shuvalov.nikita.mirrormirror.filters.Filter;

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

    public void setFace(Camera.Face face, Filter filter){
        mFace = face;
        mFaceRect = new RectF(mFace.rect);
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(90);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale((mXOffset*2)/ 2000f, (mYOffset*2) / 2000f);
        matrix.postTranslate(mXOffset+(int)(mFace.rect.width()*filter.getOffsetXPercent()), mYOffset + (int)(mFace.rect.height()*filter.getOffsetYPercent()));
        matrix.mapRect(mFaceRect);

        resizeFaceRect(filter);
    }

    public void resizeFaceRect(Filter filter){
        float centerY = mFaceRect.centerY();
        float yDelta = Math.abs(centerY - mFaceRect.top);
        float yScale = filter.getScaleY();
        float newTop = centerY - (yDelta * yScale);
        float newBottom = centerY + (yDelta * yScale);
        mFaceRect.top = newTop;
        mFaceRect.bottom = newBottom;

        float centerX = mFaceRect.centerX();
        float xDelta = Math.abs(centerX - mFaceRect.left);
        float xScale = filter.getScaleX();
        float newRight = centerX + (xDelta * xScale);
        float newLeft = centerX - (xDelta * xScale);
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


    public void clearFace(){
        mFace = null;
        mFaceRect = null;
    }


}
