package shuvalov.nikita.mirrormirror.camerafacetracker;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.ArrayList;
import java.util.List;

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
    private PointF mLeftMouth, mRightMouth, mNoseBase, mBottomLip, mRightEye, mLeftEye;
    private Face mFace;
    private boolean mActive;

    private FaceTracker() {
        mDetectionMode = null;
        mMouthOpen = false;
        mActive = true;
    }

    private static FaceTracker sFaceTracker;

    public static FaceTracker getInstance() {
        if(sFaceTracker==null){
            sFaceTracker = new FaceTracker();
        }
        return sFaceTracker;
    }


    private RectF resizeFaceRect(Filter filter){
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

    private void clearFace(){
        mFaceRect = null;
        mLeftMouth = mRightMouth = mNoseBase = mBottomLip = mRightEye = mLeftEye = null;
    }

    private void setNewFacePosition(Face face){
        PointF pos = face.getPosition();
        float faceHeight = face.getHeight();
        float faceWidth = face.getWidth();
        mFaceRect = new RectF(pos.x, pos.y, pos.x+faceWidth, pos.y+faceHeight);
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, mScreenWidth/2, mScreenHeight/2); //Rotates the face detection across center of screen, since Front facing camera has a mirrored display
        if(mDetectionMode!=null && (mDetectionMode.equals(MainActivity.GraphicType.FILTER))){
            applyFilterParameters(matrix); //Uses code to resize the location of the faceRect
        }
        matrix.mapRect(mFaceRect);
    }

    private PointF mirrorLandMark(PointF landmark){
        if(landmark==null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, mScreenWidth/2, mScreenHeight/2);
        float x = landmark.x;
        float y = landmark.y;
        float[] coords = new float[]{x, y};
        matrix.mapPoints(coords);
        landmark.set(coords[0],y);
        return landmark;
    }

    private void applyFilterParameters(Matrix matrix){
        Filter filter = FilterManager.getInstance().getSelectedFilter();
        if(filter!=null) {
            matrix.postTranslate(0, (int) (mFaceRect.height() * filter.getOffsetYPercent()));
            resizeFaceRect(filter);
        }
    }

    public void changeDetectionMode(MainActivity.GraphicType graphicType){
        mDetectionMode = graphicType;
    }

    private void distinguishLandmarks(Face face){
        mLeftEyeOpen = face.getIsLeftEyeOpenProbability()>.70;
        mRightEyeOpen = face.getIsRightEyeOpenProbability()>.70;
        calculateEyeLength(face);
        storeLandmarkPositions(face);
        deduceIfMouthIsOpen(face);
    }

    private void storeLandmarkPositions(Face face){
//        mRightMouth = mirrorLandMark(getLandmarkPosition(face, Landmark.RIGHT_MOUTH));
//        mLeftMouth = mirrorLandMark(getLandmarkPosition(face, Landmark.LEFT_MOUTH));
//        mBottomLip = mirrorLandMark(getLandmarkPosition(face, Landmark.BOTTOM_MOUTH));
//        mNoseBase = mirrorLandMark(getLandmarkPosition(face, Landmark.NOSE_BASE));
//        mRightEye = mirrorLandMark(getLandmarkPosition(face, Landmark.RIGHT_EYE));
//        mLeftEye = mirrorLandMark(getLandmarkPosition(face, Landmark.LEFT_EYE));

        for(Landmark m: face.getLandmarks()){
            switch(m.getType()){
                case (Landmark.RIGHT_MOUTH):
                    mRightMouth = mirrorLandMark(m.getPosition());
                    break;
                case (Landmark.LEFT_MOUTH):
                    mLeftMouth = mirrorLandMark(m.getPosition());
                    break;
                case (Landmark.BOTTOM_MOUTH):
                    mBottomLip = mirrorLandMark(m.getPosition());
                    break;
                case(Landmark.NOSE_BASE):
                    mNoseBase = mirrorLandMark(m.getPosition());
                    break;
                case (Landmark.RIGHT_EYE):
                    mRightEye = mirrorLandMark(m.getPosition());
                    break;
                case(Landmark.LEFT_EYE):
                    mLeftEye = mirrorLandMark(m.getPosition());
                    break;
            }
        }
    }

    //FixMe: Might be better to check if mouth is open when needed rather than always.
    //FixMe: The search here is unnecessary since we're checking for landmarks in another method, but I need to figure out if I want the position to be removed if not detected for a frame.
    private void deduceIfMouthIsOpen(Face face){
        float noseBase = Float.MIN_VALUE;
        float bottomLip = Float.MIN_VALUE;

        PointF noseBasePos = getLandmarkPosition(face, Landmark.NOSE_BASE);
        PointF bottomLipPos = getLandmarkPosition(face, Landmark.BOTTOM_MOUTH);
        if(noseBasePos!=null){
            noseBase = noseBasePos.x;
        }
        if(bottomLipPos !=null){
            bottomLip = bottomLipPos.x;
        }
        if(noseBase!= Float.MIN_VALUE && bottomLip!= Float.MIN_VALUE && mEyelength >0){
            mMouthOpen = Math.abs(bottomLip-noseBase) >mEyelength * 0.75;
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
        PointF leftEyePos = getLandmarkPosition(face, Landmark.LEFT_EYE);
        PointF rightEyePos = getLandmarkPosition(face, Landmark.RIGHT_EYE);
        if(leftEyePos!=null){
            leftEyeCenter = leftEyePos.x;
        }
        if(rightEyePos !=null){
            rightEyeCenter = rightEyePos.x;
        }
        if(leftEyeCenter!= Float.MIN_VALUE && rightEyeCenter != Float.MIN_VALUE){
            mEyelength = Math.abs(rightEyeCenter - leftEyeCenter);
        }else{
            mEyelength = -1;
        }
    }

    private void updateData(Face face){
        mFace = face;
        setNewFacePosition(face);
        distinguishLandmarks(face);
    }


    @Override
    public void onNewItem(int i, Face face) {
        if(mActive) {
            updateData(face);
        }
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        if(mActive) {
            updateData(face);
        }
    }

    @Override
    public void onMissing(Detector.Detections<Face> detections) {
        clearFace();
    }

    @Override
    public void onDone() {
        clearFace();
    }

    public boolean isRightEyeOpen() {
        return mRightEyeOpen;
    }

    public boolean isLeftEyeOpen() {
        return mLeftEyeOpen;
    }

    public boolean isMouthOpen() {
        return mMouthOpen;
    }

    public PointF getLeftMouth() {
        return (mFaceRect!=null) ?
                (mLeftMouth != null) ?
                        mLeftMouth:
                        new PointF((mFaceRect.left*2 + mFaceRect.centerX())/3f,(mFaceRect.bottom*2 + mFaceRect.centerY())/3f)
                : null;
    }

    public PointF getRightMouth() {
        return (mFaceRect!=null) ?
                (mRightMouth !=null) ?
                        mRightMouth:
                        new PointF((mFaceRect.right*2 + mFaceRect.centerX())/3f,(mFaceRect.bottom*2 + mFaceRect.centerY())/3f)
                : null;
    }

    public PointF getNoseBase() {
        return mNoseBase;
    }

    public PointF getBottomLip() {
        return mBottomLip;
    }

    public PointF getRightEye() {
        return (mFaceRect!=null) ?
                (mRightEye != null) ?
                        mRightEye :
                        new PointF((mFaceRect.left*2 + mFaceRect.centerX())/3f, (mFaceRect.centerY() + mFaceRect.top)/2f)
                : null;
    }

    public PointF getLeftEye() {
        return (mFaceRect!=null) ?
                (mLeftEye != null) ?
                        mLeftEye :
                        new PointF((mFaceRect.right*2 + mFaceRect.centerX())/3f,(mFaceRect.centerY() + mFaceRect.top)/2f)
                :null;
    }

    public double getEyelength() {
        return mEyelength;
    }

    public float getLeftEyeOpenProbability(){
        return (mFace!=null) ?  mFace.getIsLeftEyeOpenProbability() : -1;
    }

    public float getRightEyeOpenProbability(){
        return (mFace != null) ?  mFace.getIsRightEyeOpenProbability() : -1;
    }

    public float getEyeballRadius(){
        return (mLeftEye!=null && mRightEye !=null ) ? Math.abs(mLeftEye.x-mRightEye.x)/4f : -1;
    }

    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }
        return null;
    }

    public float getFaceAngle(){
     return (mFace == null) ?
             Float.MIN_VALUE :
             mFace.getEulerY();
    }
    public float getFaceTilt(){
        return (mFace == null) ?
                Float.MIN_VALUE :
                mFace.getEulerZ();
    }

    public void pause(){
        mActive= false;
    }

    public void start(){
        mActive = true;
    }
    public void setActive(boolean b){
        mActive= b;
    }

    public boolean isActive() {
        return mActive;
    }
}
