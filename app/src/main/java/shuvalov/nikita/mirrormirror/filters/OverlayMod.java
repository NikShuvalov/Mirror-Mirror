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
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.filters.FilterManager;
import shuvalov.nikita.mirrormirror.filters.GraphicThread;

/**
 * Created by NikitaShuvalov on 3/24/17.
 */

public class OverlayMod extends SurfaceView implements SurfaceHolder.Callback{
    private GraphicThread mGraphicThread;
    private Bitmap mBitmap;
    private Rect mRect;
    private boolean mUsingAnimated;

    public OverlayMod(Context context) {
        super(context);
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(this);

        Filter f = FilterManager.getInstance().getSelectedFilter();
        if(mUsingAnimated = f.isAnimated()){
            mBitmap = f.getBitmap(System.currentTimeMillis());
        }else{
            mBitmap = BitmapFactory.decodeResource(getResources(), f.getResourceInt());
        }
        mRect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF face = FaceTracker.getInstance().getFaceRect();
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        if(face!=null){
            face.round(mRect);
            //ToDo: Consider just checking if we're using an animated filter here if issues arise. But for now it seems it would be more optimal if we update the boolean on filter change.
            if(mUsingAnimated){
                mBitmap = FilterManager.getInstance().getSelectedFilter().getBitmap(System.currentTimeMillis());
            }
            canvas.drawBitmap(mBitmap, null, mRect, null);
            mRect.setEmpty();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if(mGraphicThread!=null) return;

        mGraphicThread= new GraphicThread(surfaceHolder, this);
        mGraphicThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mGraphicThread.stopThread();
    }

    public void notifyFilterChange() {
        Filter f = FilterManager.getInstance().getSelectedFilter();
        if(mUsingAnimated = f.isAnimated()){
            mBitmap = f.getBitmap(System.currentTimeMillis());
        }else{
            mBitmap = BitmapFactory.decodeResource(getResources(), f.getResourceInt());
        }
    }
}
