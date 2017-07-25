package shuvalov.nikita.mirrormirror.filters.particles;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import shuvalov.nikita.mirrormirror.filters.Filter;

/**
 * Created by NikitaShuvalov on 5/7/17.
 */

public class ParticleFilter extends Filter{


    public ParticleFilter(String filterName, Bitmap previewBitmap, FilterType filterType, float scaleX, float scaleY, float offsetXPercent, float offsetYPercent) {
        super(filterName, previewBitmap, filterType, scaleX, scaleY, offsetXPercent, offsetYPercent);
    }

    @Override
    public Bitmap getBitmap(long currentMillis) {
        return null;
    }

    @Override
    public boolean isParticle() {
        return true;
    }

    @Override
    public void drawFilterToCanvas(Canvas canvas) {

    }

    @Override
    public void drawMirroredFilterToCanvas(Canvas canvas, Matrix mirrorMatrix) {

    }
}
