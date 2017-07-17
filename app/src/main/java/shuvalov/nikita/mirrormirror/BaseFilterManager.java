package shuvalov.nikita.mirrormirror;

import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.filters.Filter;

/**
 * Created by NikitaShuvalov on 7/14/17.
 */

public abstract class BaseFilterManager {
    private ArrayList<Filter> mFilters;
    private int mSelectedIndex;

    public BaseFilterManager() {
        mFilters = new ArrayList<>();
        mSelectedIndex = 0;
    }

    public ArrayList<Filter> getFilters() {
        return mFilters;
    }

    public void setFilters(ArrayList<Filter> filters) {
        mFilters = filters;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        mSelectedIndex = selectedIndex;
    }

    public void clearSelectionIndex(){
        mSelectedIndex = 0;
    }

    public Filter getSelectedFilter(){
        return mFilters.get(mSelectedIndex);
    }

    public void addFilter(Filter f){
        mFilters.add(f);
    }

}
