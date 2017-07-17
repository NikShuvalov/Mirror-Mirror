package shuvalov.nikita.mirrormirror.video;


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

import shuvalov.nikita.mirrormirror.MainActivity;
import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.filters.FilterSelectorAdapter;

public class VideoFragment extends Fragment implements View.OnClickListener, FilterSelectorAdapter.FilterSelectorListener {
    private FrameLayout mOverlayContainer;
    private ImageButton mCameraButton, mFilterSelectionButton;
    private RecyclerView mFilterRecycler;
    private View mCameraHud;
    private boolean mFilterSelectorVisible;
    private VideoOverlay mVideoOverlay;

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
        mFilterSelectorVisible = false;
        findViews(view);
        setUpFilterSelector();
        setOnClickListeners();
        mVideoOverlay = new VideoOverlay(getContext());
        mVideoOverlay.setZOrderMediaOverlay(true);
        mOverlayContainer.addView(mVideoOverlay);
        return view;
    }


    public void setUpFilterSelector() {
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mFilterRecycler.setAdapter(new FilterSelectorAdapter(this,VideoFilterManager.getInstance()));
        mFilterRecycler.setLayoutManager(horizontalLayoutManager);
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
    public void onFilterSelected(int i ) {
        VideoFilterManager.getInstance().setSelectedIndex(i);
        mVideoOverlay.notifyFilterChange();
    }


    @Override
    public void onPause() {
        super.onPause();
        if(mVideoOverlay!=null){
            mVideoOverlay.stopGraphicThread();
            mOverlayContainer.removeView(mVideoOverlay);
        }
        VideoFilterManager.getInstance().clearSelectionIndex();
    }
}
