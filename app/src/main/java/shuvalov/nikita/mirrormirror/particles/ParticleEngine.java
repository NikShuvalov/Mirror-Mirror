package shuvalov.nikita.mirrormirror.particles;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;

/**
 * Created by NikitaShuvalov on 5/7/17.
 */

public class ParticleEngine {
    private ArrayList<Particle> mParticles;
    private PhysicsType mPhysicsType;
    private Rect mScreenBounds;
    private long mLastUpdate = SystemClock.elapsedRealtime();
    private Random mRng;
    private PointF mCurrentPosition, mPreviousPosition;
    public static final int FACE_CACHE_SIZE = 10;
    private double mFaceXShift, mFaceYShift, mCumulativeXShift, mCumulativeYShift;
    private static final double MAX_REPULSION_FORCE = 30;
    private boolean mActive;


    /**
     * SnowGlobe just shakes the particles when the face moves.
     * Oscillating uses sin/cosine to move the particles in a snaking pattern.
     * Simple is just straight lines.
     */

    public enum PhysicsType {
        SNOWGLOBE, OSCILLATING, SIMPLE, RADIATING
    }

    // to give an effect of the particles traveling behind the face. Then have them come back into existance once they leave the bounds of the face?
    public ParticleEngine(PhysicsType physicsType, Rect screenBounds, @Nullable RectF faceRect) {
        mParticles = new ArrayList<>();
        mPhysicsType = physicsType;
        mScreenBounds = screenBounds;
        mRng = new Random();
        if (faceRect != null) {
            mCurrentPosition = new PointF(faceRect.centerX(), faceRect.centerY());
        } else {
            mCurrentPosition = new PointF(mScreenBounds.exactCenterX(), mScreenBounds.exactCenterY());
        }
        mPreviousPosition = new PointF(mCurrentPosition.x, mCurrentPosition.y);
        mFaceYShift = 0;
        mFaceXShift = 0;
//        mRecentPositions = new PointF[FACE_CACHE_SIZE];
//        for(int i = 0; i<FACE_CACHE_SIZE; i ++){
//            mRecentPositions[i] = mCurrentPosition;
//        }
    }

    public void populateParticles(Particle sampleParticle) {
        mParticles.clear();
        if (sampleParticle==null) {
            mActive = false;
            return;
        }
        mActive = true;
        while (mParticles.size() <= sampleParticle.getMaxParticles()) {
            Particle p = sampleParticle.makeCarbonCopy();
            resetParticle(p);
            if (!addParticle(p)) { //Just in case.
                return;
            }
        }
    }

    //Returns false if list is or becomes maxed, returns true if room for more.
    public boolean addParticle(Particle p) {
        if (mParticles.size() >= p.getMaxParticles()) {
            return false;
        }
        mParticles.add(p);
        return true;
    }

    public ArrayList<Particle> getParticles() {
        return mParticles;
    }

    public void moveParticles() {
        long val = SystemClock.elapsedRealtime() - mLastUpdate;
        if (val >= Particle.REFRESH_RATE && mActive) {
            switch (mPhysicsType) {
                case SNOWGLOBE:
                    processSnowglobeMovement();
                    break;
                case SIMPLE:
                    processSimpleMovement();
                    break;
                case OSCILLATING:
                    processOscillatingMovement();
                    break;
                case RADIATING:
                    processRadiatingMovement();
                    break;
            }
        }
    }

    private void processSimpleMovement() {
        long currentTime = SystemClock.elapsedRealtime();
        long elapsedTime = currentTime - mLastUpdate;
        for (int i = 0; i < mParticles.size(); i++) {
            Particle p = mParticles.get(i);
            double scaledYSpeed = p.getYVel() * p.getScale();//The speed at which the particle will move up based on it's "distance" from the lens. Speed is per 30ms
            double yDisplacement = scaledYSpeed * (elapsedTime / Particle.REFRESH_RATE);

            double scaledXSpeed = p.getXVel() * p.getScale();
            double xDisplacement = scaledXSpeed * (elapsedTime / Particle.REFRESH_RATE);

            p.translatePosition(xDisplacement, yDisplacement);
            if (p.isOutOfBounds(mScreenBounds)) {
                resetParticle(p);
            }
        }
        mLastUpdate = currentTime;
    }



    private void processSnowglobeMovement() {
        long currentTime = SystemClock.elapsedRealtime();
        long elapsedTime = currentTime - mLastUpdate;
        for (int i = 0; i < mParticles.size(); i++) {
            //Do the thing?
        }
        mLastUpdate = currentTime;
    }

    //===================================== Radiating movement ========================================================
    //This movement moves outwards from a point of origin, for now just the face or mouth.
    // The user's face will repel the particles so that the closer the particle is the faster to moves away from the user.
    // Slowing as it gets away. Velocity is logarithmic to distance from face center.
    private void processRadiatingMovement(){
        long currentTime = SystemClock.elapsedRealtime();
        long elapsedTime = currentTime - mLastUpdate;
        Particle p;
        for(int i = 0; i<mParticles.size() ; i++){
            p = mParticles.get(i);
            RectF face = FaceTracker.getInstance().getFaceRect();
            double yDisplacement = getYDisplacementRadiating(elapsedTime, p, face);
            double xDisplacement = getXDisplacementRadiating(elapsedTime, p, face);
            p.translatePosition(xDisplacement, yDisplacement);
            if (p.isOutOfBounds(mScreenBounds)){
                resetParticle(p);
            }
        }
        mLastUpdate = currentTime;
    }

    //Formula: displacement = force - (force * (distance/bounds));
    private double getYDisplacementRadiating(long elapsedTime, Particle p, RectF face){
        double yForceSource;
        if(face!=null){
            yForceSource = face.centerY() + (face.height()/4);
        }else{
            yForceSource = mScreenBounds.centerY();
        }
        double particleCenter = p.getYLoc();
        double distance = particleCenter - yForceSource;
        double yDisplacement = (MAX_REPULSION_FORCE - (MAX_REPULSION_FORCE* (distance/mScreenBounds.height())) * elapsedTime/Particle.REFRESH_RATE);
        if(distance<0){
            yDisplacement = yDisplacement * -1;
        }
        return yDisplacement;
    }

    private double getXDisplacementRadiating(long elapsedTime, Particle p, RectF face){
        double xForceSource;
        if(face!=null){
            xForceSource = face.centerX();
        }else{
            xForceSource = mScreenBounds.centerX();
        }
        double particleCenter = p.getXLoc();
        double distance = particleCenter - xForceSource;
        double xDisplacement = (MAX_REPULSION_FORCE - (MAX_REPULSION_FORCE* (distance/mScreenBounds.width())) * elapsedTime/Particle.REFRESH_RATE);
        if(distance<0){
            xDisplacement= xDisplacement * -1;
        }
        return xDisplacement;
    }

    //======================================== Oscillating movement ======================================================
    private void processOscillatingMovement() {
        long currentTime = SystemClock.elapsedRealtime();
        long elapsedTime = currentTime - mLastUpdate;
        double xAxis;
        Particle p;
        for (int i = 0; i < mParticles.size(); i++) {
            p = mParticles.get(i);
            double yDisplacement = getYDisplacementOscillating(elapsedTime, p);

//            xAxis = p.getStartX() - (mCumulativeXShift * p.getScale()/3);
            xAxis = p.getStartX();

            p.translatePosition(0, yDisplacement);
            double newXPosition = (100 * Math.sin(p.getYLoc() / 100)) + xAxis;

            p.setXLoc(newXPosition);

            if (p.getYLoc() > mScreenBounds.height()) {
                resetParticle(p);
            }
        }
        mLastUpdate = currentTime;

    }

    private double getYDisplacementOscillating(long elapsedTime, Particle p){
        double scaledYSpeed = p.getYVel() * p.getScale();//The speed at which the particle will move up based on it's "distance" from the lens. Speed is per 30ms
        return scaledYSpeed * (elapsedTime / Particle.REFRESH_RATE);
    }

    //This currently only makes the particle fall/rise infinitely at the same x location, only changing in size.
    private void resetParticle(Particle p) {
        switch(mPhysicsType){
            case RADIATING:
                RectF face = FaceTracker.getInstance().getFaceRect();
                Random rng = new Random();
                if(face!=null){
                    double xCenter = face.centerX();
                    p.setXLoc(xCenter + (rng.nextInt(100) - 50));
                    double range = face.bottom - face.centerY();
                    p.setYLoc(face.centerY() + rng.nextInt((int)range));
                }else{//If no face available just spawn in middle of screen.
                    double xCenter = mScreenBounds.centerX();
                    p.setXLoc(xCenter + (rng.nextInt(100) - 50));
                    double yCenter = mScreenBounds.centerY();
                    p.setYLoc(yCenter + (rng.nextInt(100)-50));
                }
                break;
            case SIMPLE:
            case OSCILLATING:
                p.setStartX(mRng.nextInt(mScreenBounds.height())); //Using portrait in a landscape world.
                p.resetXToStart();
                double scale = (mRng.nextDouble() + .25) * 4;
                p.setScale(scale);
                p.setYLoc(p.getStartY());
                break;
        }
        p.randomizeBitmap();

    }


    public void updateFacePosition(float cx, float cy){
        mPreviousPosition.set(mCurrentPosition.x, mCurrentPosition.y);
        if(cx!= Float.MIN_VALUE){
            mCurrentPosition.set(cx, cy);
        }
//        calculateForFaceShift();
    }

    private void calculateForFaceShift(){
        //XShift needs to affect startX of particle for oscillating.
        mFaceXShift = mPreviousPosition.x - mCurrentPosition.x;
        mCumulativeXShift += mFaceXShift;
        if(mCumulativeXShift > mScreenBounds.width()/2){
            mCumulativeXShift = mScreenBounds.width()/2;
        }else if ( mCumulativeXShift< mScreenBounds.width()/-2){
            mCumulativeXShift = mScreenBounds.width()/-2;
        }

        //YShift need to shift vertically in a way that doesn't cause the particle to move upward in a sine-wave as well.
        mFaceYShift = mPreviousPosition.y - mCurrentPosition.y;
        mCumulativeYShift += mFaceYShift;
    }

    public void changePhysicsType(PhysicsType physicsType){
        mPhysicsType = physicsType;
    }
}
