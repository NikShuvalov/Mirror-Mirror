package shuvalov.nikita.mirrormirror.componentfilters.custom_component_filters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;

import shuvalov.nikita.mirrormirror.componentfilters.ComponentFilter;

/**
 * Created by NikitaShuvalov on 5/24/17.
 */

//ToDo: Create for Talk Like a pirate day
public class PirateComponentFilter extends ComponentFilter {

    public PirateComponentFilter(String name, Bitmap previewImage) {
        super(name, previewImage);
    }

    @Override
    public void onClick(View view) {

    }


    @Override
    public Bitmap getBitmap(long currentMillis) {
        return null;
    }

    @Override
    public Bitmap getBitmap() {
        return null;
    }


    @Override
    public void drawFilterToCanvas(Canvas canvas) {

    }

    @Override
    public void drawMirroredFilterToCanvas(Canvas canvas, Matrix mirrorMatrix) {

    }
}
