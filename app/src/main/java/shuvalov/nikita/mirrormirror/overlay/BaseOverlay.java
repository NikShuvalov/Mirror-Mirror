package shuvalov.nikita.mirrormirror.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import shuvalov.nikita.mirrormirror.overlay.GraphicThread;

/**
 * Created by NikitaShuvalov on 5/8/17.
 */

public abstract class BaseOverlay extends SurfaceView implements SurfaceHolder.Callback {

    private GraphicThread mGraphicThread;

    public BaseOverlay(Context context) {
        super(context);
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if(mGraphicThread!=null) {return;}
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
