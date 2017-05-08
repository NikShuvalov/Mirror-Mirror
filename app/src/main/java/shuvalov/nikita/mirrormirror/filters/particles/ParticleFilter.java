package shuvalov.nikita.mirrormirror.filters.particles;

import android.graphics.Bitmap;

import shuvalov.nikita.mirrormirror.filters.Filter;

/**
 * Created by NikitaShuvalov on 5/7/17.
 */

public class ParticleFilter extends Filter{


    public ParticleFilter(String filterName, int resourceInt, ImagePosition imagePosition, float scaleX, float scaleY, float offsetXPercent, float offsetYPercent) {
        super(filterName, resourceInt, imagePosition, scaleX, scaleY, offsetXPercent, offsetYPercent);
    }

    @Override
    public Bitmap getBitmap(long currentMillis) {
        return null;
    }

    @Override
    public boolean isAnimated() {
        return false;
    }

    @Override
    public boolean isParticle() {
        return true;
    }
}
