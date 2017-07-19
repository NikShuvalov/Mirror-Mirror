package shuvalov.nikita.mirrormirror.overlay;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

import java.util.concurrent.atomic.AtomicBoolean;

import shuvalov.nikita.mirrormirror.GraphicThreadManager;

/**
 * Created by NikitaShuvalov on 3/31/17.
 */

public class GraphicThread extends Thread {
    private AtomicBoolean mStop = new AtomicBoolean();

    private GraphicThreadManager mGraphicThreadManager;

    public GraphicThread(GraphicThreadManager graphicThreadManager) {
        mGraphicThreadManager = graphicThreadManager;
        mStop.set(false);
    }

    @SuppressLint("WrongCall")
    @Override
    public void run() {
        while(!mStop.get()){
            Canvas c = null;
            try{
                c = mGraphicThreadManager.getSurfaceHolder().lockCanvas();
                synchronized (mGraphicThreadManager.getSurfaceHolder()){
                    if(!mStop.get() && mGraphicThreadManager.getBaseOverlay()!=null) mGraphicThreadManager.getBaseOverlay().onDraw(c);
                }
            }finally {
                if(c != null)mGraphicThreadManager.getSurfaceHolder().unlockCanvasAndPost(c);
            }
        }
        if(mStop.get()){
            mGraphicThreadManager.release();
        }
    }

    public void stopThread(){
        mStop.set(true);
    }

}
