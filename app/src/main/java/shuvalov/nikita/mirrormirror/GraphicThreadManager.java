package shuvalov.nikita.mirrormirror;

import android.view.SurfaceHolder;

import java.util.concurrent.atomic.AtomicBoolean;

import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;

/**
 * Created by NikitaShuvalov on 7/19/17.
 */

public class GraphicThreadManager {
    private BaseOverlay mBaseOverlay;
    private SurfaceHolder mSurfaceHolder;

    public GraphicThreadManager(BaseOverlay baseOverlay, SurfaceHolder surfaceHolder) {
        mBaseOverlay = baseOverlay;
        mSurfaceHolder = surfaceHolder;
    }

    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }

    public BaseOverlay getBaseOverlay() {
        return mBaseOverlay;
    }

    public void setValues(BaseOverlay baseOverlay, SurfaceHolder surfaceHolder){
        mBaseOverlay = baseOverlay;
        mSurfaceHolder = surfaceHolder;
    }

    public void release(){
        mBaseOverlay = null;
        mSurfaceHolder = null;
    }
}
