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

public abstract class ComponentFilter implements View.OnClickListener{
    public ComponentFilter() {}
    public abstract Canvas drawComponentsToCanvas(Canvas canvas);
    public abstract String getName();
    public abstract Bitmap getPreviewImage();

}
