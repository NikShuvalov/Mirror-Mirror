package shuvalov.nikita.mirrormirror.filters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.overlay.FilterOverlay;

/**
 * Created by NikitaShuvalov on 4/15/17.
 */

public class FilterSelectorAdapter extends RecyclerView.Adapter<FilterSelectorViewHolder> {
    private ArrayList<Filter> mFilters;
    private FilterOverlay mFilterOverlay;

    public FilterSelectorAdapter(ArrayList<Filter> filters, FilterOverlay filterOverlay) {
        mFilters = filters;
        mFilterOverlay = filterOverlay;
    }

    @Override
    public FilterSelectorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilterSelectorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter, null));
    }

    @Override
    public void onBindViewHolder(final FilterSelectorViewHolder holder, int position) {
        holder.bindDataToViews(mFilters.get(position));
        int selectedPosition = FilterManager.getInstance().getCurrentPosition();
        holder.markAsSelected(position == selectedPosition);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterManager.getInstance().setCurrentPosition(holder.getAdapterPosition());
                mFilterOverlay.notifyFilterChange();
                notifyDataSetChanged(); //FixMe: Optimize
            }
        });
    }



    @Override
    public int getItemCount() {
        return mFilters.size();
    }
}
