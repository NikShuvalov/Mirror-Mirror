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
    private Paint mLinePaint, mTextPaint;

    private static final float TEXT_SIZE = 200f;

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

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(TEXT_SIZE);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);

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
            canvas.drawBitmap(mRickHair, null, mRect, null);
            mRect.setEmpty();
        }
    }
}
