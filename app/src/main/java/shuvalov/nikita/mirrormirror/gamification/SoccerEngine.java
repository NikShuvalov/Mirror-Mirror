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

    public SoccerEngine(Rect screenBounds) {
        mScreenBounds = screenBounds;
        int goalWidth = mScreenBounds.width()/10;
        mGoalBounds = new Rect(mScreenBounds.centerX()-goalWidth/2, 0, mScreenBounds.centerX()+goalWidth/2,50);
        mSoccerBall = new Ball(mScreenBounds.centerX(), mScreenBounds.centerY(), goalWidth/2, 0, -10, Color.YELLOW);
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
            mLastUpdateTime = currentTime;
        }
    }

    private double applyAirFriction(long currentTime){
        long elapsedTime = currentTime - mLastUpdateTime;
        double xSpeed = mSoccerBall.getXSpeed();
        double newSpeed;
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
        //ToDo: Figure out a way to use the deceleration formula  if xSpeed is 0;
        return (xSpeed * elapsedTime/REFRESH_DELAY);
    }

    private double applyGravity(long currentTime){
        long elapsedTime = currentTime- mLastUpdateTime;
        double ySpeed = mSoccerBall.getYSpeed();
        mSoccerBall.setYSpeed(ySpeed+ GRAVITY_ACCELERATION*elapsedTime/10);
        Log.d("Gravity", "applyGravity: "+ySpeed);
        return (ySpeed * elapsedTime/10) + (GRAVITY_ACCELERATION * (elapsedTime/10*elapsedTime/10))/2;
    }
}