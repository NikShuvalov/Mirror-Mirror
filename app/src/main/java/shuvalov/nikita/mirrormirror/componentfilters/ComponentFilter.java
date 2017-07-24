package shuvalov.nikita.mirrormirror.componentfilters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.View;

import com.google.android.gms.vision.face.Face;

import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.AppConstants;
import shuvalov.nikita.mirrormirror.filters.Filter;

/**
 * Created by NikitaShuvalov on 5/18/17.
 */

public abstract class ComponentFilter extends Filter implements View.OnClickListener {
    public ComponentFilter(String name) {
        super(name, null, FilterType.COMPONENT, 0, 0, 0, 0);
    }
}
