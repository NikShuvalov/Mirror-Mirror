package shuvalov.nikita.mirrormirror.componentfilters.custom_component_filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Build;


import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.componentfilters.ComponentFilter;

/**
 * Created by NikitaShuvalov on 5/19/17.
 */

public class RickComponentFilter extends ComponentFilter {
    private Paint mLinePaint, mTextPaint, mEyeBrowPaint, mEyeBallPaint, mPupilPaint, mFacePaint;
    private float mTextSize, mEyeBrowThickness;
    private RectF mFaceRect;
    private Bitmap mRickVomit, mRickHair;

    private static final String FILTER_NAME = "Rick Sanchez";
    private static final float HAIR_WIDTH_SCALE = 2.1f;
    private static final float HAIR_HEIGHT_SCALE = 1.3f;
    private static final float HAIR_VERTICAL_OFFSET = -0.35f;


    public RickComponentFilter(Context context) {
        super();
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
    public Canvas drawComponentsToCanvas(Canvas canvas) {
        FaceTracker faceTracker = FaceTracker.getInstance();
        PointF leftEye = faceTracker.getLeftEye();
        PointF rightEye = faceTracker.getRightEye();
        float eyeballRadius = Math.abs(leftEye.x-rightEye.x)/2.25f;

        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        mFaceRect = faceTracker.getFaceRect();
        PointF leftMouth = faceTracker.getLeftMouth();
        PointF rightMouth = faceTracker.getRightMouth();
        if(mFaceRect!=null){
            canvas.drawBitmap(mRickHair, null, getAdjustedRect(mFaceRect), null);
            drawFace(canvas);
            mFaceRect.setEmpty();
        }
        if(leftMouth!=null && rightMouth!=null) {
            float left = leftMouth.x - eyeballRadius;
            float right = rightMouth.x + eyeballRadius;
            float distance = Math.abs(right - left);
            float midY = (leftMouth.y + rightMouth.y) / 2;
            RectF vomitRect = new RectF(leftMouth.x, midY, rightMouth.x, midY + distance * .6f);
            canvas.drawBitmap(mRickVomit, null, vomitRect, null);
            canvas.drawLine(left, leftMouth.y, right, rightMouth.y, mLinePaint);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawArc(left - eyeballRadius/2, leftMouth.y - eyeballRadius/2, left + eyeballRadius/2, leftMouth.y + eyeballRadius/2, 90f, 180f, false, mLinePaint);
                canvas.drawArc(right - eyeballRadius/2, rightMouth.y - eyeballRadius/2, right + eyeballRadius/2, rightMouth.y + eyeballRadius/2, 270f, 180f, false, mLinePaint);
            }
        }

        float line1Height = faceTracker.getScreenHeight()*.8f;
        canvas.drawText("I stand", faceTracker.getScreenWidth()*.2f, line1Height, mTextPaint);
        canvas.drawText("with Rick!",faceTracker.getScreenWidth()*.1f, line1Height+mTextSize + 16, mTextPaint);

        drawEyes(canvas, leftEye, rightEye, eyeballRadius);
        drawEyebrows(canvas, eyeballRadius*1.1, leftEye, rightEye);
        return canvas;
    }

    private void drawFace(Canvas canvas){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float verticalArcHeight = mFaceRect.height()/4;
            float faceTop = mFaceRect.top + verticalArcHeight;
            float faceBot = mFaceRect.bottom -verticalArcHeight/2;
            Path facePath = new Path();
            facePath.moveTo(mFaceRect.left, faceTop);
            facePath.arcTo(mFaceRect.left, faceTop - verticalArcHeight, mFaceRect.right, faceTop + verticalArcHeight, 180, 180, false);
            facePath.lineTo(mFaceRect.right, faceBot);
            facePath.arcTo(mFaceRect.left, faceBot - verticalArcHeight, mFaceRect.right, faceBot + verticalArcHeight, 0, 180, false);
            facePath.lineTo(mFaceRect.left, faceTop);
            canvas.drawPath(facePath, mFacePaint);
            canvas.drawPath(facePath, mLinePaint);
        }
    }

    private void drawEyes(Canvas canvas, PointF leftEye, PointF rightEye, float eyeballRadius){
        canvas.drawCircle(leftEye.x, leftEye.y, eyeballRadius, mEyeBallPaint);
        canvas.drawCircle(leftEye.x, leftEye.y, eyeballRadius, mLinePaint);
        canvas.drawCircle(leftEye.x, leftEye.y, 10, mPupilPaint);

        canvas.drawCircle(rightEye.x, rightEye.y, eyeballRadius, mEyeBallPaint);
        canvas.drawCircle(rightEye.x, rightEye.y, eyeballRadius, mLinePaint);
        canvas.drawCircle(rightEye.x, rightEye.y, 10, mPupilPaint);
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

            canvas.drawLine(rightInnerX, rightTop, leftInnerX, leftTop, mLinePaint);
            canvas.drawLine(rightInnerX, rightBot, leftInnerX, leftBot, mLinePaint);
        }

    }
    @Override
    public String getName() {
        return FILTER_NAME;
    }


    private RectF getAdjustedRect(RectF faceRect){
        RectF adjustedRect = new RectF(faceRect);
        Matrix matrix = new Matrix();
        matrix.postTranslate(0, (int)(adjustedRect.height()*HAIR_VERTICAL_OFFSET));
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

        mEyeBrowThickness = faceRect.height()/13;
        mEyeBrowPaint.setStrokeWidth(mEyeBrowThickness);
        return adjustedRect;
    }
}
