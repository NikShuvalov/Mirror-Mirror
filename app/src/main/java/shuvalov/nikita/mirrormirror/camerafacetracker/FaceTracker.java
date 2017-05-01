package shuvalov.nikita.mirrormirror.camerafacetracker;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.filters.FilterManager;

/**
 * Created by NikitaShuvalov on 3/31/17.
 */


//ToDO: Add a variable to hold the position above the head?
public class FaceTracker extends Tracker<Face>{
//    private Face mFace;
    private int mScreenWidth, mScreenHeight;
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
//        mFace = face;
//        mFaceRect = new RectF(mFace.rect);
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);

//        // This is the value for android.hardware.Camera.setDisplayOrientation.
//        matrix.postRotate(90);
//        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
//        // UI coordinates range from (0, 0) to (width, height).
//        matrix.postScale((mXOffset*2)/ 2000f, (mYOffset*2) / 2000f);
//        matrix.postTranslate(mXOffset+(int)(mFace.rect.width()*filter.getOffsetXPercent()), mYOffset + (int)(mFace.rect.height()*filter.getOffsetYPercent()));
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
    public void setScreenSize(int screenHeight, int screenWidth){
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
    }



    public void clearFace(){
//        mFace = null;
        mFaceRect = null;
    }

    public void setNewFacePosition(Face face){
//        mFace = face;
        PointF pos = face.getPosition();
        float posX = pos.x;
        float faceHeight = face.getHeight();
        float faceWidth = face.getWidth();
        Log.d("Face", "setNewFacePosition: "+ pos.x + "," + pos.y);

        mFaceRect = new RectF(pos.x, pos.y, pos.x+faceWidth, pos.y+faceHeight);

        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, mScreenWidth/2, mScreenHeight/2);

        matrix.mapRect(mFaceRect);
        Log.d("Face", "setScaledPosition: "+ mFaceRect.left+","+mFaceRect.top);
//        resizeFaceRect(FilterManager.getInstance().getSelectedFilter());
    }

    @Override
    public void onNewItem(int i, Face face) {
        setNewFacePosition(face);
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        setNewFacePosition(face);
    }

    @Override
    public void onMissing(Detector.Detections<Face> detections) {
        clearFace();
    }

    @Override
    public void onDone() {
        clearFace();
    }
}
