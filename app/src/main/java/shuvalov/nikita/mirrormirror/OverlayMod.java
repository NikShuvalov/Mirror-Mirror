package shuvalov.nikita.mirrormirror;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by NikitaShuvalov on 3/24/17.
 */

public class OverlayMod extends SurfaceView implements SurfaceHolder.Callback {
    private int mX, mY;
    private SurfaceHolder mSurfaceHolder;



    public OverlayMod(Context context) {
        super(context);
        mSurfaceHolder = getHolder();
    }


    public void drawSomething(int x, int y){
        Canvas canvas = mSurfaceHolder.lockCanvas();
        mX = x;
        mY = y;
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(30.0f);
        canvas.drawRect(mX+50,mY-50, mX-50, mY+50, paint);
        this.draw(canvas);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
