package shuvalov.nikita.mirrormirror.filters;

import java.util.ArrayList;

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
        mFilters.add(new Filter("Instant Beauty", R.drawable.beautify_mirror, Filter.ImagePosition.FACE,1.1f, 1.2f, 0, 0));
        mFilters.add(new Filter("Corgi", R.drawable.corgi, Filter.ImagePosition.FACE,1.1f, 1.2f, 0, 0));
        mFilters.add(new Filter("Top Hat", R.drawable.top_hat, Filter.ImagePosition.TOP_OF_HEAD, 1f, 1f, 0, -0.5f));
        mFilters.add(new Filter("Truompee", R.drawable.trump_toupee, Filter.ImagePosition.HAIRLINE, 0.85f, 0.4f, 0, -0.35f));
        mFilters.add(new Filter("Saiyan", R.drawable.super_saiyan, Filter.ImagePosition.HAIRLINE, 1.75f, 1.5f, 0, -0.5f));
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
}
