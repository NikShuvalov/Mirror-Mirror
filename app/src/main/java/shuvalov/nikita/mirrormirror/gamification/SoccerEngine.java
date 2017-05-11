package shuvalov.nikita.mirrormirror.gamification;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by NikitaShuvalov on 5/10/17.
 */

/*
    Soccer Regulation dictates that a goal is 8 yards wide, while the width of the field is 70-80 yards wide, so let's just keep it simple
    and say that the goal is 1/10th of the width of the field.
    A typical soccer ball is 22cm which is 0.25 yards. But to make things simpler we can increase the size of the ball several-fold. Make the
    ball a 1/4 of the size of the goal.

    Distance determined for an accelerating object:
    distance = initial velocity * Time + (acceleration*Time^2)/2
 */
public class SoccerEngine {
    private Rect mScreenBounds, mGoalBounds;
    private Ball mSoccerBall;

    private long mLastUpdateTime;

    private static final double GRAVITY_ACCELERATION = 30.0/500; //Let's say per half a second
    private static final double AIR_FRICTION_ACCELERATION = 10.0/500; //per 500ms
    private static final int REFRESH_DELAY = 30;
    private static final double ZERO_TOLERANCE = 0.05f;
    public static final double  BALL_RIGIDITY = 3; //Reduces the acceleration after a bounce to keep ball from bouncing indefinitely without any force being applied to it.

    public SoccerEngine(Rect screenBounds) {
        mScreenBounds = screenBounds;
        int goalWidth = mScreenBounds.width()/10;
        mGoalBounds = new Rect(mScreenBounds.centerX()-goalWidth/2, 0, mScreenBounds.centerX()+goalWidth/2,50);
        mSoccerBall = new Ball(mScreenBounds.centerX(), mScreenBounds.centerY(), goalWidth, 0, -10, Color.YELLOW);
        mLastUpdateTime = SystemClock.elapsedRealtime();
    }

    public Rect getScreenBounds() {
        return mScreenBounds;
    }

    public Rect getGoalBounds() {
        return mGoalBounds;
    }

    public Ball getSoccerBall() {
        return mSoccerBall;
    }

    public void moveSoccerBall(){
        long currentTime = SystemClock.elapsedRealtime();
        long elapsedTime = currentTime- mLastUpdateTime;
        if(elapsedTime> REFRESH_DELAY){
            double yDisplacement = applyGravity(currentTime);
            double xDisplacement = applyAirFriction(currentTime);
            mSoccerBall.moveSoccerBall(xDisplacement, yDisplacement);
            int floor = mScreenBounds.bottom;
            double bottomOfBall = mSoccerBall.getCenterY() + mSoccerBall.getRadius();

            if(bottomOfBall>floor){//Check if the ball went through the floor, if so we need to adjust the position and adjust it's vertical velocity
                bounceBall(floor, bottomOfBall); //Adjusted the speed at which the ball will travel after bouncing.
                Log.d("ball", "floor: "+ floor);
                Log.d("ball", "bottomOfBall: "+ bottomOfBall);
            }
            mLastUpdateTime = currentTime;
        }
    }

    private double applyAirFriction(long currentTime){
        long elapsedTime = currentTime - mLastUpdateTime;
        double xSpeed = mSoccerBall.getXSpeed();
        double newSpeed = xSpeed;
        if(xSpeed>0){ //We are looking to move the value towards 0 with air Friction. So if value is neg, add air friction to speed, otherwise minus it.
            newSpeed = xSpeed+(AIR_FRICTION_ACCELERATION*elapsedTime/REFRESH_DELAY);
            if(Math.abs(newSpeed)<ZERO_TOLERANCE) {//Taking a page out of Google's book/sample code, reduce the xSpeed to 0 if it's a trivially close to 0 already.
                newSpeed =0;
            }
            mSoccerBall.setXSpeed(newSpeed);
        }else if (xSpeed<0){
            newSpeed = xSpeed-(AIR_FRICTION_ACCELERATION*elapsedTime/REFRESH_DELAY);
            if(Math.abs(newSpeed)<ZERO_TOLERANCE){
                newSpeed =0;
            }
            mSoccerBall.setXSpeed(newSpeed);
        }
        /*
            Since horizontal decceleration isn't always affecting the xSpeed and it matters less compared to vertical movement,
             I took a bit of a lazy approach and just found the average speed of before and after the acceleration is applied in order
             to figure out the xDisplacement amount.
         */
        return ((xSpeed * elapsedTime/REFRESH_DELAY)+(newSpeed * elapsedTime/REFRESH_DELAY))/2;
    }

    private double applyGravity(long currentTime){
        long elapsedTime = currentTime- mLastUpdateTime;
        double ySpeed = mSoccerBall.getYSpeed();
        mSoccerBall.setYSpeed(ySpeed+ GRAVITY_ACCELERATION*elapsedTime/10);
        return (ySpeed * elapsedTime/10) + (GRAVITY_ACCELERATION * (elapsedTime/10*elapsedTime/10))/2;
    }

    private void bounceBall(int floor, double bottomOfBall){
        //If the ball goes through the floor this should adjust it to the correct position as if it had bounced.
        double overBounce = bottomOfBall - floor;
        double adjustedCenter = (floor - mSoccerBall.getRadius()) - overBounce;
        mSoccerBall.setCenterY(adjustedCenter);

        //This reverses the speed of the ball as well as applying the ball's rigidity to dampen the speed after a bounce. Less Rigid balls keep more of their bounce.
        double yPreBounceSpeed = mSoccerBall.getYSpeed();
        double yPostBounceSpeed;
        if(BALL_RIGIDITY>= Math.abs(yPreBounceSpeed)){
            yPostBounceSpeed = 0;
        }else{
            double bounceFrictionApplied = Math.abs(yPreBounceSpeed)-BALL_RIGIDITY;
            yPostBounceSpeed = bounceFrictionApplied*-1;
        }
        mSoccerBall.setYSpeed(yPostBounceSpeed);
    }

}