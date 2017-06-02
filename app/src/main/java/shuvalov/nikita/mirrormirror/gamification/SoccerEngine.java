package shuvalov.nikita.mirrormirror.gamification;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Random;

import shuvalov.nikita.mirrormirror.filters.particles.Particle;

import static android.content.ContentValues.TAG;

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

/*
Idea: If I do a survival mode, remove the bounce friction and head momentum,
so that people can't just balance it on their head and score a whole bunch of points.
Or, have the ball need to travel some distance up after a hit to count towards a point.

 */



    //Idea; Make balls progressively smaller
    //Idea 2: Color balls and goals, spawn several balls and have player get balls into corresponding color goals.
public class SoccerEngine {

    // ============================================= Variables===============================================
    private Rect mScreenBounds, mGoalBounds;

    private Ball mSoccerBall;
    private float mSoccerRadius, mGoalWidth;
    private RectF mFaceRect;
    private RectF[] mPreviousRectPositions;
    private int mPlayerScore;
    private boolean mIsSurvivalMode, mBallHit, mBallDisappearing, mGoalMoving;
    private long mLastUpdateTime;
    private PointF[] mGoalInterpolation;
    private int mGoalIndex;
    private int mBoundaryLine;

    public static final int FACE_LENGTH = 400;
    public static final int SECOND = 1000;
    private static final double MAX_DEFLECTION_VALUE = 15.0;
    private static final double GRAVITY_ACCELERATION = 10.0/SECOND;
    private static final double AIR_FRICTION_ACCELERATION = 5.0/SECOND;
    private static final int REFRESH_DELAY = 30;
    private static final double ZERO_TOLERANCE = 0.05f;
    private static final double  BALL_RIGIDITY = 3; //Reduces the acceleration after a bounce to keep ball from bouncing indefinitely without any force being applied to it.

    public enum Wall {
        LEFT_SIDE, RIGHT_SIDE
    }

    // ======================================== Constructor/Set-Up ==================================================

    public SoccerEngine(Rect screenBounds) {
        mScreenBounds = screenBounds;
        mGoalWidth = mScreenBounds.width()/6;
        mSoccerRadius = mGoalWidth/3;
        mGoalInterpolation = new PointF[10];

        spawnNewGoal();
        respawnBall();

        float centerX = mScreenBounds.exactCenterX();
        float centerY = mScreenBounds.exactCenterY();
        mFaceRect = new RectF(centerX-FACE_LENGTH/2, centerY-FACE_LENGTH/2, centerX+FACE_LENGTH/2, centerY+FACE_LENGTH/2);
        gameStart();

        /*Let's keep track of the positions of the face in the last 4 updates to get a better idea of how quickly the face is moving.
        with [0] being most recent position.
        */
        mPreviousRectPositions = new RectF[]{mFaceRect, mFaceRect, mFaceRect, mFaceRect};
    }


    public void gameStart(){
        mIsSurvivalMode = false;
        mPlayerScore = 0;
        mBallDisappearing = false;
        mGoalMoving = false;
        mLastUpdateTime = SystemClock.elapsedRealtime();
        mBoundaryLine = mScreenBounds.centerY();// x = y, y = x, cause why, I don't know.
    }

    // ======================================== Getter Methods ==================================================

    public Rect getScreenBounds() {
        return mScreenBounds;
    }

    public Rect getGoalBounds() {
        return mGoalBounds;
    }

    public Ball getSoccerBall() {
        return mSoccerBall;
    }

    public int getPlayerScore(){
        return mPlayerScore;
    }


    public RectF getFaceRect() {
        return mFaceRect;
    }

    public int getBoundaryLine() {
        return mBoundaryLine;
    }

    // ======================================== Main Move Method==================================================

    public void process(){
        if(mBallDisappearing){
            disappearBall();
        }else if (mGoalMoving){
            moveGoal();
        }else {
            moveSoccerBall();
        }
    }

    private void disappearBall(){
        if(mSoccerBall.getShrinkage() <6){
            mSoccerBall.shrinkBall();
        }else{
            mSoccerBall.setCenterX(-1000);
            mBallDisappearing=false;
            mSoccerBall.resetShrinkage();
            Rect targetLocation = generateGoalLocation();
            generateGoalInterpolatedPath(new PointF(mGoalBounds.centerX(), mGoalBounds.centerY()),new PointF(targetLocation.centerX(), targetLocation.centerY()));
        }
    }

    private void moveGoal(){
        if(mGoalIndex< mGoalInterpolation.length){
            PointF nextSpot = mGoalInterpolation[mGoalIndex];
            mGoalBounds.offset((int)nextSpot.x -  mGoalBounds.centerX(), (int)nextSpot.y -  mGoalBounds.centerY());
            mGoalIndex++;
        }else{
            PointF finalPoint = mGoalInterpolation[mGoalInterpolation.length-1];
            mGoalBounds.offset( (int)finalPoint.x - mGoalBounds.centerX(), (int)finalPoint.y - mGoalBounds.centerY());
            mGoalMoving = false;
            mGoalIndex =0;
            respawnBall();
            mLastUpdateTime = SystemClock.elapsedRealtime();
        }
    }

    private void moveSoccerBall(){
        long currentTime = SystemClock.elapsedRealtime();
        long elapsedTime = currentTime - mLastUpdateTime;
        if (elapsedTime > REFRESH_DELAY) {
            double yDisplacement = getYDisplacement(elapsedTime);
            double xDisplacement = getXDisplacement(elapsedTime);
            mSoccerBall.moveSoccerBall(xDisplacement, yDisplacement);
            applyGravity(elapsedTime);
            int floor = mScreenBounds.bottom;
            double bottomOfBall = mSoccerBall.getCenterY() + mSoccerBall.getRadius();
            if (mSoccerBall.intersectRect(mFaceRect)) { //Logic if ball hits face
                mBallHit = true;
                if (mSoccerBall.getYSpeed() > 0) {
                    if (mIsSurvivalMode) { //ToDo: Need to remove bounceFriction as well, except for maybe the side bounces.
                        verticalBallBounce((int) mFaceRect.top, bottomOfBall, 0);
                    } else {
                        verticalBallBounce((int) mFaceRect.top, bottomOfBall, calculateOpposingForce(elapsedTime, mPreviousRectPositions[3].top, mFaceRect.top));
                    }
                }
                skewForAngledBounce();
            } else if (bottomOfBall > floor) {//Check if the ball went through the floor, if so we need to adjust the position and adjust it's vertical velocity
                verticalBallBounce(floor, bottomOfBall, 0); //Adjusted the speed at which the ball will travel after bouncing.
            }
            double radius = mSoccerBall.getRadius();
            double centerX = mSoccerBall.getCenterX();
            double leftOfBall = centerX - radius;
            double rightOfBall = centerX + radius;

            if (leftOfBall < mScreenBounds.left) {
                wallBallBounce(Wall.LEFT_SIDE);
            } else if (rightOfBall > mScreenBounds.right) {
                wallBallBounce(Wall.RIGHT_SIDE);
            }
            if (mBallHit) {
                checkIfScored();
            }
            mLastUpdateTime = currentTime;
        }
    }

    // ======================================== Bouncy Methods ==================================================

    private void skewForAngledBounce(){
        double ballCenter = mSoccerBall.getCenterX();
        double faceCenter = mFaceRect.centerX();
        double faceWidth = mFaceRect.width();
        double sweetSpotStart = faceCenter-faceWidth/6;
        double sweetSpotEnd = faceCenter+faceWidth/6;
        double faceStart = mFaceRect.left;
        double faceEnd = mFaceRect.right;

        double adjustmentSpeed =0;
        double distanceFromEdge;
        if(ballCenter<sweetSpotStart){
            distanceFromEdge = ballCenter - faceStart;
            if(distanceFromEdge<=0){
                adjustmentSpeed = MAX_DEFLECTION_VALUE * -1;
            }else{
                adjustmentSpeed = (distanceFromEdge/(sweetSpotStart-faceStart))* MAX_DEFLECTION_VALUE * -1;
            }
        }else if (ballCenter>sweetSpotEnd){
            distanceFromEdge = faceEnd - ballCenter;
            if(distanceFromEdge<=0){
                adjustmentSpeed = MAX_DEFLECTION_VALUE;
            }else{
                adjustmentSpeed = (distanceFromEdge/(faceEnd-sweetSpotEnd)) * MAX_DEFLECTION_VALUE;
            }
        }
        mSoccerBall.setXSpeed(mSoccerBall.getXSpeed()+adjustmentSpeed);
    }

    private void wallBallBounce(Wall side){
        double centerX = mSoccerBall.getCenterX();
        double ballRadius = mSoccerBall.getRadius();
        double overLap;
        switch(side){
            case LEFT_SIDE:
                double leftBallEdge = centerX-ballRadius;
                mSoccerBall.setCenterX(Math.abs(leftBallEdge) + ballRadius);
                break;
            case RIGHT_SIDE:
                int rightWall = mScreenBounds.right;
                overLap = Math.abs(centerX+ballRadius-rightWall);
                mSoccerBall.setCenterX(rightWall - (overLap+ballRadius));
                break;
        }
        mSoccerBall.setXSpeed(getSpeedAfterBounce(mSoccerBall.getXSpeed()));
    }

    private void verticalBallBounce(int bouncedSurface, double bottomOfBall, double opposingForce){
        double overBounce = bottomOfBall - bouncedSurface;
        double adjustedCenter = (bouncedSurface - mSoccerBall.getRadius()) - overBounce;
        mSoccerBall.setCenterY(adjustedCenter);
        double yPostBounceSpeed = getSpeedAfterBounce(mSoccerBall.getYSpeed());
        double momentum = opposingForce/4;
        if(momentum<-5){
            momentum=-5;
        } else if (momentum > 5) {
            momentum = 5;
        }
        double adjustedSpeed = yPostBounceSpeed - momentum;
        if(adjustedSpeed> 0 ){  //A positive speed would mean that the ball would would be heading towards bottom of screen.
            adjustedSpeed =0;
        }
        mSoccerBall.setYSpeed(adjustedSpeed);
    }

    // ======================================== Helper Methods ==================================================

    /**
     * Score only counts if the ball was heading upwards, and only if the user had hit the ball with their head prior to it entering the goal.
     */
    private void checkIfScored(){
        double distanceX = Math.abs(mSoccerBall.getCenterX() - mGoalBounds.centerX());
        double distanceY = Math.abs(mSoccerBall.getCenterY() - mGoalBounds.centerY());
        double distanceFromGoal = Math.sqrt((distanceX*distanceX) + (distanceY* distanceY));
        if(distanceFromGoal<mGoalWidth/2){
            mPlayerScore++;
            mBallDisappearing = true;
            mGoalMoving = true;
            mSoccerBall.setCenter(mGoalBounds.centerX(), mGoalBounds.centerY());
        }
    }

    private double getSpeedAfterBounce(double previousSpeed){
        double postBounceSpeed = Math.abs(previousSpeed) - BALL_RIGIDITY;
        if(postBounceSpeed<0){
            return 0;
        }else{
            return previousSpeed < 0 ? postBounceSpeed : postBounceSpeed * -1;
        }
    }

    private double calculateOpposingForce(long elapsedTime, float previousYPos, float currentYPos){
        float distanceTraveledHorizontally = (previousYPos - currentYPos)/10;
        return distanceTraveledHorizontally * SECOND/elapsedTime;
    }

    private double getXDisplacement(long elapsedTime){
        return (mSoccerBall.getXSpeed() * elapsedTime/REFRESH_DELAY);
    }


    private double getYDisplacement(long elapsedTime){
        return (mSoccerBall.getYSpeed() * elapsedTime/10) + (GRAVITY_ACCELERATION * (elapsedTime/10*elapsedTime/10))/2;
    }

    private void applyGravity(long elapsedTime){
        mSoccerBall.setYSpeed(mSoccerBall.getYSpeed()+ GRAVITY_ACCELERATION*elapsedTime);
    }


//    private void applyAirFriction(){
//        double newSpeed = xSpeed;
//        if(xSpeed>0){ //We are looking to move the value towards 0 with air Friction. So if value is neg, add air friction to speed, otherwise minus it.
//            newSpeed = xSpeed+(AIR_FRICTION_ACCELERATION*elapsedTime/REFRESH_DELAY);
//            if(Math.abs(newSpeed)<ZERO_TOLERANCE) {//Taking a page out of Google's book/sample code, reduce the xSpeed to 0 if it's a trivially close to 0 already.
//                newSpeed =0;
//            }
//            mSoccerBall.setXSpeed(newSpeed);
//        }else if (xSpeed<0){
//            newSpeed = xSpeed-(AIR_FRICTION_ACCELERATION*elapsedTime/REFRESH_DELAY);
//            if(Math.abs(newSpeed)<ZERO_TOLERANCE){
//                newSpeed =0;
//            }
//            mSoccerBall.setXSpeed(newSpeed);
//        }
        /*
            Since horizontal deceleration isn't always affecting the xSpeed and it matters less compared to vertical movement,
             I took a bit of a lazy approach and just found the average speed of before and after the acceleration is applied in order
             to figure out the xDisplacement amount.
         */
//        return ((xSpeed * elapsedTime/REFRESH_DELAY)+(newSpeed * elapsedTime/REFRESH_DELAY))/2;
//    }

    // ======================================== FaceStorage Methods ==================================================

    /**
     * Updates the position of the FaceRect as seen by the soccerEngine. If a face isn't detected we keep the rectangle where the face was at last update.
     *
     * @param faceRect The updated faceRect
     */
    public void updateFacePosition(@Nullable RectF faceRect){
        if(faceRect!=null){
            adjustHitBox(faceRect);
            float overStep = mBoundaryLine - faceRect.top ;
            if(overStep > 0 ){
                faceRect.offset(0, overStep);
            }
            mFaceRect = faceRect;
        }
        updateFaceRectCache();
    }

    private void updateFaceRectCache(){
        for(int i =mPreviousRectPositions.length-1; i>0; i --){
            mPreviousRectPositions[i] = mPreviousRectPositions[i-1];
        }
        mPreviousRectPositions[0] = mFaceRect;
    }

    // ================================================ Game Methods ====================================================

    private Rect generateGoalLocation(){
        Rect goalBounds = new Rect();
        Random rng = new Random();
        int spawnX = rng.nextInt(mScreenBounds.width()- (int)mSoccerRadius*2) + (int)mSoccerRadius;
        goalBounds.set(spawnX - (int)mGoalWidth/2, 10+(int)mGoalWidth/2, spawnX+(int)mGoalWidth/2, 10 + (int)mGoalWidth);
        return goalBounds;
    }

    private void generateGoalInterpolatedPath(PointF oldCenter, PointF newCenter){
        mGoalIndex = 0;
        mGoalInterpolation[0] = oldCenter;
        mGoalInterpolation[mGoalInterpolation.length-1] = newCenter;
        float distanceX = oldCenter.x - newCenter.x;
        float distanceY = oldCenter.y - newCenter.y;
        float incrementsX = distanceX / mGoalInterpolation.length-1;
        float incrementsY = distanceY / mGoalInterpolation.length-1;
        for (int i = 1; i < mGoalInterpolation.length-1; i++){
            PointF lastPosition = mGoalInterpolation[i-1];
            mGoalInterpolation[i] = new PointF(lastPosition.x - incrementsX, lastPosition.y - incrementsY);
        }
    }

    public void spawnNewGoal(){
        //ToDo: Remove this if I Don't plan on using interpolater for whatever reason or decide to do some thingy of sort.
        if(mGoalBounds!=null) {
            Random rng = new Random();
            int spawnX = rng.nextInt(mScreenBounds.width()- (int)mSoccerRadius*2) + (int)mSoccerRadius;
            mGoalBounds.set(spawnX - (int)mGoalWidth/2, 10+(int)mGoalWidth/2, spawnX+(int)mGoalWidth/2, 10 + (int)mGoalWidth);
            return;
        }
        mGoalBounds = new Rect((int)(mScreenBounds.centerX()-mGoalWidth/2), 10 + (int)mGoalWidth/2, (int)(mScreenBounds.centerX()+mGoalWidth/2), 10 + (int)mGoalWidth);
    }

    public void respawnBall(){
        Random rng = new Random();
        mBallHit = false;
        if(mSoccerBall==null){
            mSoccerBall = new Ball(mScreenBounds.centerX(), mScreenBounds.centerY()-mScreenBounds.height()/4, mSoccerRadius, rng.nextInt(4)-4, -5, Color.YELLOW);
            return;
        }
//        int spawnX = rng.nextInt(mScreenBounds.width()- (int)mSoccerRadius) + (int)mSoccerRadius;
        mSoccerBall.recycleBall(mGoalBounds.centerX(), mGoalBounds.centerY(), mSoccerRadius, rng.nextInt(10)-5, -5, Color.YELLOW);
    }


    public void adjustHitBox(RectF hitBox){
        float centerX = hitBox.centerX();
        float centerY = hitBox.centerY();
        float faceRadius = SoccerEngine.FACE_LENGTH/2; //It's not really a radius because it's a rectangle, but it's easier to name it this way.
        hitBox.set(centerX-faceRadius, centerY-faceRadius, centerX+faceRadius, centerY+faceRadius);
    }


}