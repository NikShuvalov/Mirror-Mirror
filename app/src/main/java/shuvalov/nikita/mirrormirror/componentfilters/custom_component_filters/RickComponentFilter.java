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
import android.util.Log;


import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.componentfilters.ComponentFilter;

/**
 * Created by NikitaShuvalov on 5/19/17.
 */

public class RickComponentFilter extends ComponentFilter {
    private Paint mLinePaint, mTextPaint, mEyeBrowPaint;
    private float mTextSize, mEyeBrowThickness;
    private RectF mFaceRect;
    private Bitmap mRickVomit, mRickHair;

    private static final String FILTER_NAME = "Rick Sanchez";
    private static final float WIDTH_SCALE = 1.75f;
    private static final float VERTICAL_OFFSET = -0.25f;


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
    }

    private void loadBitmaps(Context c){
        mRickHair = BitmapFactory.decodeResource(c.getResources(), R.drawable.rick_hair);
        mRickVomit = BitmapFactory.decodeResource(c.getResources(), R.drawable.rick_vomit);
    }

    @Override
    public Canvas drawComponentsToCanvas(Canvas canvas) {
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        FaceTracker faceTracker = FaceTracker.getInstance();
        resizeFaceRect(faceTracker.getFaceRect());

        PointF leftMouth = faceTracker.getLeftMouth();
        PointF rightMouth = faceTracker.getRightMouth();
        if(leftMouth!=null && rightMouth!=null){
            float left = leftMouth.x;
            canvas.drawBitmap(mRickVomit, left,Math.max(leftMouth.y, rightMouth.y),null);
        }
        if(mFaceRect!=null){
            canvas.drawBitmap(mRickHair, null, mFaceRect, null);
            mFaceRect.setEmpty();
        }


        float line1Height = faceTracker.getScreenHeight()*.8f;
        canvas.drawText("I stand", faceTracker.getScreenWidth()*.2f, line1Height, mTextPaint);
        canvas.drawText("with Rick!",faceTracker.getScreenWidth()*.1f, line1Height+mTextSize + 16, mTextPaint);
        double eyeLength = faceTracker.getEyelength();
        PointF leftEye = faceTracker.getLeftEye();
        PointF rightEye = faceTracker.getRightEye();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float leftInnerX = (float)(leftEye.x+(eyeLength/3f));
            float leftTop = leftEye.y - (mEyeBrowThickness*2.5f);
            float leftBot = leftEye.y -mEyeBrowThickness*1.5f;
            float leftOutterX = (float)(leftEye.x-(eyeLength/4f));

            float rightInnerX = (float)(rightEye.x-(eyeLength/3f));
            float rightTop = rightEye.y - (mEyeBrowThickness*2.5f);
            float rightBot = rightEye.y -mEyeBrowThickness*1.5f;
            float rightOutterX = (float)(rightEye.x+(eyeLength/4f));

            float leftYMidpoint = (leftTop+ leftBot)/2;
            float rightYMidpoint = (rightTop + rightBot)/2;

            //Draw left Eyebrow
            float arcRadius = 30;
            canvas.drawLine(leftOutterX, leftYMidpoint, leftInnerX, leftYMidpoint, mEyeBrowPaint);
            canvas.drawLine(leftInnerX, leftBot, leftOutterX,leftBot, mLinePaint);
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
        return canvas;
    }

    @Override
    public String getName() {
        return FILTER_NAME;
    }


    private void resizeFaceRect(RectF faceRect){
        if(faceRect!=null) {
            mFaceRect = faceRect;
            Matrix matrix = new Matrix();
            matrix.postTranslate(0, (int)(mFaceRect.height()*VERTICAL_OFFSET));
            matrix.mapRect(mFaceRect);

            float centerX = mFaceRect.centerX();
            float xDelta = Math.abs(centerX - mFaceRect.left);
            float newRight = centerX + (xDelta * WIDTH_SCALE);
            float newLeft = centerX - (xDelta * WIDTH_SCALE);
            mFaceRect.right = newRight;
            mFaceRect.left = newLeft;

            mEyeBrowThickness = mFaceRect.height()/13;
            mEyeBrowPaint.setStrokeWidth(mEyeBrowThickness);
        }
    }
}
