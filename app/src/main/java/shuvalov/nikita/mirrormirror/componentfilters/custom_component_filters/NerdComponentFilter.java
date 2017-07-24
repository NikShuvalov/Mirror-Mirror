package shuvalov.nikita.mirrormirror.componentfilters.custom_component_filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.View;

import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.componentfilters.ComponentFilter;

/**
 * Created by NikitaShuvalov on 5/20/17.
 */

public class NerdComponentFilter extends ComponentFilter {
    private Paint mFramePaint, mTapePaint, mLensPaint, mLinePaint, mErasePaint;
    private static final String FILTER_NAME = "Nerd";
    private Bitmap mPreviewImage;

    public NerdComponentFilter(Context context) {
        super();
        createPaints();
    }

    private void createPaints(){
        mFramePaint = new Paint();
        mFramePaint = new Paint(Color.argb(255,100,100,100));
        mFramePaint.setStyle(Paint.Style.FILL);

        mTapePaint = new Paint();
        mTapePaint.setColor(Color.argb(255,230,230 ,230));
        mTapePaint.setStyle(Paint.Style.FILL);

        mLensPaint = new Paint();
        mLensPaint.setColor(Color.argb(20, 0, 10, 120));
        mLensPaint.setStyle(Paint.Style.FILL);

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5);

        mErasePaint = new Paint();
        mErasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public Bitmap getPreviewImage() {
        return mPreviewImage;
    }

    @Override
    public Canvas drawComponentsToCanvas(Canvas canvas) {
        FaceTracker faceTracker = FaceTracker.getInstance();
        PointF leftEye = faceTracker.getLeftEye();
        PointF rightEye = faceTracker.getRightEye();
        float eyeballRadius = -1;
        if(leftEye!=null && rightEye!=null){
            eyeballRadius = Math.abs(leftEye.x - rightEye.x)/4f;
            drawGlasses(canvas, leftEye, rightEye, eyeballRadius);
        }
        return canvas;
    }

    private void drawGlasses(Canvas canvas, PointF leftEye, PointF rightEye, float eyeballRadius){
        float frameRadius = eyeballRadius*1.75f;
        float lensRadius = eyeballRadius*1.4f;
        float bridgeAngle = 30;

        //Left Frame Vars
        float leftFrameXStart = leftEye.x - frameRadius;
        float leftFrameXEnd = leftEye.x + frameRadius;
        float leftFrameTop = leftEye.y - frameRadius - eyeballRadius;
        float leftFrameBot = leftEye.y + frameRadius - eyeballRadius;

        //Right Frame Vars
        float rightFrameXStart = rightEye.x - frameRadius;
        float rightFrameXEnd = rightEye.x + frameRadius;
        float rightFrameTop = rightEye.y - frameRadius - eyeballRadius;
        float rightFrameBot = rightEye.y + frameRadius - eyeballRadius;

        //Left lens vars
        float leftLensXStart = leftEye.x - lensRadius;
        float leftLensXEnd = leftEye.x + lensRadius;
        float leftLensTop = leftEye.y - lensRadius - eyeballRadius;
        float leftLensBot = leftEye.y + lensRadius -eyeballRadius;

        //Right lens vars
        float rightLensXStart = rightEye.x - lensRadius;
        float rightLensXEnd = rightEye.x + lensRadius;
        float rightLensTop = rightEye.y - lensRadius - eyeballRadius;
        float rightLensBot = rightEye.y + lensRadius - eyeballRadius;

        RectF leftFrameOval = new RectF(leftFrameXStart, leftFrameTop, leftFrameXEnd, leftFrameBot);
        RectF rightFrameOval = new RectF(rightFrameXStart, rightFrameTop, rightFrameXEnd, rightFrameBot);

        RectF leftLensOval = new RectF(leftLensXStart,leftLensTop, leftLensXEnd, leftLensBot);
        RectF rightLensOval = new RectF(rightLensXStart, rightLensTop, rightLensXEnd, rightLensBot);

        PointF bridgePathOrigin = new PointF(leftFrameXEnd, leftEye.y-eyeballRadius);

        Path bridgePath = new Path();
        bridgePath.moveTo(bridgePathOrigin.x, bridgePathOrigin.y);
        bridgePath.arcTo(leftFrameOval,0, bridgeAngle * -1);
        bridgePath.arcTo(rightFrameOval, 180 + bridgeAngle, bridgeAngle* - 1, false);
        bridgePath.lineTo(leftFrameXEnd, leftEye.y- eyeballRadius);

        canvas.drawOval(leftFrameOval, mFramePaint);
        canvas.drawOval(rightFrameOval, mFramePaint);
        canvas.drawOval(leftFrameOval, mLinePaint);
        canvas.drawOval(rightFrameOval, mLinePaint);

        canvas.drawOval(leftLensOval, mErasePaint);
        canvas.drawOval(rightLensOval, mErasePaint);
        canvas.drawOval(leftLensOval, mLensPaint);
        canvas.drawOval(rightLensOval, mLensPaint);
        canvas.drawPath(bridgePath, mTapePaint);
        canvas.drawPath(bridgePath, mLinePaint);

        bridgePath.close();
    }

    @Override
    public String getName() {
        return FILTER_NAME;
    }

    @Override
    public void onClick(View view) {

    }
}
