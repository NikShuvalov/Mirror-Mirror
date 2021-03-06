package shuvalov.nikita.mirrormirror.particles;


import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.AppConstants;
import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;

import static android.content.ContentValues.TAG;

public class ParticleOverlayFragment extends Fragment implements View.OnClickListener, ParticleRecyclerAdapter.EngineIgnitionListener, PhysicsRecyclerAdapter.PhysicsSelectorListener {
    private ImageButton mCameraButton, mParticleButton;
    private FrameLayout mOverlayContainer, mPhysicsButton;
    private ParticleOverlay mParticleOverlay;
    private RecyclerView mPhysicsSelector, mParticleSelector;
    private OptionsDisplayed mOptionsDisplayed;
    private View mCameraHud;
    private boolean mAnimationLocked, mOverlayLoaded;
    private ParticleEngine mParticleEngine;
    private ImageView mPhysicsDisplay;


    private enum OptionsDisplayed{
        CAMERA_BUTTON, PARTICLE_SELECTOR, PHYSICS_SELECTOR
    }
    public ParticleOverlayFragment() {
        // Required empty public constructor
    }

    public static ParticleOverlayFragment newInstance() {
        return new ParticleOverlayFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View particleFragment = inflater.inflate(R.layout.fragment_particle_overlay, container, false);
        mOptionsDisplayed = OptionsDisplayed.CAMERA_BUTTON;
        mAnimationLocked = false;
        mOverlayLoaded = false;
        findViews(particleFragment);
        setUpManager();
        setOnClickListeners();
        setUpRecyclers();
        updatePhysicsTypeImage(ParticleManager.getInstance().getPhysicsType());
        return particleFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mOverlayLoaded){
            setUpOverlay();
        }
    }

    private void findViews(View particleFragment){
        mOverlayContainer = (FrameLayout) particleFragment.findViewById(R.id.overlay_container);
        mPhysicsSelector =(RecyclerView)particleFragment.findViewById(R.id.physics_selector_recycler);
        mParticleSelector = (RecyclerView) particleFragment.findViewById(R.id.particle_selector_recycler);
        mCameraButton = (ImageButton) particleFragment.findViewById(R.id.camera_button);
        mParticleButton = (ImageButton)particleFragment.findViewById(R.id.particle_button);
        mPhysicsButton = (FrameLayout)particleFragment.findViewById(R.id.physics_button);
        mCameraHud = particleFragment.findViewById(R.id.camera_hud);
        mPhysicsDisplay = (ImageView) particleFragment.findViewById(R.id.current_physics_display);
    }

    private void setOnClickListeners(){
        mCameraButton.setOnClickListener(this);
        mParticleButton.setOnClickListener(this);
        mOverlayContainer.setOnClickListener(this);
        mPhysicsButton.setOnClickListener(this);
    }

    private void setUpRecyclers(){
        ParticleRecyclerAdapter  particleAdapter = new ParticleRecyclerAdapter(this, ParticleManager.getInstance().getParticleList());
        PhysicsRecyclerAdapter physicsAdapter = new PhysicsRecyclerAdapter(this, ParticleManager.getInstance().getSupportedPhysicsTypes());
        LinearLayoutManager particleLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager physicsLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mParticleSelector.setAdapter(particleAdapter);
        mPhysicsSelector.setAdapter(physicsAdapter);
        mParticleSelector.setLayoutManager(particleLinearLayoutManager);
        mPhysicsSelector.setLayoutManager(physicsLinearLayoutManager);
    }

    private void setUpManager(){
        ParticleManager particleManager = ParticleManager.getInstance();
        particleManager.setParticleList(getSupportedParticles());
    }

    private ArrayList<Particle> getSupportedParticles(){
        ArrayList<Particle> supportedParticles = new ArrayList<>();
        supportedParticles.add(null);
        supportedParticles.add(new Particle("Songbird",AppConstants.getBitmapList(getContext(),R.array.musical_notes_list),false, 0, 10, 2,7));
        supportedParticles.add(new Particle("Flames", AppConstants.getBitmapList(getContext(), R.array.flame_animation_list),true, 0,10,2,30));
        return supportedParticles;
    }

    public void setUpOverlay(){
        Log.d(TAG, "setUpOverlay: started");
        if(mParticleEngine== null){
            mParticleEngine = getFunctionalParticleEngine();
        }
        mParticleOverlay = new ParticleOverlay(getContext());
        mParticleOverlay.setZOrderMediaOverlay(true);
        mParticleOverlay.setParticleEngine(mParticleEngine);
        mOverlayContainer.addView(mParticleOverlay);
        Log.d(TAG, "setUpOverlay: ended");
    }

    public ParticleEngine getFunctionalParticleEngine(){
        Log.d(TAG, "getFunctionalParticleEngine: started");
        ParticleManager particleManager = ParticleManager.getInstance();
        Rect screenBounds = new Rect();
        mOverlayContainer.getHitRect(screenBounds);
        ParticleEngine pEngine = new ParticleEngine(particleManager.getPhysicsType(), screenBounds, null);
        new EnginePopulatorTask().execute(pEngine);
        Log.d(TAG, "getFunctionalParticleEngine: ended");
        return pEngine;
    }


    @Override
    public void onPause() {
        super.onPause();
        ParticleManager.getInstance().clearParticleSelection();
        if(mParticleOverlay!=null){
            mParticleOverlay.stopGraphicThread();
            mOverlayContainer.removeView(mParticleOverlay);
        }
    }

    public void replaceBottomView(final View viewToHide, final View viewToShow) {
        Animation hideAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_panel_hide);
        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewToHide.clearAnimation();
                showView(viewToShow);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        viewToHide.setAnimation(hideAnim);
        viewToHide.setVisibility(View.INVISIBLE);
    }

    public void showView(final View v) {
        Animation showAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_panel_show);
        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.clearAnimation();
                mAnimationLocked=false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.setAnimation(showAnim);
        v.setVisibility(View.VISIBLE);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.camera_button:
                Toast.makeText(getContext(), "Picture Time", Toast.LENGTH_SHORT).show();
                break;
            case R.id.particle_button:
                if(mOptionsDisplayed!= OptionsDisplayed.PARTICLE_SELECTOR && !mAnimationLocked){
                    mAnimationLocked = true;
                    replaceBottomView(mCameraHud, mParticleSelector);
                    mOptionsDisplayed = OptionsDisplayed.PARTICLE_SELECTOR;
                }
                break;
            case R.id.physics_button:
                if(mOptionsDisplayed!=OptionsDisplayed.PHYSICS_SELECTOR && !mAnimationLocked){
                    mAnimationLocked = true;
                    replaceBottomView(mCameraHud, mPhysicsSelector);
                    mOptionsDisplayed = OptionsDisplayed.PHYSICS_SELECTOR;
                }
            default:
                if(mOptionsDisplayed!= OptionsDisplayed.CAMERA_BUTTON && !mAnimationLocked){
                    View viewToHide = null;
                    ParticleManager particleManager = ParticleManager.getInstance();
                    switch(mOptionsDisplayed){
                        case PARTICLE_SELECTOR:
                            viewToHide = mParticleSelector;
                            break;
                        case PHYSICS_SELECTOR:
                            viewToHide = mPhysicsSelector;
                            updatePhysicsTypeImage(particleManager.getPhysicsType());
                            break;
                    }
                    mAnimationLocked=true;
                    replaceBottomView(viewToHide, mCameraHud);
                    mOptionsDisplayed = OptionsDisplayed.CAMERA_BUTTON;
                }
        }
    }

    private void updatePhysicsTypeImage(ParticleEngine.PhysicsType p){
        switch(p){
            case SIMPLE:
                mPhysicsDisplay.setImageResource(R.drawable.icon_simple);
                break;
            case OSCILLATING:
                mPhysicsDisplay.setImageResource(R.drawable.icon_wavy);
                break;
            case RADIATING:
                mPhysicsDisplay.setImageResource(R.drawable.icon_radial);
                break;
        }
    }

    @Override
    public void onEngineIgnition() {
        FaceTracker faceTracker = FaceTracker.getInstance();
        if(!mOverlayLoaded){
            mOverlayLoaded = true;
            setUpOverlay();
        }
        if(!faceTracker.isActive()){
            FaceTracker.getInstance().start();
        }
        new EnginePopulatorTask().execute(mParticleEngine);
    }

    @Override
    public void onEngineShutDown() {
        mParticleEngine.populateParticles(ParticleManager.getInstance().getCurrentParticle());
        FaceTracker.getInstance().pause();
    }

    @Override
    public void onPhysicsSelected() {
        if(mParticleEngine==null) {
            mParticleEngine = getFunctionalParticleEngine();
        }
        mParticleEngine.changePhysicsType(ParticleManager.getInstance().getPhysicsType());
    }

    private class EnginePopulatorTask extends AsyncTask<ParticleEngine, Void, Void>{

        @Override
        protected Void doInBackground(ParticleEngine... particleEngines) {
            particleEngines[0].populateParticles(ParticleManager.getInstance().getCurrentParticle());
            return null;
        }
    }
}
