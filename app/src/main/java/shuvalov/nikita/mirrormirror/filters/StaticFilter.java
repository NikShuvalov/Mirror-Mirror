package shuvalov.nikita.mirrormirror.filters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.RectF;

import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;

/**
 * Created by NikitaShuvalov on 5/4/17.
 */

public class StaticFilter extends Filter {


    public StaticFilter(String filterName, Bitmap initialBitmap, FilterType filterType, float scaleX, float scaleY, float offsetXPercent, float offsetYPercent) {
        super(filterName, initialBitmap, filterType, scaleX, scaleY, offsetXPercent, offsetYPercent);
    }

    /**
     * There's no reason to use this for staticFilters.
     *
     * @param c
     * @return The filter's bitmap
     */
    @Override
    public Bitmap getBitmap(long c) {
        return super.getBitmap();
    }

    @Override
    public void drawFilterToCanvas(Canvas canvas) {
        RectF faceRect = FaceTracker.getInstance().getFaceRect();
        if(faceRect!=null){
            canvas.drawBitmap(super.getBitmap(),null, faceRect,null);
        }
    }

    @Override
    public void drawMirroredFilterToCanvas(Canvas canvas, Matrix mirrorMatrix) {
        RectF faceRect = FaceTracker.getInstance().getFaceRect();
        mirrorMatrix.mapRect(faceRect);
        if(faceRect!=null){
            Bitmap imageBitmap = super.getBitmap();
            imageBitmap = Bitmap.createBitmap(imageBitmap,0,0, imageBitmap.getWidth()*(int)getScaleX(), imageBitmap.getHeight()*(int)getScaleY(), mirrorMatrix, true);
            canvas.drawBitmap(imageBitmap, null, faceRect, null);
        }
    }
}
