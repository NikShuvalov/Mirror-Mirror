package shuvalov.nikita.mirrormirror.filters.Particles;

import android.graphics.Rect;

/**
 * Created by NikitaShuvalov on 5/7/17.
 */

public class Particle {
    private int mResourceInt;
    private float mXLoc, mYLoc, mXVel, mYVel, mScale, mStartX, mStartY;
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
    public Particle(int resourceInt, float startX, float startY, float XVel, float YVel, float scale) {
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
    public void translatePosition(float x, float y){
        mXLoc+=x;
        mYLoc+=y;
    }

    public float getXLoc() {
        return mXLoc;
    }

    public float getYLoc() {
        return mYLoc;
    }

    public float getXVel() {
        return mXVel;
    }

    public float getYVel() {
        return mYVel;
    }

    public void setXLoc(float XLoc) {
        mXLoc = XLoc;
    }

    public void setYLoc(float YLoc) {
        mYLoc = YLoc;
    }

    public void setScale(float scale) {
        mScale = scale;
    }

    public float getScale() {
        return mScale;
    }

    public boolean isOutOfBounds(Rect screenBounds){
        return screenBounds.contains((int)mXLoc, (int)mYLoc);
    }

}
