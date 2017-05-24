package shuvalov.nikita.mirrormirror.filters.particles;

import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.AppConstants;
import shuvalov.nikita.mirrormirror.R;

/**
 * Created by NikitaShuvalov on 5/24/17.
 */

public class ParticleManager {
    private ArrayList<Particle> mParticleList;
    private int mCurrentParticleIndex;
    private ParticleEngine.PhysicsType mPhysicsType;
    private ParticleEngine.PhysicsType[] mSupportedPhysicsTypes;
    private static ParticleManager sParticleManager;

    private ParticleManager(){
        mParticleList = new ArrayList<>();
        mCurrentParticleIndex = 0;
        mPhysicsType = ParticleEngine.PhysicsType.SIMPLE;
        setSupportedPhysicTypes();
    }

    private void setSupportedPhysicTypes(){
        mSupportedPhysicsTypes = new ParticleEngine.PhysicsType[]{ParticleEngine.PhysicsType.SIMPLE, ParticleEngine.PhysicsType.OSCILLATING,ParticleEngine.PhysicsType.RADIATING };
    }

    public static ParticleManager getInstance() {
        if(sParticleManager==null){
            sParticleManager = new ParticleManager();
        }
        return sParticleManager;
    }

    public int getParticleListSize(){
        return mParticleList.size();
    }

    public ArrayList<Particle> getParticleList(){
        return mParticleList;
    }

    public Particle getCurrentParticle(){
        return mParticleList.get(mCurrentParticleIndex);
    }

    public ParticleEngine.PhysicsType getPhysicsType() {
        return mPhysicsType;
    }

    public ParticleEngine.PhysicsType[] getSupportedPhysicsTypes() {
        return mSupportedPhysicsTypes;
    }

    public void setPhysicsType(ParticleEngine.PhysicsType physicsType){
        mPhysicsType = physicsType;
    }
    public void setPhysicsType(int index){
        mPhysicsType = mSupportedPhysicsTypes[index];
    }

    public void setParticleList(ArrayList<Particle> particleList) {
        mParticleList = particleList;
    }

    public void addParticleToList(Particle p){
        mParticleList.add(p);
    }

    public int getCurrentParticleIndex() {
        return mCurrentParticleIndex;
    }
}
