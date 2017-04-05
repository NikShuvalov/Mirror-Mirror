package shuvalov.nikita.mirrormirror;

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
}
