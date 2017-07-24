package shuvalov.nikita.mirrormirror.filters;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by NikitaShuvalov on 4/5/17.
 */

public abstract class Filter {
    private float mScaleX, mScaleY;
    private float mOffsetXPercent, mOffsetYPercent;
    private String mFilterName;
    private FilterType mFilterType;
    private Bitmap mInitialBitmap;

    public Filter(String filterName, Bitmap initialBitmap, FilterType filterType, float scaleX, float scaleY, float offsetXPercent, float offsetYPercent) {
        mFilterName = filterName;
        mInitialBitmap = initialBitmap;
        mFilterType = filterType;
        mScaleX = scaleX;
        mScaleY = scaleY;
        mOffsetXPercent = offsetXPercent;
        mOffsetYPercent = offsetYPercent;
    }

    /**
     *Use this method on animated filters to get the current frames bitmap. Doesn't work on static filters.
     *
     * @returns a bitmap if the filter is animated, returns null if static.
     */
    public abstract Bitmap getBitmap(long currentMillis);

    public Bitmap getBitmap(){
        return mInitialBitmap;
    }

    public boolean isParticle(){return false;}

    public float getScaleX() {
        return mScaleX;
    }

    public float getScaleY() {
        return mScaleY;
    }

    public float getOffsetXPercent() {
        return mOffsetXPercent;
    }

    public float getOffsetYPercent() {
        return mOffsetYPercent;
    }

    public String getFilterName() {
        return mFilterName;
    }

    public FilterType getFilterType() {
        return mFilterType;
    }

    public enum FilterType{ //ToDo: Consider changing this to STATIC, ANIMATED, COMPONENT, PARTICLE, and the facebook profile ones FULL, CENTER, BANNER_TOP/BOTTOM
        FULL, CENTER, BANNER_TOP, BANNER_BOTTOM, FACE, TOP_OF_HEAD, BELOW_FACE, HAIRLINE, COMPONENT
    }

    public abstract void drawFilterToCanvas(Canvas canvas);
}
