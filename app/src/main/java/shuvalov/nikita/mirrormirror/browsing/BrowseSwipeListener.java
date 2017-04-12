package shuvalov.nikita.mirrormirror.browsing;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by NikitaShuvalov on 4/12/17.
 */

public class BrowseSwipeListener implements View.OnTouchListener {
    private static final int MIN_SWIPE_DISTANCE = 50;
    private float mDownX, mDownY, mUpX, mUpY;
    private OnSwipeListener mSwipeListener;

    public BrowseSwipeListener(OnSwipeListener swipeListener) {
        mSwipeListener = swipeListener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = motionEvent.getX();
                mDownY = motionEvent.getY();
                return true;
            case MotionEvent.ACTION_UP:
                mUpX = motionEvent.getX();
                mUpY = motionEvent.getY();
                return countsAsSwipe();
        }
        return false;
    }

    private boolean countsAsSwipe(){
        //If positive Y swipe is downward, if positive X swipe is rightward
        float deltaX = mUpX- mDownX;
        float deltaY = mUpY - mDownY;
        SWIPE_DIRECTION swipeDirection = SWIPE_DIRECTION.CLICK;
        if(Math.abs(deltaX) > MIN_SWIPE_DISTANCE || Math.abs(deltaY) > MIN_SWIPE_DISTANCE){
            Log.d("Swipe", "countsAsSwipe: True");
            if(deltaX>0){
                swipeDirection= SWIPE_DIRECTION.RIGHT;
            }else{
                swipeDirection = SWIPE_DIRECTION.LEFT;
            }
            mSwipeListener.onSwipe(swipeDirection);
            return true;
        }
        mSwipeListener.onSwipe(swipeDirection);
        return false;
    }


    public enum SWIPE_DIRECTION{
        RIGHT, LEFT, UP, DOWN, CLICK;
    }

    public interface OnSwipeListener{
        void onSwipe(SWIPE_DIRECTION d);
    }
}
