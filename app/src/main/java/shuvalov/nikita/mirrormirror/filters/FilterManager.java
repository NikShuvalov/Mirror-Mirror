package shuvalov.nikita.mirrormirror.filters;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;

import shuvalov.nikita.mirrormirror.R;

/**
 * Created by NikitaShuvalov on 4/2/17.
 */

public class FilterManager {
    private ArrayList<Filter> mFilters;
    private int mCurrentPosition;

    private FilterManager() {
        mCurrentPosition=0;
        prepareAllImages();
    }

    private void prepareAllImages(){
        mFilters = new ArrayList<>();
        mFilters.add(new StaticFilter("Instant Beauty", R.drawable.beautify_mirror, Filter.ImagePosition.FACE,1.1f, 1.2f, 0, 0));
        mFilters.add(new StaticFilter("Corgi", R.drawable.corgi, Filter.ImagePosition.FACE,1.1f, 1.2f, 0, 0));
        mFilters.add(new StaticFilter("Top Hat", R.drawable.top_hat, Filter.ImagePosition.TOP_OF_HEAD, 1f, 1f, 0, -0.5f));
        mFilters.add(new StaticFilter("Truompee", R.drawable.trump_toupee, Filter.ImagePosition.HAIRLINE, 0.85f, 0.4f, 0, -0.35f));
        mFilters.add(new StaticFilter("Saiyan", R.drawable.super_saiyan, Filter.ImagePosition.HAIRLINE, 1.75f, 1.5f, 0, -0.5f));
    }

    private static FilterManager sFilterManager;


    public static FilterManager getInstance() {
        if(sFilterManager== null){
            sFilterManager = new FilterManager();
        }
        return sFilterManager;
    }

    public void moveToNextPosition(){
        mCurrentPosition++;
        if(mCurrentPosition>=mFilters.size()){
            mCurrentPosition=0;
        }
    }

    public Filter getSelectedFilter(){
        return mFilters.get(mCurrentPosition);
    }

    public ArrayList<Filter> getFilters() {
        return mFilters;
    }

    public void setCurrentPosition(int newPosition){
        mCurrentPosition = newPosition;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void addAnimatedFilters(AnimatedFilter... animatedFilters){
        for(Filter f: animatedFilters){
            mFilters.add(f);
        }
    }

    public void dirtyDebuggingCode(Filter f){
        mFilters.add(f);
        mCurrentPosition = mFilters.size()-1;
    }
}
