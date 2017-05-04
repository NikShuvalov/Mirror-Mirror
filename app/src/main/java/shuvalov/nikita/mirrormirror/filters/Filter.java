package shuvalov.nikita.mirrormirror.filters;

import android.graphics.Bitmap;

/**
 * Created by NikitaShuvalov on 4/5/17.
 */

public abstract class Filter {
    private int mResourceInt;
    private ImagePosition mImagePosition;
    private float mScaleX, mScaleY;
    private float mOffsetXPercent, mOffsetYPercent;
    private String mFilterName;

    public Filter(String filterName, int resourceInt, ImagePosition imagePosition, float scaleX, float scaleY, float offsetXPercent, float offsetYPercent) {
        mFilterName = filterName;
        mResourceInt = resourceInt;
        mImagePosition = imagePosition;
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

    //FixME: This is effectively useless, unless if I decide to use different filters at once. Like a mix-and-match type deal.
    public enum ImagePosition{
        TOP_OF_HEAD, FACE, BELOW_FACE, HAIRLINE
    }

    public int getResourceInt() {
        return mResourceInt;
    }

    public ImagePosition getImagePosition() {
        return mImagePosition;
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
}
