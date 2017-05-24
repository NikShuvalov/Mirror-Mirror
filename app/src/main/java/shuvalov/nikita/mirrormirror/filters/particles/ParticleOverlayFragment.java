package shuvalov.nikita.mirrormirror.filters.particles;


import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;


import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.AppConstants;
import shuvalov.nikita.mirrormirror.MainActivity;
import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.overlay.ParticleOverlay;

public class ParticleOverlayFragment extends Fragment implements View.OnClickListener {
    private ImageButton mParticleButton, mCameraButton, mPhysicsButton;
    private FrameLayout mOverlayContainer;
    private ParticleOverlay mParticleOverlay;
    private RecyclerView mPhysicsSelector, mParticleSelector;
    private OptionsDisplayed mOptionsDisplayed;
    private View mCameraHud;
    private boolean mAnimationLocked;

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
        findViews(particleFragment);
        setUpManager();
        setUpOverlay();
        setOnClickListeners();
        setUpRecyclers();
        return particleFragment;
    }

    private void findViews(View particleFragment){
        mOverlayContainer = (FrameLayout) particleFragment.findViewById(R.id.overlay_container);
        mPhysicsSelector =(RecyclerView)particleFragment.findViewById(R.id.physics_selector_recycler);
        mParticleSelector = (RecyclerView) particleFragment.findViewById(R.id.particle_selector_recycler);
        mCameraButton = (ImageButton) particleFragment.findViewById(R.id.camera_button);
        mParticleButton = (ImageButton)particleFragment.findViewById(R.id.particle_button);
        mPhysicsButton = (ImageButton)particleFragment.findViewById(R.id.physics_button);
        mCameraHud = particleFragment.findViewById(R.id.camera_hud);
    }

    private void setOnClickListeners(){
        mCameraButton.setOnClickListener(this);
        mParticleButton.setOnClickListener(this);
        mOverlayContainer.setOnClickListener(this);
        mPhysicsButton.setOnClickListener(this);
    }

    private void setUpRecyclers(){
        ParticleRecyclerAdapter  particleAdapter = new ParticleRecyclerAdapter(ParticleManager.getInstance().getParticleList());
        PhysicsRecyclerAdapter physicsAdapter = new PhysicsRecyclerAdapter(ParticleManager.getInstance().getSupportedPhysicsTypes());
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
        supportedParticles.add(new Particle("Songbird",AppConstants.getBitmapList(getContext(),R.array.musical_notes_list),false, 0, 10, 2,7));
        supportedParticles.add(new Particle("Flames", AppConstants.getBitmapList(getContext(), R.array.flame_animation_list),true, 0,10,2,30));
        return supportedParticles;
    }
    public void setUpOverlay(){
        mParticleOverlay = new ParticleOverlay(getContext());
        mParticleOverlay.setZOrderMediaOverlay(true);
        mParticleOverlay.setParticleEngine(getFunctionalParticleEngine());
        mOverlayContainer.addView(mParticleOverlay);
    }

    public ParticleEngine getFunctionalParticleEngine(){
        Rect screenBounds = ((MainActivity)getActivity()).getScreenBounds();
        ParticleEngine pEngine = new ParticleEngine(ParticleEngine.PhysicsType.RADIATING, screenBounds, null);
        pEngine.populateParticles(ParticleManager.getInstance().getCurrentParticle());
        return pEngine;
    }


    @Override
    public void onPause() {
        super.onPause();
        mParticleOverlay.stopGraphicThread();
        mOverlayContainer.removeView(mParticleOverlay);
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
                    switch(mOptionsDisplayed){
                        case PARTICLE_SELECTOR:
                            viewToHide = mParticleSelector;
                            break;
                        case PHYSICS_SELECTOR:
                            viewToHide = mPhysicsSelector;
                            break;
                    }
                    mAnimationLocked=true;
                    replaceBottomView(viewToHide, mCameraHud);
                    mOptionsDisplayed = OptionsDisplayed.CAMERA_BUTTON;
                }
        }
    }
}
