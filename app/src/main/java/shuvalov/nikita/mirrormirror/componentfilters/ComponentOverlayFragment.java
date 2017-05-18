package shuvalov.nikita.mirrormirror.componentfilters;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.filters.StaticFilter;
import shuvalov.nikita.mirrormirror.overlay.FilterOverlay;

public class ComponentOverlayFragment extends Fragment {
    private FrameLayout mOverlayContainer;
    private ComponentOverlay mComponentOverlay;


    public ComponentOverlayFragment() {
        // Required empty public constructor
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
        ArrayList<Filter> f = new ArrayList<>();
        ComponentFilter c = new ComponentFilter("Rick Sanchez",f);//Just filling this in to appease my own code, the overlay is hard-coded.
        mComponentOverlay = new ComponentOverlay(getContext(), c);
        mComponentOverlay.setZOrderMediaOverlay(true);
        mOverlayContainer.addView(mComponentOverlay);
    }

    @Override
    public void onPause() {
        super.onPause();
        mComponentOverlay.stopGraphicThread();
        mOverlayContainer.removeView(mComponentOverlay);
    }
}
