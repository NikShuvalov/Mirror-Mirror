package shuvalov.nikita.mirrormirror.gamification;

import android.graphics.Paint;
import android.graphics.RectF;

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

    /**
     * The logic behind establishing whether the ball was hit by the face.
     * If the top of the ball is below the top of the face, then it already doesn't count as contact, otherwise follow the below logic.
     *
     * If Ball(left) is right of Face(left) and Ball(Right) is left of Face(right) then contact was made
     * If Ball(left) is left of Face(left), and Ball(right) is right of Face(Left), or vice versa, that means face made contact.
     * If neither of those conditions are met the ball does not make contact.
     * @param rectF Rectangle of face
     * @return true if face made contact with ball
     */
    public boolean intersectRect(RectF rectF){
        float topOfBall = (float) (mCenterY-mRadius);
        float bottomOfBall = (float) (mCenterY+mRadius);
        float topOfFace = rectF.top;
        if(topOfBall>topOfFace || bottomOfBall< topOfFace){ //Since Y increases downwards.
            return false;
        }
        double ballRight = mCenterX+mRadius;
        double ballLeft = mCenterX-mRadius;
        return !(ballRight < rectF.left || ballLeft > rectF.right);
    }
}
