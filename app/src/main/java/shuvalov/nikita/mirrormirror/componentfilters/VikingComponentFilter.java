package shuvalov.nikita.mirrormirror.componentfilters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;

/**
 * Created by NikitaShuvalov on 7/25/17.
 */

public class VikingComponentFilter extends ComponentFilter {
    private static final String FILTER_NAME = "Viking";
    private Bitmap mBeardBitmap, mHatBitmap;
    private static final float HAT_HORIZONTAL_SCALE = 1.25f;


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
        return null;
    }

    @Override
    public void drawFilterToCanvas(Canvas canvas) {
        if(canvas!=null){
            FaceTracker faceTracker = FaceTracker.getInstance();
            RectF faceRect = faceTracker.getFaceRect();
            if(faceRect!=null) {
                RectF hatRect = new RectF(faceRect);
                RectF beardRect = new RectF(faceRect);

                Matrix beardTranslate = new Matrix();
                beardTranslate.postTranslate(0, faceRect.height()*0.55f);
                beardTranslate.mapRect(beardRect);

                Matrix hatTranslate = new Matrix();
                hatTranslate.postTranslate(0, -faceRect.height() / 2);
                hatTranslate.mapRect(hatRect);
                adjustHatRect(hatRect);


                canvas.drawBitmap(mHatBitmap, null, hatRect, null);
                canvas.drawBitmap(mBeardBitmap, null, beardRect, null);
            }
        }
    }

    private void adjustHatRect(RectF hatRect){
        float centerX = hatRect.centerX();
        float xDelta = Math.abs(centerX - hatRect.left);
        float scaledLeft = centerX - (xDelta * HAT_HORIZONTAL_SCALE);
        float scaledRight = centerX + (xDelta * HAT_HORIZONTAL_SCALE);
        hatRect.left = scaledLeft;
        hatRect.right = scaledRight;
    }

    @Override
    public void drawMirroredFilterToCanvas(Canvas canvas, Matrix mirrorMatrix) {
        if(canvas!=null){
            canvas.save();
            canvas.setMatrix(mirrorMatrix);
            FaceTracker faceTracker = FaceTracker.getInstance();
            RectF faceRect = faceTracker.getFaceRect();
            RectF hatRect = new RectF(faceRect);
            RectF beardRect = new RectF(faceRect);

            Matrix beardTranslate = new Matrix();
            beardTranslate.postTranslate(0, faceRect.height()*0.55f);
            beardTranslate.mapRect(beardRect);

            Matrix hatTranslate = new Matrix();
            hatTranslate.postTranslate(0, -faceRect.height()/2);
            hatTranslate.mapRect(hatRect);
            adjustHatRect(hatRect);

            canvas.drawBitmap(mHatBitmap, null, hatRect, null);
            canvas.drawBitmap(mBeardBitmap, null, beardRect, null);
            canvas.restore();
        }

    }
}
