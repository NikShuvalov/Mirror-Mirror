package shuvalov.nikita.mirrormirror.componentfilters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;

import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.filters.FilterManager;
import shuvalov.nikita.mirrormirror.filters.StaticFilter;
import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;

/**
 * Created by NikitaShuvalov on 5/18/17.
 */

public class ComponentOverlay extends BaseOverlay{
    private Rect mRect;
    private ArrayList<Filter> mFiltersUsed;
    private Paint mLinePaint, mTextPaint, mEyeBrowPaint;

    private static final float TEXT_SIZE = 200f;
    private static final float EYEBROW_THICKNESS = 65f;

    private Bitmap mRickHair, mRickVomit; //FixMe: Get Rid of hard-coding

    public ComponentOverlay(Context context, ComponentFilter componentFilter) {
        super(context);
        mFiltersUsed = componentFilter.getComponentList();
        mRect = new Rect();

        dirtyCoding();

    }

    public void dirtyCoding(){
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStrokeWidth(5f);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(TEXT_SIZE);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mEyeBrowPaint = new Paint();
        mEyeBrowPaint.setColor(Color.argb(200, 156, 239, 243));
        mEyeBrowPaint.setStyle(Paint.Style.FILL);
        mEyeBrowPaint.setStrokeWidth(EYEBROW_THICKNESS);

        mRickHair = BitmapFactory.decodeResource(getResources(), R.drawable.rick_hair);
        mRickVomit = BitmapFactory.decodeResource(getResources(), R.drawable.rick_vomit);
        Filter dirtySanchez = new StaticFilter("Rick Sanchez", R.drawable.rick_hair, Filter.ImagePosition.HAIRLINE, 1.75f, 1f, 0f, -0.25f);
        FilterManager.getInstance().dirtyDebuggingCode(dirtySanchez);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF faceRect =  FaceTracker.getInstance().getFaceRect();
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        if(faceRect!=null){
            faceRect.round(mRect);
            //ToDo: Consider just checking if we're using an animated filter here if issues arise. But for now it seems it would be more optimal if we update the boolean on filter change.
//            if(mUsingAnimated){
//                mBitmap = FilterManager.getInstance().getSelectedFilter().getBitmap(SystemClock.uptimeMillis());
//            }
            doItForRick(canvas);
        }
    }

    private void doItForRick(Canvas canvas){
        PointF leftMouth = FaceTracker.getInstance().getLeftMouth();
        PointF rightMouth = FaceTracker.getInstance().getRightMouth();
        if(leftMouth!=null && rightMouth!=null){
            float left = leftMouth.x;
            canvas.drawBitmap(mRickVomit, left,Math.max(leftMouth.y, rightMouth.y),null);
        }
        FaceTracker faceTracker = FaceTracker.getInstance();
        float line1Height = faceTracker.getScreenHeight()*.8f;
        canvas.drawText("I stand", faceTracker.getScreenWidth()*.2f, line1Height, mTextPaint);
        canvas.drawText("with Rick!",faceTracker.getScreenWidth()*.1f, line1Height+TEXT_SIZE + 16, mTextPaint);
        double eyeLength = faceTracker.getEyelength();
        PointF leftEye = faceTracker.getLeftEye();
        PointF rightEye = faceTracker.getRightEye();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float leftInnerX = (float)(leftEye.x+(eyeLength/2.5f));
            float leftTop = leftEye.y - (EYEBROW_THICKNESS*2.5f);
            float leftBot = leftEye.y -EYEBROW_THICKNESS*1.5f;
            float leftOutterX = (float)(leftEye.x-(eyeLength/3f));

            float rightInnerX = (float)(rightEye.x-(eyeLength/2.5f));
            float rightTop = rightEye.y - (EYEBROW_THICKNESS*2.5f);
            float rightBot = rightEye.y -EYEBROW_THICKNESS*1.5f;
            float rightOutterX = (float)(rightEye.x+(eyeLength/3f));

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

        canvas.drawBitmap(mRickHair, null, mRect, null);
        mRect.setEmpty();
    }

}
