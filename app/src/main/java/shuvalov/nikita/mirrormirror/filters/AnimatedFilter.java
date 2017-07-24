package shuvalov.nikita.mirrormirror.filters;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.SystemClock;

import java.util.List;
import java.util.Random;

import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;

/**
 * Created by NikitaShuvalov on 5/4/17.
 */

public class AnimatedFilter extends Filter {
    private List<Bitmap> mAnimationList;
    private int mIndex;
    private long mLastFrameMillis;

    private static final int MILLIS_PER_FRAME = 200;


    public AnimatedFilter(String filterName, Bitmap initialBitmap, FilterType filterType, float scaleX, float scaleY, float offsetXPercent, float offsetYPercent, List<Bitmap> animationList) {
        super(filterName, initialBitmap, filterType, scaleX, scaleY, offsetXPercent, offsetYPercent);
        mAnimationList = animationList;
        mIndex = 0;
        mLastFrameMillis = 0;
    }

    @Override
    public Bitmap getBitmap() {
        return super.getBitmap();
    }

    @Override
    public Bitmap getBitmap(long currentMillis) {
        if(mLastFrameMillis == 0){
            mLastFrameMillis = currentMillis;
        }
        else if(mLastFrameMillis+MILLIS_PER_FRAME<currentMillis){
            moveToNextFrame();
            mLastFrameMillis = currentMillis;
        }
        return mAnimationList.get(mIndex);
    }


    private void moveToNextFrame(){
        mIndex++;
        if(mIndex == mAnimationList.size()){
            mIndex=0;
        }
    }

    @Override
    public void drawFilterToCanvas(Canvas canvas) {
        RectF face = FaceTracker.getInstance().getFaceRect();
        Bitmap bp = getBitmap(SystemClock.elapsedRealtime());
        if(face!=null){
            canvas.drawBitmap(bp, null, face, null);
        }
    }

    public AnimatedFilter randomizeStartFrame (){
        mIndex = new Random().nextInt(mAnimationList.size()-1);
        return this;
    }
}
