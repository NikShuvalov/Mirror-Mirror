package shuvalov.nikita.mirrormirror.filters.particles;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Random;

/**
 * Created by NikitaShuvalov on 5/7/17.
 */

public class Particle {
    private List<Bitmap> mBitmapList;
    private double mXLoc, mYLoc, mXVel, mYVel, mScale, mStartX, mStartY;
    public static final long REFRESH_RATE = 30; //Target FPS
    private long mLastUpdate;
    private int mIndex;
    private static final long mMillisPerFrame =200;
    private boolean mAnimated;
    private int mMaxParticles;
    private String mName;


    /**
     * Creates a particle to be used by the particle engine for display.
     *
     * @param bitmaps The different appearances of the particles
     * @param animated If animated particle changes appearances as it moves, otherwise it changes appearance only on resetting.
     * @param XVel Number of pixels to move based on refresh rate, a positive value means moving to the right; negative to the left.
     * @param YVel Number of pixels to moved based on refresh rate, a positive value means falling on the screen, a negative value means rising
     * @param scale The scale emulates the distance of the particle from the lens
     *              and also used to determine the speed of which the particle moves
     *              for a parallaxing effect.
     *@param maxParticles Max number of particles allowed to be populated for this particle.
     */
    public Particle(String name, List<Bitmap> bitmaps, boolean animated,  double XVel, double YVel, double scale, int maxParticles) {
        mName = name;
        mBitmapList = bitmaps;

        mStartX= 0;
        mStartY = 0;
        mXLoc = mStartX;
        mYLoc = mStartY;

        mXVel = XVel;
        mYVel = YVel;

        mScale = scale;

        mAnimated = animated;
        mLastUpdate = 0;
        mIndex = 0;
        mMaxParticles = maxParticles;
    }

    public Bitmap getCurrentBitMap(long currentTime) {
        if(mAnimated){
            if(mLastUpdate == 0){
                mLastUpdate = currentTime;
            }else if (mLastUpdate + mMillisPerFrame < currentTime) {
                mIndex++;
                if(mIndex>= mBitmapList.size()){
                    mIndex = 0;
                }
            }
        }
        return mBitmapList.get(mIndex);
    }

    public void randomizeBitmap(){
        Random rng = new Random();
        mIndex = rng.nextInt(mBitmapList.size());
    }
    /**
     * Adds the translation x and y amounts to the current x and y positions of the particle.
     *
     * @param x
     * @param y
     */
    public void translatePosition(double x, double y){
        //Portrait mode makes things confusing with this api.
        mXLoc+=x;
        mYLoc+=y;
    }

    public double getXLoc() {
        return mXLoc;
    }

    public double getYLoc() {
        return mYLoc;
    }

    public double getXVel() {
        return mXVel;
    }

    public double getYVel() {
        return mYVel;
    }

    public void setXLoc(double XLoc) {
        mXLoc = XLoc;
    }

    public void setYLoc(double YLoc) {
        mYLoc = YLoc;
    }

    public void setScale(double scale) {
        mScale = scale;
    }

    public double getScale() {
        return mScale;
    }

    public double getStartX() {
        return mStartX;
    }

    public double getStartY() {
        return mStartY;
    }

    public boolean isOutOfBounds(Rect screenBounds){
        return !screenBounds.contains((int)mXLoc, (int)mYLoc);
    }

    public boolean isVerticallyOutOfFrame(Rect screenBounds){
        return mYLoc > screenBounds.bottom;
    }

    public Particle makeCarbonCopy(){
        return new Particle(mName, mBitmapList, mAnimated, mXVel, mYVel, mScale, mMaxParticles);
    }

    public void setStartX(double startX) {
        mStartX = startX;
    }

    public void moveStartX(double xShift){ mStartX+=xShift;}

    public void setStartY(double startY) {
        mStartY = startY;
    }

    public void resetXToStart(){
        mXLoc = mStartX;
    }

    public int getMaxParticles() {
        return mMaxParticles;
    }

    public String getName() {
        return mName;
    }

    public Bitmap getPreviewBitmap(){
        return mBitmapList.get(0);
    }
}
