package shuvalov.nikita.mirrormirror.filters;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.List;

/**
 * Created by NikitaShuvalov on 5/4/17.
 */

public class AnimatedFilter extends Filter {
    private List<Bitmap> mAnimationList;
    private int mIndex;
    private long mLastFrameMillis, mMillisPerFrame;


    public AnimatedFilter(String filterName, int resourceInt, ImagePosition imagePosition, float scaleX, float scaleY, float offsetXPercent, float offsetYPercent, List<Bitmap> animationList) {
        super(filterName, resourceInt, imagePosition, scaleX, scaleY, offsetXPercent, offsetYPercent);
        mAnimationList = animationList;
        mIndex = 0;
        mMillisPerFrame = 400; //ToDo: Change from hard-coded value.
        mLastFrameMillis = 0;
    }

    @Override
    public Bitmap getBitmap(long currentMillis) {
        if(mLastFrameMillis == 0){
            mLastFrameMillis = currentMillis;
        }
        else if(mLastFrameMillis+mMillisPerFrame<currentMillis){
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
    public boolean isAnimated() {
        return true;
    }

}
