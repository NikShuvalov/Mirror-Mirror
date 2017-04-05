package shuvalov.nikita.mirrormirror;

import java.util.ArrayList;

/**
 * Created by NikitaShuvalov on 4/2/17.
 */

public class FilterManager {
    private ArrayList<Integer> mResourceInts;
    private int mCurrentPosition;
    private Filter.ImagePosition mImagePosition;

    private FilterManager() {
        mCurrentPosition=0;
        mImagePosition = Filter.ImagePosition.FACE;
        prepareAllImages();
    }

    private void prepareAllImages(){
        mResourceInts = new ArrayList<>();
        mResourceInts.add(R.drawable.beautify_mirror);
//        mResourceInts.add(R.drawable.badman);
        mResourceInts.add(R.drawable.corgi);
//        mResourceInts.add(R.drawable.eye_of_sauron);
        mResourceInts.add(R.drawable.top_hat);
        mResourceInts.add(R.drawable.trump_toupee);
        mResourceInts.add(R.drawable.super_saiyan);
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
        mImagePosition = Filter.ImagePosition.FACE;
        if(mCurrentPosition>=mResourceInts.size()){
            mCurrentPosition=0;
        }

        if(mCurrentPosition==2) mImagePosition = Filter.ImagePosition.TOP_OF_HEAD;
        else if(mCurrentPosition >=3) mImagePosition = Filter.ImagePosition.HAIRLINE;
    }

    public Integer getSelectedRes(){
        return mResourceInts.get(mCurrentPosition);
    }

    public Filter.ImagePosition getImagePosition() {
        return mImagePosition;
    }

}
