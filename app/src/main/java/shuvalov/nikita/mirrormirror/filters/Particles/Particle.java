package shuvalov.nikita.mirrormirror.filters.Particles;

/**
 * Created by NikitaShuvalov on 5/7/17.
 */

public class Particle {
    private int mResourceInt;
    private float mXLoc, mYLoc, mXVel, mYVel, mScale;

    public Particle(int resourceInt, float XLoc, float YLoc, float XVel, float YVel, float scale) {
        mResourceInt = resourceInt;
        mXLoc = XLoc;
        mYLoc = YLoc;
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
}
