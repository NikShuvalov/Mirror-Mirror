package shuvalov.nikita.mirrormirror.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.SystemClock;

import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;


/**
 * Created by NikitaShuvalov on 7/14/17.
 */

public class VideoOverlay extends BaseOverlay{
    private Bitmap mBitmap;
    private boolean mUsingAnimated;
    private Rect mRect;

    public VideoOverlay(Context context) {
        super(context);
        notifyFilterChange();
        mRect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.getClipBounds(mRect);
        Filter f = VideoFilterManager.getInstance().getSelectedFilter();
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        if(mBitmap!=null){
            if(mUsingAnimated){
                mBitmap = VideoFilterManager.getInstance().getSelectedFilter().getBitmap(SystemClock.uptimeMillis());
            }
            switch(f.getFilterType()){
                case FULL:
                    canvas.drawBitmap(mBitmap, null, mRect,null);
                    break;
                case CENTER:
                    int centerX = mRect.centerX();
                    int centerY = mRect.centerY();
                    int width = mRect.width()/3;
                    int height = mRect.height()/3;
                    mRect.set(centerX-Math.min(width, height), centerY- Math.min(width, height), centerX + Math.min(width, height), centerY+ Math.min(width, height));
                    canvas.drawBitmap(mBitmap, null, mRect, null);
                    break;
                case BANNER_TOP:
                    mRect.set(mRect.left, 0,mRect.right,mRect.height()/5);
                    canvas.drawBitmap(mBitmap,null, mRect, null);
                    break;
                case BANNER_BOTTOM:
                    int top = (int)(mRect.height()*.8f);
                    mRect.set(mRect.left, top ,mRect.right,mRect.bottom);
                    canvas.drawBitmap(mBitmap, null, mRect,null);
                    break;
            }
        }
        mRect.setEmpty();
    }

    public void notifyFilterChange() {
        Filter f = VideoFilterManager.getInstance().getSelectedFilter();
        if(f!=null) {
            mBitmap = f.getBitmap();
            return;
        }
        mUsingAnimated = false;
        mBitmap = null;
    }


}
