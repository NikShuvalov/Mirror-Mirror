package shuvalov.nikita.mirrormirror.filters;

import android.graphics.Bitmap;

/**
 * Created by NikitaShuvalov on 4/5/17.
 */

public abstract class Filter {
    private int mResourceInt;
    private float mScaleX, mScaleY;
    private float mOffsetXPercent, mOffsetYPercent;
    private String mFilterName;
    private FilterType mFilterType;

    public Filter(String filterName, int resourceInt, FilterType filterType, float scaleX, float scaleY, float offsetXPercent, float offsetYPercent) {
        mFilterName = filterName;
        mResourceInt = resourceInt;
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

    public abstract boolean isAnimated();

    public boolean isParticle(){return false;}


    public int getResourceInt() {
        return mResourceInt;
    }

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

    public enum FilterType{
        FULL, CENTER, BANNER_TOP, BANNER_BOTTOM, FACE, TOP_OF_HEAD, BELOW_FACE, HAIRLINE
    }
}
