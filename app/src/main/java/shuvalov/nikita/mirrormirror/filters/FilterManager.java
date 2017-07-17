package shuvalov.nikita.mirrormirror.filters;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;

import shuvalov.nikita.mirrormirror.BaseFilterManager;
import shuvalov.nikita.mirrormirror.R;

/**
 * Created by NikitaShuvalov on 4/2/17.
 */

public class FilterManager extends BaseFilterManager{

    private FilterManager() {
        super();
        prepareAllImages();
    }

    private void prepareAllImages(){
        ArrayList<Filter> filters = new ArrayList<>();
        filters.add(null);
        filters.add(new StaticFilter("Instant Beauty", R.drawable.beautify_mirror, Filter.FilterType.FACE,1.1f, 1.2f, 0, 0));
        filters.add(new StaticFilter("Corgi", R.drawable.corgi, Filter.FilterType.FACE,1.1f, 1.2f, 0, 0));
        filters.add(new StaticFilter("Top Hat", R.drawable.top_hat, Filter.FilterType.TOP_OF_HEAD, 1f, 1f, 0, -0.5f));
        filters.add(new StaticFilter("Truompee", R.drawable.trump_toupee, Filter.FilterType.HAIRLINE, 0.85f, 0.4f, 0, -0.35f));
        filters.add(new StaticFilter("Saiyan", R.drawable.super_saiyan, Filter.FilterType.HAIRLINE, 1.75f, 1.5f, 0, -0.5f));
        super.setFilters(filters);
    }

    private static FilterManager sFilterManager;


    public static FilterManager getInstance() {
        if(sFilterManager== null){
            sFilterManager = new FilterManager();
        }
        return sFilterManager;
    }

    public void addAnimatedFilters(AnimatedFilter... animatedFilters){
        for(Filter f: animatedFilters){
            super.addFilter(f);
        }
    }

}
