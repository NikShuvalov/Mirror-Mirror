package shuvalov.nikita.mirrormirror.componentfilters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;

/**
 * Created by NikitaShuvalov on 7/25/17.
 */

public class VikingComponentFilter extends ComponentFilter {
    private static final String FILTER_NAME = "Viking";
    private Bitmap mBeardBitmap, mHatBitmap;
    private static final float HAT_HORIZONTAL_SCALE = 1.15f;


    public VikingComponentFilter(Context context, Bitmap previewBitmap) {
        super(FILTER_NAME, previewBitmap);
        loadBitmaps(context);
    }

    private void loadBitmaps(Context c){
        mHatBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.viking_hat);
        mBeardBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.viking_beard);
    }

    @Override
    public Bitmap getBitmap(long currentMillis) {
        return getBitmap();
    }

    @Override
    public void drawFilterToCanvas(Canvas canvas) {
        if(canvas!=null){
            FaceTracker faceTracker = FaceTracker.getInstance();
            RectF faceRect = faceTracker.getFaceRect();
            if(faceRect!=null) {
                //ToDo: Allow for tilt detection
                RectF hatRect = new RectF(faceRect);
                RectF beardRect = new RectF(faceRect);

                adjustBeardRect(beardRect);
                adjustHatRect(hatRect);
                drawComponents(canvas, hatRect, beardRect);
            }
        }
    }


    private void drawComponents(Canvas canvas, RectF hatRect, RectF beardRect){
//        float faceAngle = -FaceTracker.getInstance().getFaceAngle();
//        if(faceAngle!= Float.MIN_VALUE){
//            canvas.save();
//            canvas.rotate(faceAngle, hatRect.centerX(), hatRect.centerY());
//            canvas.drawBitmap(mHatBitmap, null, hatRect, null);
//            canvas.restore();
//            canvas.save();
//            canvas.rotate(faceAngle, beardRect.centerX(), beardRect.centerY());
//            canvas.drawBitmap(mBeardBitmap, null, beardRect, null);
//            canvas.restore();
//        }else{
        canvas.drawBitmap(mHatBitmap, null, hatRect, null);
        canvas.drawBitmap(mBeardBitmap, null, beardRect, null);
    }

    private void adjustBeardRect(RectF beardRect){
        FaceTracker faceTracker = FaceTracker.getInstance();
        PointF noseBase = faceTracker.getNoseBase();
        float deltaY;
        if(noseBase!=null) {
            float noseBaseY = faceTracker.getNoseBase().y;
            float faceTop = beardRect.top;
            deltaY = (noseBaseY - faceTop) *.9f;
        }else{
            deltaY = faceTracker.getFaceRect().height() * 0.55f;
        }
        Matrix matrix = new Matrix();
        matrix.setTranslate(0, deltaY);
        matrix.mapRect(beardRect);
    }

    private void adjustHatRect(RectF hatRect){
        Matrix hatTranslate = new Matrix();
        hatTranslate.setTranslate(0, -hatRect.height()*0.6f);
        hatTranslate.postScale(HAT_HORIZONTAL_SCALE, 1, hatRect.centerX(),hatRect.centerY());
        hatTranslate.mapRect(hatRect);
    }

    @Override
    public void drawMirroredFilterToCanvas(Canvas canvas, Matrix mirrorMatrix) {
        if(canvas!=null){
            canvas.save();
            FaceTracker faceTracker = FaceTracker.getInstance();
            RectF faceRect = faceTracker.getFaceRect();

            canvas.setMatrix(mirrorMatrix);

            RectF hatRect = new RectF(faceRect);
            RectF beardRect = new RectF(faceRect);

            adjustBeardRect(beardRect);
            adjustHatRect(hatRect);
            drawComponents(canvas, hatRect, beardRect);
            canvas.restore();
        }

    }
}
