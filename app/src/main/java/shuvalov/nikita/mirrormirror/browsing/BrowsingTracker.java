package shuvalov.nikita.mirrormirror.browsing;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static android.content.ContentValues.TAG;

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
        Collections.reverse(mImageFiles);
        mCurrentIndex = 0;
    }

    public File getCurrentImageFile(){
        return mImageFiles.get(mCurrentIndex);
    }

    public void moveToNextPicture(){
        mCurrentIndex++;
        if(mCurrentIndex>=mImageFiles.size()){
            mCurrentIndex=0;
        }
        Log.d(TAG, "moveToNextPicture: " + mCurrentIndex);
    }

    public void moveToPreviousPicture(){
        mCurrentIndex--;
        if(mCurrentIndex<0){
            mCurrentIndex= mImageFiles.size()-1;
        }
        Log.d(TAG, "moveToPreviousPicture: " + mCurrentIndex);

    }

    public boolean isAlbumEmpty(){
        return mImageFiles.isEmpty();
    }

    public void clearCache(){
        mImageFiles.clear();
        mCurrentIndex = 0;
    }

    public int albumSize(){
        return mImageFiles.size();
    }
}
