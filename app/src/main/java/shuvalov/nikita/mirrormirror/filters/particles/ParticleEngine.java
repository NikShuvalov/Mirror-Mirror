package shuvalov.nikita.mirrormirror.filters.particles;

import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by NikitaShuvalov on 5/7/17.
 */

public class ParticleEngine {
    private ArrayList<Particle> mParticles;
    private PhysicsType mPhysicsType;
    private int MAX_PARTICLES = 25;
    private Rect mScreenBounds;
    private RectF mFaceBounds;
    private long mLastUpdate = SystemClock.elapsedRealtime();

    //ToDO: Make an enum for vertical direction of particles?

    /**
     * SnowGlobe just shakes the particles when the face moves.
     * Oscillating uses sin/cosine to move the particles in a snaking pattern.
     * Simple is just straight lines.
     */
    public enum PhysicsType{
        SNOWGLOBE, OSCILLATING, SIMPLE
    }

    //ToDo: Have particles that are below a certain size(aka distance) not appear when within the bounds of the face
    // to give an effect of the particles traveling behind the face. Then have them come back into existance once they leave the bounds of the face?
    public ParticleEngine(PhysicsType physicsType, Rect screenBounds,@Nullable RectF faceBounds) {
        mParticles = new ArrayList<>();
        mPhysicsType = physicsType;
        mScreenBounds = screenBounds;
        mFaceBounds = faceBounds;
    }

    public void setFaceBounds(RectF faceBounds){
        mFaceBounds = faceBounds;
    }

    public void populateParticles(Particle sampleParticle){
        while(mParticles.size()<=MAX_PARTICLES){
            Particle p = sampleParticle.makeCarbonCopy();
            resetParticle(p);
            if(addParticle(p)) { //Just in case.
                break;
            }
        }
    }

    //Returns false if list is or becomes maxed, returns true if room for more.
    public boolean addParticle(Particle p){
        if(mParticles.size()>= MAX_PARTICLES){
            return false;
        }
        mParticles.add(p);
        return mParticles.size() != MAX_PARTICLES;
    }

    public ArrayList<Particle> getParticles() {
        return mParticles;
    }

    public void moveParticles(){
        switch(mPhysicsType){
            case SNOWGLOBE:
                processSnowglobeMovement();
                break;
            case SIMPLE:
                processSimpleMovement();
                break;
            case OSCILLATING:
                processOscillatingMovement();
                break;
        }
    }

    //ToDo: Make asynchronous?
    private void processSimpleMovement(){
        long currentTime = SystemClock.elapsedRealtime();
        long elapsedTime = currentTime-mLastUpdate;
        for(int i =0; i< mParticles.size(); i++){
            Particle p = mParticles.get(i);
            float scaledYSpeed = p.getYVel()*p.getScale();//The speed at which the particle will move up based on it's "distance" from the lens. Speed is per 30ms
            float yDisplacement = scaledYSpeed * (Particle.REFRESH_RATE/elapsedTime);

            float scaledXSpeed = p.getXVel() * p.getScale();
            float xDisplacement = scaledXSpeed * (Particle.REFRESH_RATE/elapsedTime);

            //ToDo: Add faceShifts to displacement?
            p.translatePosition(xDisplacement, yDisplacement);

            if(p.isOutOfBounds(mScreenBounds)){
                resetParticle(p);
            }
        }
        mLastUpdate = currentTime;
    }

    private void processSnowglobeMovement(){

    }

    private void processOscillatingMovement(){

    }

    //This currently only makes the particle fall/rise infinitely at the same x location, only changing in size.
    private void resetParticle(Particle p){
        Random rng = new Random();
        p.setXLoc(rng.nextInt(mScreenBounds.right));
        float scale = rng.nextFloat()*2;
        if(scale<0.5){
            scale= 0.5f;
        }
        p.setScale(scale);
        p.setYLoc(p.getStartY());
    }
}
