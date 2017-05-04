package shuvalov.nikita.mirrormirror.filters;

import android.graphics.Bitmap;

/**
 * Created by NikitaShuvalov on 5/4/17.
 */

public class StaticFilter extends Filter {


    public StaticFilter(String filterName, int resourceInt, ImagePosition imagePosition, float scaleX, float scaleY, float offsetXPercent, float offsetYPercent) {
        super(filterName, resourceInt, imagePosition, scaleX, scaleY, offsetXPercent, offsetYPercent);
    }

    @Override
    public Bitmap getBitmap(long c) {
        return null;
    }

    @Override
    public boolean isAnimated() {
        return false;
    }
}
