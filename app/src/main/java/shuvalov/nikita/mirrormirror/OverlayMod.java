package shuvalov.nikita.mirrormirror;

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

/**
 * Created by NikitaShuvalov on 3/24/17.
 */

public class OverlayMod extends SurfaceView implements SurfaceHolder.Callback{
    private GraphicThread mGraphicThread;
    private Bitmap mBitmap;



    public OverlayMod(Context context) {
        super(context);
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(this);

        int resId = FilterManager.getInstance().getSelectedRes();
        mBitmap = BitmapFactory.decodeResource(getResources(), resId);
    }

    //FixMe: X and Y seem to be inverted.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF face = FaceTracker.getInstance().getFaceRect();
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        if(face!=null){
            Rect rect = new Rect();
            face.round(rect);
            canvas.drawBitmap(mBitmap, null, rect, null);
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
        int resId = FilterManager.getInstance().getSelectedRes();
        mBitmap = BitmapFactory.decodeResource(getResources(), resId);
    }
}
