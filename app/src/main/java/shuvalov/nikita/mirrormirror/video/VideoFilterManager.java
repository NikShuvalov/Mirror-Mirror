package shuvalov.nikita.mirrormirror.video;

import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.filters.StaticFilter;

/**
 * Created by NikitaShuvalov on 7/14/17.
 */

public class VideoFilterManager {
    private int mSelectionIndex;
    private ArrayList<Filter> mFilters;

    private VideoFilterManager() {
        mSelectionIndex = 0;
        populateFilterList();
    }

    private static VideoFilterManager sVideoFilterManager;

    public static VideoFilterManager getInstance() {
        if(sVideoFilterManager == null){
            sVideoFilterManager = new VideoFilterManager();
        }
        return sVideoFilterManager;
    }

    public Filter getSelectedFilter(){
        return mFilters.get(mSelectionIndex);
    }

    public ArrayList<Filter> getFilters() {
        return mFilters;
    }

    public void nextFilter(){
        mSelectionIndex++;
        if(mSelectionIndex>= mFilters.size()){
            mSelectionIndex=0;
        }
    }

    public int getSelectionIndex(){
        return mSelectionIndex;
    }

    public void setSelectionIndex(int i){
        mSelectionIndex = i;
    }

    public void clearSelectionIndex(){
        mSelectionIndex = 0;
    }

    private void populateFilterList(){
        mFilters = new ArrayList<>();
        mFilters.add(new StaticFilter("CenterTest", R.drawable.mirror_mirror_logo, Filter.FilterType.CENTER,0,0,0,0));
        mFilters.add(new StaticFilter("FrameTest", R.drawable.mirror_mirror_logo, Filter.FilterType.FULL,0,0,0,0));
        mFilters.add(new StaticFilter("BannerTopTest", R.drawable.mirror_mirror_logo, Filter.FilterType.BANNER_TOP,0,0,0,0));
        mFilters.add(new StaticFilter("BannerBotTest", R.drawable.mirror_mirror_logo, Filter.FilterType.BANNER_BOTTOM, 0,0,0,0));
    }


}
