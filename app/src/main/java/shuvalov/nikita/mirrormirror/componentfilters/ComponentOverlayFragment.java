package shuvalov.nikita.mirrormirror.componentfilters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import shuvalov.nikita.mirrormirror.R;

public class ComponentOverlayFragment extends Fragment {
    private FrameLayout mOverlayContainer;
    private ComponentOverlay mComponentOverlay;


    //ToDo: Create a recycler that allows user to select different component filter, make sure that recycler updates the choice in the overlay.
    public ComponentOverlayFragment() {
    }


    public static ComponentOverlayFragment newInstance() {
        ComponentOverlayFragment fragment = new ComponentOverlayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_component_overlay, container, false);
        mOverlayContainer = (FrameLayout)view.findViewById(R.id.overlay_container);
        setUpOverlay();
        return view;
    }

    public void setUpOverlay(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mirror_mirror_logo);
        ComponentFilter componentFilter = new RickComponentFilter(getContext(), bitmap);
        mComponentOverlay = new ComponentOverlay(getContext(), componentFilter);
        mComponentOverlay.setZOrderMediaOverlay(true);
        mOverlayContainer.addView(mComponentOverlay);
        mComponentOverlay.setOnClickListener(componentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mComponentOverlay.stopGraphicThread();
        mOverlayContainer.removeView(mComponentOverlay);
    }
}
