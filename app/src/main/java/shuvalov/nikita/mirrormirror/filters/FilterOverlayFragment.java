package shuvalov.nikita.mirrormirror.filters;


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
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.overlay.FilterOverlay;

public class FilterOverlayFragment extends Fragment implements View.OnClickListener, FilterSelectorAdapter.FilterSelectorListener {
    private FrameLayout mOverlayContainer;
    private FilterOverlay mFilterOverlay;
    private RecyclerView mFilterRecycler;
    private ImageButton mCameraButton, mFilterSelectionButton, mMoreButton, mDupleFilterSelButton;
    private View mCameraHud, mAdditionalOptsHud;
    public boolean mSelectorVisible;
    private ImageButton mGameOption, mBrowseOption;

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
        mSelectorVisible = false;
        findViews(filterFragment);
        setUpFilterSelector();
        setOnClickListeners();
        return filterFragment;
    }

    public void setOnClickListeners(){
        mOverlayContainer.setOnClickListener(this);
        mCameraButton.setOnClickListener(this);
        mFilterSelectionButton.setOnClickListener(this);
        mBrowseOption.setOnClickListener(this);
        mGameOption.setOnClickListener(this);
        mMoreButton.setOnClickListener(this);
        mDupleFilterSelButton.setOnClickListener(this);
    }

    public void findViews(View v){
        mCameraButton = (ImageButton) v.findViewById(R.id.camera_button);
        mFilterSelectionButton = (ImageButton) v.findViewById(R.id.filter_button);
        mGameOption = (ImageButton) v.findViewById(R.id.game_option);
        mBrowseOption = (ImageButton)v.findViewById(R.id.browse_option);
        mOverlayContainer = (FrameLayout) v.findViewById(R.id.overlay_container);
        mFilterRecycler = (RecyclerView) v.findViewById(R.id.filters_recycler);
        mMoreButton = (ImageButton) v.findViewById(R.id.more_options);
        mDupleFilterSelButton = (ImageButton)v.findViewById(R.id.duple_filter_button);
        mCameraHud = v.findViewById(R.id.camera_hud);
        mAdditionalOptsHud = v.findViewById(R.id.additional_options);
    }

    public void setUpFilterSelector() {
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mFilterRecycler.setAdapter(new FilterSelectorAdapter(this,FilterManager.getInstance()));
        mFilterRecycler.setLayoutManager(horizontalLayoutManager);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mFilterOverlay!=null) {
            mOverlayContainer.removeView(mFilterOverlay);
            mFilterOverlay.stopGraphicThread();
        }
        FilterManager.getInstance().clearSelectionIndex();
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
                if(viewToShow!=null) {
                    showView(viewToShow);
                }else{
                    ((MainActivity)getActivity()).notifyOverlayChanged();
                }
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
                if (!mSelectorVisible) {
                    replaceBottomView(mCameraHud, mFilterRecycler);
                    mSelectorVisible = true;
                }
                break;
            case R.id.overlay_container:
                if(mSelectorVisible) {
                    replaceBottomView(mFilterRecycler.getVisibility() == View.VISIBLE ?
                            mFilterRecycler : mAdditionalOptsHud,
                            mCameraHud);
                    mSelectorVisible = false;
                }
                break;
            case R.id.more_options:
                replaceBottomView(mCameraHud, mAdditionalOptsHud);
                mSelectorVisible = true;
                break;
            case R.id.browse_option:
                ((MainActivity)getActivity()).changeOverlay(MainActivity.GraphicType.BROWSE);
                replaceBottomView(mAdditionalOptsHud, null);
                break;
            case R.id.game_option:
                ((MainActivity)getActivity()).changeOverlay(MainActivity.GraphicType.GAME);
                replaceBottomView(mAdditionalOptsHud, null);
                break;
            case R.id.duple_filter_button:
                replaceBottomView(mAdditionalOptsHud, mFilterRecycler);
                //This view is only visible if SelectorVisible is true, so no need to toggle it.
                break;
        }
    }

    @Override
    public void onFilterSelected(int i) {
        FilterManager.getInstance().setSelectedIndex(i);
        if(mFilterOverlay==null){
            setUpOverlay();
        }
        FaceTracker.getInstance().setActive(FilterManager.getInstance().getSelectedIndex()>0);
        mFilterOverlay.notifyFilterChange();
    }

    public void setUpOverlay(){
        mFilterOverlay = new FilterOverlay(getContext());
        mFilterOverlay.setZOrderMediaOverlay(true);
        mOverlayContainer.addView(mFilterOverlay);
    }

}
