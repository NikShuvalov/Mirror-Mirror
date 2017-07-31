package shuvalov.nikita.mirrormirror.componentfilters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;

/**
 * Created by NikitaShuvalov on 7/28/17.
 */

public class DisguiseComponentFilter extends ComponentFilter {
    private static final String FILTER_NAME = "Master of Disguise";
    private Bitmap mNoseGlasses, mMustache;


    public DisguiseComponentFilter(Context context, Bitmap previewBitmap) {
        super(FILTER_NAME, previewBitmap);
        loadBitmaps(context);
    }

    private void loadBitmaps(Context c){
        mNoseGlasses = BitmapFactory.decodeResource(c.getResources(), R.drawable.mod_glasses_nose);
        mMustache = BitmapFactory.decodeResource(c.getResources(), R.drawable.mod_mustache);
    }

    @Override
    public Bitmap getBitmap(long currentMillis) {
        return getBitmap();
    }


    private boolean adjustGlassesRect(RectF glassesRect){
        FaceTracker faceTracker = FaceTracker.getInstance();
        PointF leftEye = faceTracker.getLeftEye();
        PointF rightEye = faceTracker.getRightEye();
        RectF faceRect = faceTracker.getFaceRect();
        float eyeRadius = faceTracker.getEyeballRadius();
        if(faceRect!=null){
            float meanEyeY = (leftEye.y + rightEye.y)/2;
            if(eyeRadius==-1){
                glassesRect.set(faceRect.left, meanEyeY - faceRect.height()/5, faceRect.right, meanEyeY + faceRect.height()/5);
                return true;
            }else {
                glassesRect.set(faceRect.left, meanEyeY - eyeRadius * 3, faceRect.right, meanEyeY + eyeRadius * 3);
                return true;
            }
        }
        return false;
    }

    private boolean adjustMustacheRect(RectF mustacheRect, RectF glassesRect){
        FaceTracker faceTracker = FaceTracker.getInstance();
        PointF leftMouth = faceTracker.getLeftMouth();
        PointF rightMouth = faceTracker.getRightMouth();
        PointF bottomLip = faceTracker.getBottomLip();
        float eyeRadius = faceTracker.getEyeballRadius();
        if(leftMouth!=null && rightMouth!=null) {
            if (bottomLip != null) {
                mustacheRect.set(leftMouth.x, glassesRect.bottom - eyeRadius, rightMouth.x, bottomLip.y);
                return true;
            } else {
                mustacheRect.set(leftMouth.x, glassesRect.bottom - eyeRadius, rightMouth.x, Math.max(leftMouth.y, rightMouth.y));
                return true;
            }
        }
        return false;
    }

    @Override
    public void drawFilterToCanvas(Canvas canvas) {
        RectF glassesRect = new RectF();
        RectF mustacheRect = new RectF();
        if(adjustGlassesRect(glassesRect)){
            if(adjustMustacheRect(mustacheRect, glassesRect)){
                canvas.drawBitmap(mMustache, null, mustacheRect, null);
                canvas.drawBitmap(mNoseGlasses, null, glassesRect, null);
            }else{
                canvas.drawBitmap(mNoseGlasses, null, glassesRect, null);
            }
        }
    }

    @Override
    public void drawMirroredFilterToCanvas(Canvas canvas, Matrix mirrorMatrix) {
        canvas.save();
        canvas.setMatrix(mirrorMatrix);
        RectF glassesRect = new RectF();
        RectF mustacheRect = new RectF();
        if(adjustGlassesRect(glassesRect)){
            if(adjustMustacheRect(mustacheRect, glassesRect)){
                canvas.drawBitmap(mMustache, null, mustacheRect, null);
                canvas.drawBitmap(mNoseGlasses, null, glassesRect, null);
            }else{
                canvas.drawBitmap(mNoseGlasses, null, glassesRect, null);
            }
        }
        canvas.restore();
    }
}
