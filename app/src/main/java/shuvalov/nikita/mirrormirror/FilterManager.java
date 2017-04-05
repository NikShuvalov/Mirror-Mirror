package shuvalov.nikita.mirrormirror;

import java.util.ArrayList;

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
        mFilters.add(new Filter(R.drawable.beautify_mirror, Filter.ImagePosition.FACE,1.25f, 1.5f, 0, 0));
        mFilters.add(new Filter(R.drawable.corgi, Filter.ImagePosition.FACE,1.25f, 1.5f, 0, 0));
        mFilters.add(new Filter(R.drawable.top_hat, Filter.ImagePosition.TOP_OF_HEAD, 1.5f, 1.5f, 0, -0.75f));
        mFilters.add(new Filter(R.drawable.trump_toupee, Filter.ImagePosition.HAIRLINE, 1.25f, 0.65f, 0, -0.5f));
        mFilters.add(new Filter(R.drawable.super_saiyan, Filter.ImagePosition.HAIRLINE, 2.25f, 1.75f, 0, -0.6f));
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

}
