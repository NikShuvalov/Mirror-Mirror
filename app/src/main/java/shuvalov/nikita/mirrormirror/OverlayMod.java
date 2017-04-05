package shuvalov.nikita.mirrormirror;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
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
    private Rect mRect;

    public OverlayMod(Context context) {
        super(context);
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(this);

        int resId = FilterManager.getInstance().getSelectedRes();
        mBitmap = BitmapFactory.decodeResource(getResources(), resId);
        mRect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF face = FaceTracker.getInstance().getFaceRect();
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        if(face!=null){
                face.round(mRect);
                canvas.drawBitmap(mBitmap, null, mRect, null);
                mRect.setEmpty();
//                Point[] eyes = FaceTracker.getInstance().getEyes();
//                for(Point eye: eyes){
//                    if(eye!=null) {
//                        float w = face.width();
//                        float iris = w / 20;
//                        int left = (int) (eye.x - iris);
//                        int top = (int) (eye.y - iris);
//                        int right = (int) (eye.x + iris);
//                        int bottom = (int) (eye.y + iris);
//                        mRect.set(left, top, right, bottom);
//                        canvas.drawBitmap(mBitmap, null, mRect, null);
//                    }
//                }
//                mRect.setEmpty();
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
