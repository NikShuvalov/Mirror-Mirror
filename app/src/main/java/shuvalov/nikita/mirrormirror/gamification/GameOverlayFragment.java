package shuvalov.nikita.mirrormirror.gamification;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import shuvalov.nikita.mirrormirror.MainActivity;
import shuvalov.nikita.mirrormirror.R;


public class GameOverlayFragment extends Fragment {
    private FrameLayout mOverlayContainer;
    private GameOverlay mGameOverlay;

    public GameOverlayFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static GameOverlayFragment newInstance() {
        return new GameOverlayFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setUpOverlay(){
        mGameOverlay = new GameOverlay(getContext());
        Rect screenBounds = ((MainActivity)getActivity()).getScreenBounds();
        SoccerEngine soccerEngine = new SoccerEngine(screenBounds);
        mGameOverlay.setSoccerEngine(soccerEngine);
        mGameOverlay.setZOrderMediaOverlay(true);
        mOverlayContainer.addView(mGameOverlay);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View gameFragment = inflater.inflate(R.layout.fragment_game_overlay, container, false);
        mOverlayContainer = (FrameLayout)gameFragment.findViewById(R.id.overlay_container);
        setUpOverlay();
        return gameFragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        mOverlayContainer.removeView(mGameOverlay);
        mGameOverlay.stopGraphicThread();
    }
}
