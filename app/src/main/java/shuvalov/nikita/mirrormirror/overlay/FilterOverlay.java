package shuvalov.nikita.mirrormirror.overlay;

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
import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.filters.FilterManager;
import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;
import shuvalov.nikita.mirrormirror.overlay.GraphicThread;

import static android.content.ContentValues.TAG;

/**
 * Created by NikitaShuvalov on 3/24/17.
 */

public class FilterOverlay extends BaseOverlay{
    private Bitmap mBitmap;
    private Filter mFilter;
    private Rect mRect;
    private boolean mUsingAnimated;

    public FilterOverlay(Context context) {
        super(context);
        notifyFilterChange();
        mRect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Filter filter = FilterManager.getInstance().getSelectedFilter();
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        if(filter!=null) {
            filter.drawFilterToCanvas(canvas);
        }
    }


    public void notifyFilterChange() {
        mFilter= FilterManager.getInstance().getSelectedFilter();
        if(mFilter!=null) {
            mBitmap = mFilter.getBitmap(SystemClock.elapsedRealtime());
            return;
        }
        mUsingAnimated = false;
        mBitmap = null;
    }
}
