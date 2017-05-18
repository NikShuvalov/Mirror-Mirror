package shuvalov.nikita.mirrormirror.componentfilters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
        Filter dirtySanchez = new StaticFilter("Rick Sanchez", R.drawable.rick_hair, Filter.ImagePosition.HAIRLINE, 1.7f, 1f, 0f, -0.3f);
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
            canvas.drawLine(leftMouth.x, leftMouth.y, rightMouth.x, rightMouth.y, mLinePaint); //Draw mouthline
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
            float leftInnerX = (float)(leftEye.x+(eyeLength/2f));
            float leftTop = leftEye.y - (EYEBROW_THICKNESS*3f);
            float leftBot = leftEye.y -EYEBROW_THICKNESS*2f;
            float leftOutterX = (float)(leftEye.x-(eyeLength/2.5f));

            float rightInnerX = (float)(rightEye.x-(eyeLength/2.5f));
            float rightTop = rightEye.y - (EYEBROW_THICKNESS*3f);
            float rightBot = rightEye.y -EYEBROW_THICKNESS*2f;
            float rightOutterX = (float)(rightEye.x+(eyeLength/2.5f));



            //Draw left Eyebrow
            canvas.drawRoundRect(leftOutterX, leftTop, leftInnerX, leftBot, 50, 50, mEyeBrowPaint);
            canvas.drawRoundRect(leftOutterX, leftTop, leftInnerX, leftBot, 50, 50, mLinePaint);

            //Draw right eyebrow
            canvas.drawRoundRect(rightInnerX, rightTop,rightOutterX, rightBot, 50, 50, mEyeBrowPaint);
            canvas.drawRoundRect(rightInnerX, rightTop,rightOutterX, rightBot, 50, 50, mLinePaint);

            //Unify the brows!
//            float leftYMidpoint = leftTop+ (leftBot-leftTop)/2;
//            float rightYMidpoint = rightTop + (rightBot-rightTop)/2;
//
//            canvas.drawLine(leftInnerX, leftYMidpoint,rightInnerX,rightYMidpoint,  mEyeBrowPaint);
//            canvas.drawLine(leftInnerX, leftTop, rightInnerX,rightTop, mLinePaint);
//            canvas.drawLine(leftInnerX, leftBot, rightInnerX, rightBot, mLinePaint);
        }


        canvas.drawBitmap(mRickHair, null, mRect, null);
        mRect.setEmpty();
    }

}
