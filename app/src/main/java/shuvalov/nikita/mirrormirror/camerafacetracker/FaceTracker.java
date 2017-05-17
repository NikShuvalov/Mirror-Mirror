package shuvalov.nikita.mirrormirror.camerafacetracker;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import shuvalov.nikita.mirrormirror.MainActivity;
import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.filters.FilterManager;

/**
 * Created by NikitaShuvalov on 3/31/17.
 */


//ToDO: Add a variable to hold the position above the head?
public class FaceTracker extends Tracker<Face>{
    private int mScreenWidth, mScreenHeight;
    private RectF mFaceRect;
    private MainActivity.GraphicType mDetectionMode;
    private boolean mRightEyeOpen, mLeftEyeOpen, mMouthOpen;
    private double mEyelength;

    private FaceTracker() {
        mDetectionMode = null;
        mMouthOpen = false;
    }

    private static FaceTracker sFaceTracker;

    public static FaceTracker getInstance() {
        if(sFaceTracker==null){
            sFaceTracker = new FaceTracker();
        }
        return sFaceTracker;
    }


    public RectF resizeFaceRect(Filter filter){
        if(mFaceRect!=null) {
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
        return mFaceRect;
    }

    public RectF getFaceRect() {
        return mFaceRect;
    }

    public void setScreenSize(int screenHeight, int screenWidth){
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    public void clearFace(){
        mFaceRect = null;
    }

    public void setNewFacePosition(Face face){
        PointF pos = face.getPosition();
        float faceHeight = face.getHeight();
        float faceWidth = face.getWidth();

        mFaceRect = new RectF(pos.x, pos.y, pos.x+faceWidth, pos.y+faceHeight);

        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, mScreenWidth/2, mScreenHeight/2); //Rotates the face detection across center of screen, since Front facing camera has a mirrored display
        if(mDetectionMode!=null && mDetectionMode.equals(MainActivity.GraphicType.FILTER)){
            applyFilterParameters(matrix);
        }
        matrix.mapRect(mFaceRect);
    }

    private void applyFilterParameters(Matrix matrix){
        Filter filter = FilterManager.getInstance().getSelectedFilter();
        matrix.postTranslate(0, (int)(mFaceRect.height()*filter.getOffsetYPercent()));
        resizeFaceRect(filter);
    }
    public void changeDetectionMode(MainActivity.GraphicType graphicType){
        mDetectionMode = graphicType;
    }

    private void distinguishLandmarks(Face face){
        if(face.getIsLeftEyeOpenProbability()<.30){
            Log.d("Eye", "Left eye closed ");
        }
        if(face.getIsRightEyeOpenProbability()<.30){
            Log.d("Eye", "Right eye closed ");
        }
        calculateEyeLength(face);
        deduceIfMouthIsOpen(face);
    }

    private void deduceIfMouthIsOpen(Face face){
        float noseBase = Float.MIN_VALUE;
        float bottomLip = Float.MAX_VALUE;
        for(Landmark mark: face.getLandmarks()){
            if(mark.getType() == Landmark.NOSE_BASE){
                noseBase = mark.getPosition().y;
            }
            if(mark.getType() == Landmark.BOTTOM_MOUTH){
                bottomLip = mark.getPosition().y;
            }
        }
        if(noseBase!= Float.MIN_VALUE && bottomLip!= Float.MIN_VALUE && mEyelength >0){
            mMouthOpen = Math.abs(bottomLip-noseBase) >mEyelength;
            return;
            //Have there be a counter or something, if there's several detections of an open mouth within a time span, treat as Open Mouth, to reduce probability of a false positive.
        }
        mMouthOpen = false;
    }

    /**
     * The distance between the edges of both eyes is typically a single eye's width.
     * This calculates the distance of an eye by getting the distance between the center of both eyes divided by 2.
     *
     * @param face
     */
    private void calculateEyeLength(Face face){
        float leftEyeCenter = Float.MIN_VALUE;
        float rightEyeCenter = Float.MIN_VALUE;
        for(Landmark mark :face.getLandmarks()){
            if(mark.getType() == Landmark.LEFT_EYE){
                leftEyeCenter = mark.getPosition().x;
            }
            if (mark.getType() == Landmark.RIGHT_EYE){
                rightEyeCenter = mark.getPosition().x;
            }
        }
        if(leftEyeCenter!= Float.MIN_VALUE && rightEyeCenter != Float.MIN_VALUE){
            mEyelength = Math.abs(rightEyeCenter - leftEyeCenter);
        }else{
            mEyelength = -1;
        }
    }

    private void updateData(Face face){
        setNewFacePosition(face);
        distinguishLandmarks(face);
    }


    @Override
    public void onNewItem(int i, Face face) {
        updateData(face);
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        updateData(face);
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
