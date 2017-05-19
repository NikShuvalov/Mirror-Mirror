package shuvalov.nikita.mirrormirror.componentfilters;

import android.content.Context;
import android.graphics.Canvas;

import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;

/**
 * Created by NikitaShuvalov on 5/18/17.
 */

public class ComponentOverlay extends BaseOverlay{
    private ComponentFilter mCurrentComponentFilter;
    private final Object mCanvasLock;
    
    public ComponentOverlay(Context context, ComponentFilter componentFilter) {
        super(context);
        mCurrentComponentFilter = componentFilter;
        mCanvasLock = new Object();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (mCanvasLock){
            mCurrentComponentFilter.drawComponentsToCanvas(canvas);
            try {
                Thread.sleep(50); //FixMe: Find a more elegant solution to reduce FPS or figure out what else can be done to keep bitmaps from flickering.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeComponentFilter(ComponentFilter componentFilter){
        mCurrentComponentFilter = componentFilter;
    }
}
