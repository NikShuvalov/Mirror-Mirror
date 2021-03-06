package shuvalov.nikita.mirrormirror.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import shuvalov.nikita.mirrormirror.GraphicThreadManager;
import shuvalov.nikita.mirrormirror.overlay.GraphicThread;

/**
 * Created by NikitaShuvalov on 5/8/17.
 */

public abstract class BaseOverlay extends SurfaceView implements SurfaceHolder.Callback {
    private GraphicThread mGraphicThread;
    private GraphicThreadManager mGraphicThreadManager;

    public BaseOverlay(Context context) {
        super(context);
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(this);
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if(mGraphicThread!=null) {return;}
        if(mGraphicThreadManager==null){
            mGraphicThreadManager = new GraphicThreadManager(this, surfaceHolder);
        }else{
            mGraphicThreadManager.setValues(this, surfaceHolder);
        }
        mGraphicThread= new GraphicThread(mGraphicThreadManager);
        mGraphicThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void stopGraphicThread(){
        if(mGraphicThread!=null && mGraphicThread.isAlive()){
            mGraphicThread.stopThread();
        }
    }
}
