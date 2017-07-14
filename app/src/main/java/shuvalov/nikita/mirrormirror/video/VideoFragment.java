package shuvalov.nikita.mirrormirror.video;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import shuvalov.nikita.mirrormirror.R;

public class VideoFragment extends Fragment implements View.OnClickListener {
    private FrameLayout mOverlayContainer;

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        mOverlayContainer = (FrameLayout)view.findViewById(R.id.overlay_container);
        mOverlayContainer.setOnClickListener(this);
        VideoOverlay videoOverlay = new VideoOverlay(getContext());
        videoOverlay.setZOrderMediaOverlay(true);
        mOverlayContainer.addView(videoOverlay);
        return view;
    }

    //Debugging purposes
    @Override
    public void onClick(View view) {
        VideoFilterManager.getInstance().nextFilter();
    }
}
