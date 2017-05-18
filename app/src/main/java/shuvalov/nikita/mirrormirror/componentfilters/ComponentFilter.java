package shuvalov.nikita.mirrormirror.componentfilters;

import android.graphics.Bitmap;

import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.AppConstants;
import shuvalov.nikita.mirrormirror.filters.Filter;

/**
 * Created by NikitaShuvalov on 5/18/17.
 */

public class ComponentFilter{
    private String mFilterName;
    private ArrayList<Filter> mComponentList;
    private ArrayList<Bitmap> mComponentBitmaps;


    public ComponentFilter(String filterName, ArrayList<Filter> componentList) {
        mFilterName = filterName;
        mComponentList = componentList;
//        mComponentBitmaps = AppConstants.getBitmapList() ?
    }

    public void addComponent(Filter f){
        mComponentList.add(f);
    }

    public String getFilterName() {
        return mFilterName;
    }

    public ArrayList<Filter> getComponentList() {
        return mComponentList;
    }

    public ArrayList<Bitmap> getComponentBitmaps() {
        return mComponentBitmaps;
    }
}
