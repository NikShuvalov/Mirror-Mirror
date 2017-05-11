package shuvalov.nikita.mirrormirror.gamification;

import android.graphics.Paint;

/**
 * Created by NikitaShuvalov on 5/10/17.
 */

public class Ball {
    private double mCenterX, mCenterY, mRadius, mXSpeed, mYSpeed; //Charlie used pxPerMs for speed
    private Paint mPaint;

    public Ball(double centerX, double centerY, double radius, double xSpeed, double ySpeed, int color) {
        mCenterX = centerX;
        mCenterY = centerY;
        mRadius = radius;

        //Instead of using direction, I'm going to indicate vector velocity for horizontal and vertical motion since those 2 values will be affected by gravity and friction.
        mXSpeed = xSpeed;
        mYSpeed = ySpeed;
        mPaint = new Paint();
        mPaint.setColor(color);
    }

    public double getCenterX() {
        return mCenterX;
    }

    public double getCenterY() {
        return mCenterY;
    }

    public void setCenterX(double centerX) {
        mCenterX = centerX;
    }

    public void setCenterY(double centerY) {
        mCenterY = centerY;
    }

    public double getRadius() {
        return mRadius;
    }

    public double getXSpeed() {
        return mXSpeed;
    }

    public double getYSpeed() {
        return mYSpeed;
    }

    public void setXSpeed(double XSpeed) {
        mXSpeed = XSpeed;
    }

    public void setYSpeed(double YSpeed) {
        mYSpeed = YSpeed;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public void moveSoccerBall(double x, double y){
        mCenterX+=x;
        mCenterY+=y;
    }
}
