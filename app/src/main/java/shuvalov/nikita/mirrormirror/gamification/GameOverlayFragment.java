package shuvalov.nikita.mirrormirror.gamification;


import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import shuvalov.nikita.mirrormirror.MainActivity;
import shuvalov.nikita.mirrormirror.R;


public class GameOverlayFragment extends Fragment implements View.OnClickListener{
    private FrameLayout mOverlayContainer;
    private GameOverlay mGameOverlay;

    public GameOverlayFragment() {
        // Required empty public constructor
    }

    public static GameOverlayFragment newInstance() {
        return new GameOverlayFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setUpOverlay(Rect screenBounds){
        mGameOverlay = new GameOverlay(getContext(), screenBounds);
        mGameOverlay.setZOrderMediaOverlay(true);
        mOverlayContainer.addView(mGameOverlay);
        mGameOverlay.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View gameFragment = inflater.inflate(R.layout.fragment_game_overlay, container, false);
        mOverlayContainer = (FrameLayout)gameFragment.findViewById(R.id.overlay_container);
        Rect screenBounds = new Rect(container.getLeft(),container.getTop(),container.getWidth(), container.getHeight());
        setUpOverlay(screenBounds);
        return gameFragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        mOverlayContainer.removeView(mGameOverlay);
        mGameOverlay.stopGraphicThread();
    }

    @Override
    public void onClick(View view) {
        mGameOverlay.onScreenClick();
    }
}
