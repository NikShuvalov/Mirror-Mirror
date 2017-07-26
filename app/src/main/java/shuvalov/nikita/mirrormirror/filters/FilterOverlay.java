package shuvalov.nikita.mirrormirror.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.componentfilters.ComponentFilter;
import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.filters.FilterManager;
import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;
import shuvalov.nikita.mirrormirror.overlay.GraphicThread;

import static android.content.ContentValues.TAG;

/**
 * Created by NikitaShuvalov on 3/24/17.
 */

public class FilterOverlay extends BaseOverlay{
    private final Object mCanvasLock;

    public FilterOverlay(Context context) {
        super(context);
        mCanvasLock = new Object();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (mCanvasLock) {
            Filter filter = FilterManager.getInstance().getSelectedFilter();
            if(canvas!=null) {
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                if (filter != null) {
                    filter.drawFilterToCanvas(canvas);
                    if (filter instanceof ComponentFilter) {
                        try {
                            Thread.sleep(75);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
