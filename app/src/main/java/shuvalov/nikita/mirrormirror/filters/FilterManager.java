package shuvalov.nikita.mirrormirror.filters;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Collection;

import shuvalov.nikita.mirrormirror.AppConstants;
import shuvalov.nikita.mirrormirror.BaseFilterManager;
import shuvalov.nikita.mirrormirror.R;

/**
 * Created by NikitaShuvalov on 4/2/17.
 */

public class FilterManager extends BaseFilterManager{

    private FilterManager() {
        super();
    }

    public void prepareAllImages(Context context){
        if(super.getFilters()== null || super.getFilters().isEmpty()) {
            ArrayList<Filter> filters = new ArrayList<>();
            filters.add(null);
            filters.add(new StaticFilter("Instant Beauty", BitmapFactory.decodeResource(context.getResources(), R.drawable.beautify_mirror), Filter.FilterType.FACE, 1.1f, 1.2f, 0, 0));
            filters.add(new StaticFilter("Corgi", BitmapFactory.decodeResource(context.getResources(), R.drawable.corgi), Filter.FilterType.FACE, 1.1f, 1.2f, 0, 0));
            filters.add(new StaticFilter("Truompee", BitmapFactory.decodeResource(context.getResources(), R.drawable.trump_toupee), Filter.FilterType.HAIRLINE, 0.85f, 0.4f, 0, -0.35f));
            filters.add(new StaticFilter("Saiyan", BitmapFactory.decodeResource(context.getResources(), R.drawable.super_saiyan), Filter.FilterType.HAIRLINE, 1.75f, 1.5f, 0, -0.5f));
            filters.add(new AnimatedFilter("Flames", BitmapFactory.decodeResource(context.getResources(), R.drawable.flamekey0), Filter.FilterType.FACE, 1.25f, 1.5f, 0, -0.65f, AppConstants.getBitmapList(context, R.array.flame_animation_list)));
            super.setFilters(filters);
        }
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
