package shuvalov.nikita.mirrormirror.filters;

/**
 * Created by NikitaShuvalov on 4/5/17.
 */

public class Filter {
    private int mResourceInt;
    private ImagePosition mImagePosition;
    private float mScaleX, mScaleY;
    private float mOffsetXPercent, mOffsetYPercent;

    public Filter(int resourceInt, ImagePosition imagePosition, float scaleX, float scaleY, float offsetXPercent, float offsetYPercent) {
        mResourceInt = resourceInt;
        mImagePosition = imagePosition;
        mScaleX = scaleX;
        mScaleY = scaleY;
        mOffsetXPercent = offsetXPercent;
        mOffsetYPercent = offsetYPercent;
    }


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

}
