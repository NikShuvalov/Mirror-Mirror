package shuvalov.nikita.mirrormirror.componentfilters;

import android.graphics.Bitmap;
import android.view.View;

import shuvalov.nikita.mirrormirror.filters.Filter;

/**
 * Created by NikitaShuvalov on 5/18/17.
 */

public abstract class ComponentFilter extends Filter{
    public ComponentFilter(String name, Bitmap previewBitmap) {
        super(name, previewBitmap, FilterType.COMPONENT, 1f, 1f, 0, 0);
    }
}
