package shuvalov.nikita.mirrormirror.filters.particles;


import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import java.util.Random;

import shuvalov.nikita.mirrormirror.MainActivity;
import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.overlay.ParticleOverlay;

public class ParticleOverlayFragment extends Fragment {
    private FrameLayout mOverlayContainer;
    private ParticleOverlay mParticleOverlay;

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
        mOverlayContainer = (FrameLayout) particleFragment.findViewById(R.id.overlay_container);
        setUpOverlay();
        return particleFragment;
    }

    public void setUpOverlay(){
        mParticleOverlay = new ParticleOverlay(getContext());
        mParticleOverlay.setZOrderMediaOverlay(true);
        mParticleOverlay.setParticleEngine(getFunctionalParticleEngine());
        mOverlayContainer.addView(mParticleOverlay);
    }

    public ParticleEngine getFunctionalParticleEngine(){
        Random rng = new Random();
        Rect screenBounds = ((MainActivity)getActivity()).getScreenBounds();
        ParticleEngine pEngine = new ParticleEngine(ParticleEngine.PhysicsType.OSCILLATING, screenBounds,null);
        pEngine.populateParticles(new Particle(R.drawable.flamekey0, rng.nextInt(screenBounds.width()),1,0,10,2));
        return pEngine;
    }


    @Override
    public void onPause() {
        super.onPause();
        mParticleOverlay.stopGraphicThread();
        mOverlayContainer.removeView(mParticleOverlay);
    }
}
