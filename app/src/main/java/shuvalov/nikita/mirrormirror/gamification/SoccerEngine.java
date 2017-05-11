package shuvalov.nikita.mirrormirror.gamification;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.support.annotation.Nullable;
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
    private RectF mFaceRect;
    private RectF[] mPreviousRectPositions;

    public static final int FACE_LENGTH = 400;
    public static final int SECOND = 1000;
    private long mLastUpdateTime;

    private static final double GRAVITY_ACCELERATION = 10.0/SECOND;
    private static final double AIR_FRICTION_ACCELERATION = 5.0/SECOND;
    private static final int REFRESH_DELAY = 30;
    private static final double ZERO_TOLERANCE = 0.05f;
    public static final double  BALL_RIGIDITY = 3; //Reduces the acceleration after a bounce to keep ball from bouncing indefinitely without any force being applied to it.

    public SoccerEngine(Rect screenBounds) {
        mScreenBounds = screenBounds;
        int goalWidth = mScreenBounds.width()/10;
        mGoalBounds = new Rect(mScreenBounds.centerX()-goalWidth/2, 0, mScreenBounds.centerX()+goalWidth/2,50);
        mSoccerBall = new Ball(mScreenBounds.centerX(), mScreenBounds.centerY(), goalWidth, 0, -10, Color.YELLOW);
        mLastUpdateTime = SystemClock.elapsedRealtime();
        float centerX = mScreenBounds.exactCenterX();
        float centerY = mScreenBounds.exactCenterY();
        mFaceRect = new RectF(centerX-FACE_LENGTH/2, centerY-FACE_LENGTH/2, centerX+FACE_LENGTH/2, centerY+FACE_LENGTH/2);

        /*Let's keep track of the positions of the face in the last 4 updates to get a better idea of how quickly the face is moving.
        with [0] being most recent position.
        */
        mPreviousRectPositions = new RectF[]{mFaceRect, mFaceRect, mFaceRect, mFaceRect};
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
            double yDisplacement = applyGravity(currentTime); //ToDo: Split the method into two parts, one that applies gravity, another that gets displacement distance.
            double xDisplacement = applyAirFriction(currentTime);
            mSoccerBall.moveSoccerBall(xDisplacement, yDisplacement);
            int floor = mScreenBounds.bottom;
            double bottomOfBall = mSoccerBall.getCenterY() + mSoccerBall.getRadius();
            if(mSoccerBall.intersectRect(mFaceRect)){
                if(mSoccerBall.getYSpeed()>0){
                    bounceBall((int)mFaceRect.top, bottomOfBall, calculateOpposingForce(elapsedTime, mPreviousRectPositions[3].top, mFaceRect.top));
                }
            }
            else if(bottomOfBall>floor){//Check if the ball went through the floor, if so we need to adjust the position and adjust it's vertical velocity
                bounceBall(floor, bottomOfBall, 0); //Adjusted the speed at which the ball will travel after bouncing.
            }
            mLastUpdateTime = currentTime;
        }
    }

    private double calculateOpposingForce(long elapsedTime, float previousYPos, float currentYPos){
        float distanceTraveledHorizontally = (previousYPos - currentYPos)/10;
        return distanceTraveledHorizontally * SECOND/elapsedTime;
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
            Since horizontal deceleration isn't always affecting the xSpeed and it matters less compared to vertical movement,
             I took a bit of a lazy approach and just found the average speed of before and after the acceleration is applied in order
             to figure out the xDisplacement amount.
         */
        return ((xSpeed * elapsedTime/REFRESH_DELAY)+(newSpeed * elapsedTime/REFRESH_DELAY))/2;
    }

    private double applyGravity(long currentTime){
        long elapsedTime = currentTime- mLastUpdateTime;
        double ySpeed = mSoccerBall.getYSpeed();
//        mSoccerBall.setYSpeed(ySpeed+ GRAVITY_ACCELERATION* elapsedTime);
//        return (ySpeed * elapsedTime) + (GRAVITY_ACCELERATION * (elapsedTime*elapsedTime))/2;

        mSoccerBall.setYSpeed(ySpeed+ GRAVITY_ACCELERATION*elapsedTime);
        return (ySpeed * elapsedTime/10) + (GRAVITY_ACCELERATION * (elapsedTime/10*elapsedTime/10))/2;

    }

    private void bounceBall(int bouncedSurface, double bottomOfBall, double opposingForce){
        //If the ball goes through the floor this should adjust it to the correct position as if it had bounced.
        double overBounce = bottomOfBall - bouncedSurface;
        double adjustedCenter = (bouncedSurface - mSoccerBall.getRadius()) - overBounce;
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
        double momentum = opposingForce/4;
        if(momentum<-5){
            momentum=-5;
        } else if (momentum > 5) {
            momentum = 5;
        }

        Log.d("Momentum", "bounceBall: "+momentum);
        double adjustedSpeed = yPostBounceSpeed - momentum;

        /*
        If your head goes down at same or great rate as the ball coming down, then the momentum has a chance of moving the ball with a momentum
        that will speed it up going downwards, which doesn't happen in real-life, so instead
        if the momentum would direct the acceleration downwards then we would have the ball stall with a vertical speed of 0 just like in real-life.

          */

        if(adjustedSpeed> 0 ){  //A positive speed would mean that the ball would would be heading towards bottom of screen.
            adjustedSpeed =0;
        }
        mSoccerBall.setYSpeed(adjustedSpeed);
    }

    /**
     * Updates the position of the FaceRect as seen by the soccerEngine. If a face isn't detected we keep the rectangle where the face was at last update.
     *
     * @param faceRect The updated faceRect
     */
    public void updateFacePosition(@Nullable RectF faceRect){
        if(faceRect!=null){
            mFaceRect = faceRect;
        }
        updateFaceRectCache();
    }

    public void updateFaceRectCache(){
        for(int i =mPreviousRectPositions.length-1; i>0; i --){
            mPreviousRectPositions[i] = mPreviousRectPositions[i-1];
        }
        mPreviousRectPositions[0] = mFaceRect;
    }

}