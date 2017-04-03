package shuvalov.nikita.mirrormirror;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by NikitaShuvalov on 3/31/17.
 */

public class GraphicThread extends Thread {
    private final SurfaceHolder mSurfaceHolder;
    private OverlayMod mOverlayMod;
    private AtomicBoolean mStop = new AtomicBoolean();

    public GraphicThread(SurfaceHolder surfaceHolder, OverlayMod overlayMod) {
        mSurfaceHolder = surfaceHolder;
        mOverlayMod = overlayMod;
        mStop.set(false);
    }

    @SuppressLint("WrongCall")
    @Override
    public void run() {
        while(!mStop.get()){
            Canvas c = null;
            try{
                c = mSurfaceHolder.lockCanvas();
                synchronized (mSurfaceHolder){
                    if(!mStop.get()) mOverlayMod.onDraw(c);
                }
            }finally {
                if(c != null)mSurfaceHolder.unlockCanvasAndPost(c);
            }
        }
    }

    public void stopThread(){
        mStop.set(true);
    }

}
