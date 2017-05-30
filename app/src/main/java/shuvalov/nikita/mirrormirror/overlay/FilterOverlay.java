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
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.filters.FilterManager;
import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;
import shuvalov.nikita.mirrormirror.overlay.GraphicThread;

/**
 * Created by NikitaShuvalov on 3/24/17.
 */

public class FilterOverlay extends BaseOverlay{
    private Bitmap mBitmap;
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
        RectF face =  FaceTracker.getInstance().getFaceRect();
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        if(face!=null && mBitmap!=null){
            face.round(mRect);
            //ToDo: Consider just checking if we're using an animated filter here if issues arise. But for now it seems it would be more optimal if we update the boolean on filter change.
            if(mUsingAnimated){
                mBitmap = FilterManager.getInstance().getSelectedFilter().getBitmap(SystemClock.uptimeMillis());
            }
            canvas.drawBitmap(mBitmap, null, mRect, null);
            mRect.setEmpty();
        }
    }


    public void notifyFilterChange() {
        Filter f = FilterManager.getInstance().getSelectedFilter();
        if(f!=null) {
            mBitmap = (mUsingAnimated = f.isAnimated()) ?
                    f.getBitmap(SystemClock.uptimeMillis()) :
                    BitmapFactory.decodeResource(getResources(), f.getResourceInt());
            return;
        }
        mUsingAnimated = false;
        mBitmap = null;
    }
}
