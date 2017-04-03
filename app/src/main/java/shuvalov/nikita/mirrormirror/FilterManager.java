package shuvalov.nikita.mirrormirror;

import java.util.ArrayList;

/**
 * Created by NikitaShuvalov on 4/2/17.
 */

public class FilterManager {
    private ArrayList<Integer> mResourceInts;
    private int mCurrentPosition;

    private FilterManager() {
        mCurrentPosition=0;
        prepareAllImages();

    }

    private void prepareAllImages(){
        mResourceInts = new ArrayList<>();
        mResourceInts.add(R.drawable.beautify_mirror);
//        mResourceInts.add(R.drawable.badman);
        mResourceInts.add(R.drawable.corgi);
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
        if(mCurrentPosition>=mResourceInts.size()){
            mCurrentPosition=0;
        }
    }

    public Integer getSelectedRes(){
        return mResourceInts.get(mCurrentPosition);
    }
}
