package shuvalov.nikita.mirrormirror.componentfilters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;


import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;

/**
 * Created by NikitaShuvalov on 5/19/17.
 */

public class RickComponentFilter extends ComponentFilter {
    private Paint mLinePaint, mTextPaint, mEyeBrowPaint, mEyeBallPaint, mPupilPaint, mFacePaint;
    private float mTextSize, mEyeBrowThickness;
    private Bitmap mRickVomit, mRickHair;
    private boolean mFaceRicking;

    private static final String FILTER_NAME = "Rick Sanchez";
    private static final float HAIR_WIDTH_SCALE = 2.1f;
    private static final float HAIR_HEIGHT_SCALE = 1.3f;
    private static final float HAIR_VERTICAL_OFFSET = -0.35f;


    public RickComponentFilter(Context context, Bitmap previewImage) {
        super(FILTER_NAME,previewImage);
        mFaceRicking = true;
        mTextSize = 200f;
        mEyeBrowThickness = 65f;
        createPaints();
        loadBitmaps(context);
    }

    private void createPaints(){
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStrokeWidth(5f);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mEyeBrowPaint = new Paint();
        mEyeBrowPaint.setColor(Color.argb(200, 156, 239, 243));
        mEyeBrowPaint.setStyle(Paint.Style.FILL);
        mEyeBrowPaint.setStrokeWidth(mEyeBrowThickness);

        mEyeBallPaint = new Paint();
        mEyeBallPaint.setColor(Color.WHITE);
        mEyeBallPaint.setStyle(Paint.Style.FILL);

        mPupilPaint = new Paint();
        mPupilPaint.setColor(Color.BLACK);
        mPupilPaint.setStyle(Paint.Style.FILL);

        mFacePaint = new Paint();
        mFacePaint.setColor(Color.argb(255, 250, 242, 242));
        mFacePaint.setStyle(Paint.Style.FILL);
    }

    private void loadBitmaps(Context c){
        mRickHair = BitmapFactory.decodeResource(c.getResources(), R.drawable.rick_hair);
        mRickVomit = BitmapFactory.decodeResource(c.getResources(), R.drawable.rick_vomit);
    }

    @Override
    public void drawFilterToCanvas(Canvas canvas) {
        if(canvas!=null) {
            FaceTracker faceTracker = FaceTracker.getInstance();
            PointF leftEye = faceTracker.getLeftEye();
            PointF rightEye = faceTracker.getRightEye();
            float eyeballRadius = faceTracker.getEyeballRadius() * 1.75f;
            PointF leftMouth = faceTracker.getLeftMouth();
            PointF rightMouth = faceTracker.getRightMouth();
            RectF faceRect = faceTracker.getFaceRect();
            if (faceRect != null) {
                RectF adjustedRect = getAdjustedRect(faceRect);
                adjustEyebrowThickness(adjustedRect);
                canvas.drawBitmap(mRickHair, null, adjustedRect, null);
//                if (mFaceRicking) {
                    drawFace(canvas, faceRect);
                    drawEars(canvas, eyeballRadius, faceRect);
//                }
                if (eyeballRadius >= 0) {
                    drawVomit(canvas, leftMouth, rightMouth, eyeballRadius);
//                    if (mFaceRicking) {
                        drawMouth(canvas, eyeballRadius, leftMouth, rightMouth);
                        drawEyes(canvas, leftEye, rightEye, eyeballRadius);
                        drawNose(canvas, leftEye, rightEye, eyeballRadius);
//                    }
                    drawEyebrows(canvas, eyeballRadius * 1.1, leftEye, rightEye);
                }
            }
            if (!mFaceRicking) {
                drawMessage(canvas, faceTracker.getScreenHeight() * .8f, faceTracker.getScreenWidth());
            }
        }
    }

    @Override
    public void drawMirroredFilterToCanvas(Canvas canvas, Matrix mirrorMatrix) {
        if(canvas!=null) {
            canvas.save();
            canvas.setMatrix(mirrorMatrix);
            FaceTracker faceTracker = FaceTracker.getInstance();
            PointF leftEye = faceTracker.getLeftEye();
            PointF rightEye = faceTracker.getRightEye();
            float eyeballRadius = faceTracker.getEyeballRadius() * 1.75f;
            PointF leftMouth = faceTracker.getLeftMouth();
            PointF rightMouth = faceTracker.getRightMouth();
            RectF faceRect = faceTracker.getFaceRect();
            if (faceRect != null) {
                RectF adjustedRect = getAdjustedRect(faceRect);
                adjustEyebrowThickness(adjustedRect);
                canvas.drawBitmap(mRickHair, null, adjustedRect, null);
                drawFace(canvas, faceRect);
                drawEars(canvas, eyeballRadius, faceRect);
                if (eyeballRadius >= 0) {
                    drawVomit(canvas, leftMouth, rightMouth, eyeballRadius);
                    drawMouth(canvas, eyeballRadius, leftMouth, rightMouth);
                    drawEyes(canvas, leftEye, rightEye, eyeballRadius);
                    drawNose(canvas, leftEye, rightEye, eyeballRadius);
                    drawEyebrows(canvas, eyeballRadius * 1.1, leftEye, rightEye);
                }
                canvas.restore();
            }
        }
    }

    private void drawVomit(Canvas canvas, PointF leftMouth, PointF rightMouth, float eyeballRadius){
        float left = leftMouth.x - eyeballRadius;
        float right = rightMouth.x + eyeballRadius;
        float midY = (leftMouth.y + rightMouth.y) / 2;
        float distance = Math.abs(right - left);
        RectF vomitRect = new RectF(leftMouth.x, midY, rightMouth.x, midY + distance * .6f);
        canvas.drawBitmap(mRickVomit, null, vomitRect, null);
    }

    //FixMe: Mouth detection doesn't trigger nearly accurately enough, but if I do improve it, revisit this code.

//    private void drawMouth(Canvas canvas, PointF leftCorner, PointF rightCorner){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            Path mouthPath = new Path();
//
//            float mouthWidth = Math.abs(leftCorner.x - rightCorner.x);
//            float mouthHeight = mouthWidth/3;
//            float cornerRadius = mouthWidth/4;
//
//            float bottomLeft = leftCorner.y + mouthHeight/2;
//            float topLeft = leftCorner.y - mouthHeight/2;
//            float topRight = rightCorner.y - mouthHeight/2;
//            float bottomRight = rightCorner.y - mouthHeight/2;
//            float midX = (leftCorner.x + rightCorner.x);
//            float topLipY = (topLeft+topRight)/2-mouthHeight/4;
//            float botLipY = (bottomLeft+bottomRight)/2-mouthHeight/4;
//
//
//            mouthPath.moveTo(leftCorner.x, leftCorner.y+mouthHeight/2);
//            mouthPath.arcTo(leftCorner.x-cornerRadius, topLeft, leftCorner.x + cornerRadius, bottomLeft,90, 180, false);
//            mouthPath.arcTo(leftCorner.x,topLipY,rightCorner.x, botLipY, 180, 180,false);
//            mouthPath.arcTo(rightCorner.x - cornerRadius, topRight, rightCorner.x + cornerRadius, bottomRight, 270, 180, false);
//            mouthPath.arcTo(leftCorner.x, botLipY, rightCorner.x, botLipY+mouthHeight, 0, -180, false);
//            canvas.drawPath(mouthPath,mMouthPaint);
//            canvas.drawPath(mouthPath, mLinePaint);
//        }
//    }

    private void drawMouth(Canvas canvas, float eyeballRadius,PointF leftMouth, PointF rightMouth ){
        float left = leftMouth.x - eyeballRadius;
        float right = rightMouth.x + eyeballRadius;
        canvas.drawLine(left, leftMouth.y, right, rightMouth.y, mLinePaint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(left - eyeballRadius/2, leftMouth.y - eyeballRadius/2, left + eyeballRadius/2, leftMouth.y + eyeballRadius/2, 90f, 180f, false, mLinePaint);
            canvas.drawArc(right - eyeballRadius/2, rightMouth.y - eyeballRadius/2, right + eyeballRadius/2, rightMouth.y + eyeballRadius/2, 270f, 180f, false, mLinePaint);
        }
    }

    private void drawMessage(Canvas canvas, float line1Height, float screenWidth){
        canvas.drawText("I stand", screenWidth*.2f, line1Height, mTextPaint);
        canvas.drawText("with Rick!",screenWidth*.1f, line1Height+mTextSize + 16, mTextPaint);
    }

    private void drawNose(Canvas canvas, PointF leftEye, PointF rightEye, float eyeballRadius){
        canvas.drawArc(leftEye.x+eyeballRadius/1.5f, leftEye.y, rightEye.x - eyeballRadius/1.5f, Math.max(leftEye.y, rightEye.y)+ 2*eyeballRadius,0, 180, false, mLinePaint);
    }

    public void drawEars(Canvas canvas, float eyeballRadius, RectF faceRect){
        float midYPoint = (faceRect.top + faceRect.bottom)/2;

        //Draw left ear
        canvas.drawArc(faceRect.left-eyeballRadius/2, midYPoint, faceRect.left+eyeballRadius/2,midYPoint+eyeballRadius, 90, 180, false, mFacePaint);
        canvas.drawArc(faceRect.left-eyeballRadius/2, midYPoint, faceRect.left+eyeballRadius/2,midYPoint+eyeballRadius, 90, 180, true, mLinePaint);

        //Draw  right ear
        canvas.drawArc(faceRect.right-eyeballRadius/2, midYPoint, faceRect.right+eyeballRadius/2,midYPoint+eyeballRadius, 270, 180, false, mFacePaint);
        canvas.drawArc(faceRect.right-eyeballRadius/2, midYPoint, faceRect.right+eyeballRadius/2,midYPoint+eyeballRadius, 270, 180, true, mLinePaint);
    }

    private void drawFace(Canvas canvas, RectF faceRect){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float verticalArcHeight = faceRect.height()/4;
            float faceTop = faceRect.top + verticalArcHeight;
            float faceBot = faceRect.bottom -verticalArcHeight/2;
            Path facePath = new Path();
            facePath.moveTo(faceRect.left, faceTop);
            facePath.arcTo(faceRect.left, faceTop - verticalArcHeight, faceRect.right, faceTop + verticalArcHeight, 180, 180, false);
            facePath.lineTo(faceRect.right, faceBot);
            facePath.arcTo(faceRect.left, faceBot - verticalArcHeight, faceRect.right, faceBot + verticalArcHeight, 0, 180, false);
            facePath.lineTo(faceRect.left, faceTop);
            canvas.drawPath(facePath, mFacePaint);
            canvas.drawPath(facePath, mLinePaint);
            facePath.close();
        }
    }

    private void drawEyes(Canvas canvas, PointF leftEye, PointF rightEye, float eyeballRadius){

        float rightEyeOpenChance = FaceTracker.getInstance().getRightEyeOpenProbability();
        float leftEyeOpenChance = FaceTracker.getInstance().getLeftEyeOpenProbability();

        //Left-Eye draw logic
        if(leftEyeOpenChance > .3f && leftEyeOpenChance<.7) {
            canvas.drawCircle(leftEye.x, leftEye.y, eyeballRadius, mEyeBallPaint);
            canvas.drawArc(leftEye.x - eyeballRadius, leftEye.y - eyeballRadius, leftEye.x + eyeballRadius, leftEye.y + eyeballRadius, 180, 180, true, mFacePaint);
            canvas.drawArc(leftEye.x - eyeballRadius, leftEye.y - eyeballRadius, leftEye.x + eyeballRadius, leftEye.y + eyeballRadius, 180, 180, true, mLinePaint);
            canvas.drawCircle(leftEye.x, leftEye.y+10, 10, mPupilPaint);
        }else if(leftEyeOpenChance<.3f){
            canvas.drawCircle(leftEye.x, leftEye.y, eyeballRadius, mFacePaint);
            canvas.drawLine(leftEye.x - eyeballRadius, leftEye.y, leftEye.x + eyeballRadius, leftEye.y, mLinePaint);
        }else{
            canvas.drawCircle(leftEye.x, leftEye.y, eyeballRadius, mEyeBallPaint);
            canvas.drawCircle(leftEye.x, leftEye.y, 10, mPupilPaint);

        }
        canvas.drawCircle(leftEye.x, leftEye.y, eyeballRadius, mLinePaint);

        //Right-Eye draw logic
        canvas.drawCircle(rightEye.x, rightEye.y, eyeballRadius, mEyeBallPaint);
        if(rightEyeOpenChance> .3f && rightEyeOpenChance<.7) {
            canvas.drawCircle(rightEye.x, rightEye.y, eyeballRadius, mEyeBallPaint);
            canvas.drawArc(rightEye.x - eyeballRadius, rightEye.y - eyeballRadius, rightEye.x + eyeballRadius, rightEye.y + eyeballRadius, 180, 180, true, mFacePaint);
            canvas.drawArc(rightEye.x - eyeballRadius, rightEye.y - eyeballRadius, rightEye.x + eyeballRadius, rightEye.y + eyeballRadius, 180, 180, true, mLinePaint);
            canvas.drawCircle(rightEye.x, rightEye.y+10, 10, mPupilPaint);
        }else if (rightEyeOpenChance<.3f){
            canvas.drawCircle(rightEye.x, rightEye.y, eyeballRadius, mFacePaint);
            canvas.drawLine(rightEye.x - eyeballRadius, rightEye.y, rightEye.x + eyeballRadius, rightEye.y, mLinePaint);
        }else{
            canvas.drawCircle(rightEye.x, rightEye.y, eyeballRadius, mEyeBallPaint);
            canvas.drawCircle(rightEye.x, rightEye.y, 10, mPupilPaint);

        }

        canvas.drawCircle(rightEye.x, rightEye.y, eyeballRadius, mLinePaint);
    }

    private void drawEyebrows(Canvas canvas, double eyeballRadius, PointF leftEye, PointF rightEye){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float leftInnerX = (float) (leftEye.x + (eyeballRadius / 3f));
            float leftTop = (float) (leftEye.y - (mEyeBrowThickness * 1.5f + eyeballRadius));
            float leftBot = (float) (leftEye.y - (mEyeBrowThickness * 0.5f + eyeballRadius));
            float leftOutterX = (float) (leftEye.x - (eyeballRadius / 4f));

            float rightInnerX = (float) (rightEye.x - (eyeballRadius / 3f));
            float rightTop = (float) (rightEye.y - (mEyeBrowThickness * 1.5f + eyeballRadius));
            float rightBot = (float) (rightEye.y - (mEyeBrowThickness * 0.5f + eyeballRadius));
            float rightOutterX = (float) (rightEye.x + (eyeballRadius / 4f));

            float leftYMidpoint = (leftTop + leftBot) / 2;
            float rightYMidpoint = (rightTop + rightBot) / 2;

            //Draw left Eyebrow
            float arcRadius = 30;
            canvas.drawLine(leftOutterX, leftYMidpoint, leftInnerX, leftYMidpoint, mEyeBrowPaint);
            canvas.drawLine(leftInnerX, leftBot, leftOutterX, leftBot, mLinePaint);
            canvas.drawArc(leftOutterX - arcRadius, leftTop, leftOutterX + arcRadius, leftBot, 90, 180, false, mEyeBrowPaint);
            canvas.drawArc(leftOutterX - arcRadius, leftTop, leftOutterX + arcRadius, leftBot, 90, 180, false, mLinePaint);
            canvas.drawLine(leftOutterX, leftTop, leftInnerX, leftTop, mLinePaint);

            //Draw Right Eyebrow
            canvas.drawLine(rightOutterX, rightYMidpoint, rightInnerX, rightYMidpoint, mEyeBrowPaint);
            canvas.drawLine(rightInnerX, rightBot, rightOutterX, rightBot, mLinePaint);
            canvas.drawArc(rightOutterX - arcRadius, rightTop, rightOutterX + arcRadius, rightBot, 270, 180, false, mEyeBrowPaint);
            canvas.drawArc(rightOutterX - arcRadius, rightTop, rightOutterX + arcRadius, rightBot, 270, 180, false, mLinePaint);
            canvas.drawLine(rightOutterX, rightTop, rightInnerX, rightTop, mLinePaint);

            //Unify the Brows!!!
            Path uniBrowPath = new Path();
            uniBrowPath.moveTo(leftInnerX, leftTop);
            uniBrowPath.lineTo(rightInnerX, rightTop);
            uniBrowPath.lineTo(rightInnerX, rightBot);
            uniBrowPath.lineTo(leftInnerX, leftBot);
            uniBrowPath.lineTo(leftInnerX, leftTop);
            canvas.drawPath(uniBrowPath, mEyeBrowPaint);
            uniBrowPath.close();

            canvas.drawLine(rightInnerX, rightTop, leftInnerX, leftTop, mLinePaint);
            canvas.drawLine(rightInnerX, rightBot, leftInnerX, leftBot, mLinePaint);
        }

    }

    private RectF getAdjustedRect(RectF faceRect){
        RectF adjustedRect = new RectF(faceRect);
        Matrix matrix = new Matrix();
        matrix.postTranslate(0, (int)(adjustedRect.height()* HAIR_VERTICAL_OFFSET));
        matrix.mapRect(adjustedRect);

        float centerY = adjustedRect.centerY();
        float yDelta = Math.abs(centerY - adjustedRect.top);
        float newTop = centerY - (yDelta * HAIR_HEIGHT_SCALE);
        float newBottom = centerY + (yDelta * HAIR_HEIGHT_SCALE);
        adjustedRect.top = newTop;
        adjustedRect.bottom = newBottom;

        float centerX = adjustedRect.centerX();
        float xDelta = Math.abs(centerX - adjustedRect.left);
        float newRight = centerX + (xDelta * HAIR_WIDTH_SCALE);
        float newLeft = centerX - (xDelta * HAIR_WIDTH_SCALE);
        adjustedRect.right = newRight;
        adjustedRect.left = newLeft;
        return adjustedRect;
    }

    private void adjustEyebrowThickness(RectF faceRect){
        mEyeBrowThickness = faceRect.height()/13;
        mEyeBrowPaint.setStrokeWidth(mEyeBrowThickness);
    }

    @Override
    public Bitmap getBitmap(long currentMillis) {
        return super.getBitmap();
    }

    @Override
    public Bitmap getBitmap() {
        return super.getBitmap();
    }

}
