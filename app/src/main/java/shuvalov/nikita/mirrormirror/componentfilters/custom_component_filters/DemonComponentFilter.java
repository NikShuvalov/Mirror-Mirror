package shuvalov.nikita.mirrormirror.componentfilters.custom_component_filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import shuvalov.nikita.mirrormirror.AppConstants;
import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.componentfilters.ComponentFilter;
import shuvalov.nikita.mirrormirror.filters.AnimatedFilter;
import shuvalov.nikita.mirrormirror.filters.Filter;

/**
 * Created by NikitaShuvalov on 5/20/17.
 */

public class DemonComponentFilter extends ComponentFilter {
    private static final String FILTER_NAME = "Demon";
    private AnimatedFilter mFlames;

    public DemonComponentFilter(Context context) {
        mFlames = new AnimatedFilter("Flames", R.drawable.flamekey0, Filter.ImagePosition.FACE, 1.25f, 1.5f, 0, -0.65f, AppConstants.getBitmapList(context, R.array.flame_animation_list));
    }

    @Override
    public Canvas drawComponentsToCanvas(Canvas canvas) {
        FaceTracker faceTracker = FaceTracker.getInstance();
        float eyeballRadius = faceTracker.getEyeballRadius();

        return canvas;
    }

    @Override
    public String getName() {
        return FILTER_NAME;
    }

    @Override
    public void onClick(View view) {

    }
}
