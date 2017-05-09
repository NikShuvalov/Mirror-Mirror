package shuvalov.nikita.mirrormirror.filters.particles;

import android.graphics.Rect;

/**
 * Created by NikitaShuvalov on 5/7/17.
 */

public class Particle {
    private int mResourceInt;
    private double mXLoc, mYLoc, mXVel, mYVel, mScale, mStartX, mStartY;
    public static final long REFRESH_RATE = 30; //Target FPS


    /**
     * Creates a particle to be used by the particle engine for display.
     *
     * @param resourceInt Image Resources
     * @param startX At what horizontal position the particle starts.
     * @param startY At what Vertical position the particle starts. Typically it'll be 0 if particle is falling, screenheight if particle is rising.
     * @param XVel Number of pixels to move based on refresh rate, a positive value means moving to the right; negative to the left.
     * @param YVel Number of pixels to moved based on refresh rate, a positive value means falling on the screen, a negative value means rising
     * @param scale The scale emulates the distance of the particle from the lens
     *              and also used to determine the speed of which the particle moves
     *              for a parallaxing effect.
     *
     */
    public Particle(int resourceInt, double startX, double startY, double XVel, double YVel, double scale) {
        mResourceInt = resourceInt;

        mStartX= startX;
        mStartY = startY;

        mXLoc = startX;
        mYLoc = startY;

        mXVel = XVel;
        mYVel = YVel;

        mScale = scale;
    }



    public int getResourceInt() {
        return mResourceInt;
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

    //ToDo: Add a buffer of space to allow the particle to go off screen cause it might be able to come back on screen.
    public boolean isOutOfBounds(Rect screenBounds){
        return !screenBounds.contains((int)mXLoc, (int)mYLoc);
    }

    public boolean isVerticallyOutOfFrame(Rect screenBounds){
        return mYLoc > screenBounds.bottom;
    }

    public Particle makeCarbonCopy(){
        return new Particle(mResourceInt, mStartX, mStartY, mXVel, mYVel, mScale);
    }

    public void setStartX(double startX) {
        mStartX = startX;
    }

    public void setStartY(double startY) {
        mStartY = startY;
    }

    public void resetXToStart(){
        mXLoc = mStartX;
    }
}
