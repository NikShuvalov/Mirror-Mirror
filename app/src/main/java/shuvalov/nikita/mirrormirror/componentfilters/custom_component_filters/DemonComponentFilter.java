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
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import shuvalov.nikita.mirrormirror.AppConstants;
import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.componentfilters.ComponentFilter;
import shuvalov.nikita.mirrormirror.filters.AnimatedFilter;
import shuvalov.nikita.mirrormirror.filters.Filter;

/**
 * Created by NikitaShuvalov on 5/20/17.
 */

public class DemonComponentFilter extends ComponentFilter {
    private static final String FILTER_NAME = "Demon";
    private AnimatedFilter mLeftFlame, mRightFlame;
    private Paint mLinePaint, mHornPaint, mGoateePaint;
    private Bitmap mPreviewImage;

    public DemonComponentFilter(Context context) {
        mLeftFlame = new AnimatedFilter("Flames", R.drawable.flamekey0, Filter.FilterType.FACE, 1.25f, 1.5f, 0, -0.65f, AppConstants.getBitmapList(context, R.array.flame_animation_list)).randomizeStartFrame();
        mRightFlame = new AnimatedFilter("Flames", R.drawable.flamekey0, Filter.FilterType.FACE, 1.25f, 1.5f, 0, -0.65f, AppConstants.getBitmapList(context, R.array.flame_animation_list)).randomizeStartFrame();
        preparePaints();
    }

    private void preparePaints(){
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5f);

        mHornPaint = new Paint();
        mHornPaint.setColor(Color.argb(255, 218, 155, 100));
        mHornPaint.setStyle(Paint.Style.FILL);

        mGoateePaint = new Paint();
        mGoateePaint.setColor(Color.argb(255, 169, 77, 0));
        mGoateePaint.setStyle(Paint.Style.FILL);
    }
    @Override
    public Canvas drawComponentsToCanvas(Canvas canvas) {
        FaceTracker faceTracker = FaceTracker.getInstance();
        RectF faceRect = faceTracker.getFaceRect();
        PointF leftMouth = faceTracker.getLeftMouth();
        PointF rightMouth = faceTracker.getRightMouth();
        float eyeballRadius = faceTracker.getEyeballRadius();
        PointF leftEye = faceTracker.getLeftEye();
        PointF rightEye = faceTracker.getRightEye();
        if(faceRect!=null){

            drawEyeFlames(canvas, leftEye, rightEye, eyeballRadius);
//            drawHorns(canvas, leftEye, rightEye, eyeballRadius);
            drawGoatee(canvas, faceRect,leftMouth, rightMouth);
        }
        return canvas;
    }

    @Override
    public Bitmap getPreviewImage() {
        return mPreviewImage;
    }

    private void drawGoatee(Canvas canvas, RectF faceRect, PointF leftMouth, PointF rightMouth){

        float goateeTopLeft = leftMouth.y + faceRect.height()/10;
        float goateeTopRight = rightMouth.y + faceRect.height()/10;
        float goateeLen = faceRect.height() * 0.3f;
        float goateeMiddleX = (rightMouth.x + leftMouth.x)/2;
        RectF goateeOval = new RectF(leftMouth.x, goateeTopLeft - goateeLen, rightMouth.x, goateeTopLeft + goateeLen);

        PointF leftMouthPoint = new PointF(leftMouth.x, goateeTopLeft);
        PointF rightMouthPoint = new PointF(rightMouth.x, goateeTopRight);
        PointF middlePoint = new PointF(goateeMiddleX, goateeOval.centerY() + goateeLen);

        Path path = new Path();
        path.moveTo(leftMouthPoint.x, leftMouthPoint.y);
        path.arcTo(goateeOval, 180 , -35);
        path.lineTo((leftMouthPoint.x + middlePoint.x)/2f, (leftMouthPoint.y + middlePoint.y)/2f);
        path.lineTo(middlePoint.x, middlePoint.y);
        path.lineTo((rightMouthPoint.x + middlePoint.x)/2f, (rightMouthPoint.y + middlePoint.y)/2f);
        path.arcTo(goateeOval, 35,-35);
        path.lineTo(leftMouthPoint.x, leftMouthPoint.y);
        canvas.drawPath(path, mGoateePaint);
        canvas.drawPath(path, mLinePaint);
    }

    private void drawHorns(Canvas canvas, PointF leftEye, PointF rightEye, float eyeballRadius){
        FaceTracker faceTracker = FaceTracker.getInstance();
        Matrix matrix = new Matrix();
        RectF hornBaseOval;
        RectF hornTipOval;
        float hornBaseRadius = eyeballRadius*2.5f;
        float hornTipDiameter = eyeballRadius * 4f;
        boolean rightSideForward = faceTracker.getFaceAngle()<=0;

        float hornExtensionX = hornBaseRadius * (rightSideForward ? 0.55f: -0.55f);
        float hornExtensionY = hornBaseRadius* -1.75f;

        float baseStartAngle = 90;
        float baseSweepAngle = rightSideForward ? -75 : 75;
        float tipStartAngle = rightSideForward ? 0 : 180;
        float tipSweepAngle = rightSideForward ? -270 : 270;
        PointF origin;
        if(rightSideForward){
            float rightHornBaseY = rightEye.y - eyeballRadius*3;
            float rightHornInner = rightEye.x - eyeballRadius;
            float rightOutterX = rightHornInner + hornBaseRadius;
            float hornBaseOvalTop = rightHornBaseY - eyeballRadius*3;
            origin = new PointF(rightHornInner,rightHornBaseY);
            hornBaseOval = new RectF(rightHornInner, hornBaseOvalTop, rightOutterX, rightHornBaseY);
            hornTipOval = new RectF(rightOutterX -hornTipDiameter, hornBaseOvalTop -hornTipDiameter +hornExtensionY, rightOutterX + hornExtensionX,rightHornBaseY + hornExtensionY);
        }else{
            float leftHornBaseY = leftEye.y - eyeballRadius*3;
            float leftHornInner = leftEye.x + eyeballRadius*1.75f;
            float leftHornOuter = leftHornInner - hornBaseRadius;
            float hornBaseOvalTop = leftHornBaseY - eyeballRadius*3;
            float hornBaseRight = leftHornInner+hornBaseRadius;
            origin = new PointF(leftHornInner, leftHornBaseY);
            hornBaseOval = new RectF(leftHornOuter, hornBaseOvalTop, hornBaseRight, leftHornBaseY);
            hornTipOval = new RectF(leftHornOuter + hornExtensionX, hornBaseOvalTop + hornExtensionY, leftHornOuter + hornExtensionX,leftHornBaseY - hornExtensionY);
        }

        Path hornPath = new Path();
        hornPath.moveTo(origin.x, origin.y);
        hornPath.arcTo(hornBaseOval,baseStartAngle, baseSweepAngle);
        hornPath.rLineTo(hornExtensionX, hornExtensionY);
        hornPath.arcTo(hornTipOval,tipStartAngle, tipSweepAngle);
        hornPath.lineTo(origin.x, origin.y);

        canvas.drawPath(hornPath, mHornPaint);
        canvas.drawPath(hornPath, mLinePaint);
        canvas.drawArc(hornTipOval, tipStartAngle, tipSweepAngle, false, mLinePaint);


        matrix.postScale(-1, 1, (rightEye.x + leftEye.x)/2, faceTracker.getScreenHeight()/2);
        hornPath.transform(matrix);
        canvas.drawPath(hornPath, mHornPaint);
        canvas.drawPath(hornPath, mLinePaint);

    }

    private void drawEyeFlames(Canvas canvas, PointF leftEye, PointF rightEye, float eyeballRadius){
        if(leftEye==null || rightEye == null){
            return;
        }
        RectF leftRect = new RectF(leftEye.x - eyeballRadius *1.25f, leftEye.y - eyeballRadius*3, leftEye.x + eyeballRadius * 1.25f, leftEye.y);
        RectF rightRect = new RectF(rightEye.x - eyeballRadius * 1.25f, rightEye.y - eyeballRadius*3, rightEye.x + eyeballRadius * 1.25f, rightEye.y);
        mRightFlame.getBitmap(SystemClock.elapsedRealtime());
        canvas.drawBitmap(mLeftFlame.getBitmap(SystemClock.elapsedRealtime()), null, leftRect, null);
        canvas.drawBitmap(mRightFlame.getBitmap(SystemClock.elapsedRealtime()), null, rightRect, null);
    }

    @Override
    public String getName() {
        return FILTER_NAME;
    }

    @Override
    public void onClick(View view) {
        Log.d("Face Angle", "onClick: " + FaceTracker.getInstance().getFaceAngle());
        Log.d("Face TILT", "onClick: " + FaceTracker.getInstance().getFaceTilt());
    }
}
