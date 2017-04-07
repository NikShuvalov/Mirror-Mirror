package shuvalov.nikita.mirrormirror.browsing;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by NikitaShuvalov on 4/7/17.
 */

public class BrowsingTracker {
    private ArrayList<File> mImageFiles;
    private int mCurrentIndex;

    public BrowsingTracker() {
        mImageFiles = new ArrayList<>();
        mCurrentIndex = 0;
    }

    private static BrowsingTracker sBrowsingTracker;

    public static BrowsingTracker getInstance() {
        if (sBrowsingTracker == null) {
            sBrowsingTracker = new BrowsingTracker();
        }
        return sBrowsingTracker;
    }

    public void setImageFiles(ArrayList<File> imageFiles) {
        mImageFiles = imageFiles;
    }

    public File getCurrentImageFile(){
        return mImageFiles.get(mCurrentIndex);
    }

    public void moveToNextPicture(){
        mCurrentIndex++;
        if(mCurrentIndex>=mImageFiles.size()){
            mCurrentIndex=0;
        }
    }

    public void moveToPreviousPicture(){
        mCurrentIndex--;
        if(mCurrentIndex<0){
            mCurrentIndex= mImageFiles.size()-1;
        }
    }

    public boolean isAlbumEmpty(){
        return mImageFiles.isEmpty();
    }
}
