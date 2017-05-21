package shuvalov.nikita.mirrormirror.filters;


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

import shuvalov.nikita.mirrormirror.MainActivity;
import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;
import shuvalov.nikita.mirrormirror.overlay.FilterOverlay;
import shuvalov.nikita.mirrormirror.overlay.ParticleOverlay;

public class FilterOverlayFragment extends Fragment implements View.OnClickListener {
    private FrameLayout mOverlayContainer;
    private FilterOverlay mFilterOverlay;
    private RecyclerView mFilterRecycler;
    private ImageButton mCameraButton, mFilterSelectionButton;
    private View mCameraHud;
    public boolean mFilterSelectorVisible;

    public FilterOverlayFragment() {
        // Required empty public constructor
    }


    public static FilterOverlayFragment newInstance(){
        return new FilterOverlayFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View filterFragment = inflater.inflate(R.layout.fragment_filter_overlay, container, false);
        mFilterSelectorVisible = false;
        findViews(filterFragment);

        setUpOverlay();
        setUpFilterSelector();
        setOnClickListeners();
        return filterFragment;
    }

    public void setOnClickListeners(){
        mOverlayContainer.setOnClickListener(this);
        mCameraButton.setOnClickListener(this);
        mFilterSelectionButton.setOnClickListener(this);
    }
    public void findViews(View v){
        mCameraButton = (ImageButton) v.findViewById(R.id.camera_button);
        mFilterSelectionButton = (ImageButton) v.findViewById(R.id.filter_button);
        mOverlayContainer = (FrameLayout) v.findViewById(R.id.overlay_container);
        mFilterRecycler = (RecyclerView) v.findViewById(R.id.filters_recycler);
        mCameraHud = v.findViewById(R.id.camera_hud);
    }

    public void setUpOverlay(){
        mFilterOverlay = new FilterOverlay(getContext());
        mFilterOverlay.setZOrderMediaOverlay(true);
        mOverlayContainer.addView(mFilterOverlay);
    }

    public void setUpFilterSelector() {
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mFilterRecycler.setAdapter(new FilterSelectorAdapter(FilterManager.getInstance().getFilters(), mFilterOverlay));
        mFilterRecycler.setLayoutManager(horizontalLayoutManager);
    }
    @Override
    public void onPause() {
        super.onPause();
        mFilterOverlay.stopGraphicThread();
        mOverlayContainer.removeView(mFilterOverlay);
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
        switch (view.getId()) {
            case R.id.camera_button:
                ((MainActivity)getActivity()).captureImage();
                break;
            case R.id.filter_button:
                if (!mFilterSelectorVisible) {
                    replaceBottomView(mCameraHud, mFilterRecycler);
                    mFilterSelectorVisible = true;
                }
                break;
            case R.id.overlay_container:
                if(mFilterSelectorVisible) {
                    replaceBottomView(mFilterRecycler, mCameraHud);
                    mFilterSelectorVisible = false;
                }
                break;
        }
    }
}
