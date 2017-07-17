package shuvalov.nikita.mirrormirror.video;

import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.BaseFilterManager;
import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.filters.StaticFilter;

/**
 * Created by NikitaShuvalov on 7/14/17.
 */

public class VideoFilterManager extends BaseFilterManager{

    private VideoFilterManager() {
        super();
        populateFilterList();
    }

    private static VideoFilterManager sVideoFilterManager;

    public static VideoFilterManager getInstance() {
        if(sVideoFilterManager == null){
            sVideoFilterManager = new VideoFilterManager();
        }
        return sVideoFilterManager;
    }

    private void populateFilterList(){
        ArrayList<Filter> filters = new ArrayList<>();
        filters.add(new StaticFilter("CenterTest", R.drawable.mirror_mirror_logo, Filter.FilterType.CENTER,0,0,0,0));
        filters.add(new StaticFilter("FrameTest", R.drawable.mirror_mirror_logo, Filter.FilterType.FULL,0,0,0,0));
        filters.add(new StaticFilter("BannerTopTest", R.drawable.mirror_mirror_logo, Filter.FilterType.BANNER_TOP,0,0,0,0));
        filters.add(new StaticFilter("BannerBotTest", R.drawable.mirror_mirror_logo, Filter.FilterType.BANNER_BOTTOM, 0,0,0,0));
        super.setFilters(filters);
    }


}
