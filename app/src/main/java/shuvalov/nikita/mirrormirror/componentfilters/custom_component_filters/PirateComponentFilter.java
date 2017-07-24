package shuvalov.nikita.mirrormirror.componentfilters.custom_component_filters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import shuvalov.nikita.mirrormirror.componentfilters.ComponentFilter;

/**
 * Created by NikitaShuvalov on 5/24/17.
 */

//ToDo: Create for Talk Like a pirate day
public class PirateComponentFilter extends ComponentFilter {
    private Bitmap mPreviewImage;



    @Override
    public Canvas drawComponentsToCanvas(Canvas canvas) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public Bitmap getPreviewImage() {
        return mPreviewImage;
    }
}
