package shuvalov.nikita.mirrormirror.filters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import shuvalov.nikita.mirrormirror.BaseFilterManager;
import shuvalov.nikita.mirrormirror.R;

/**
 * Created by NikitaShuvalov on 4/15/17.
 */

public class FilterSelectorAdapter extends RecyclerView.Adapter<FilterSelectorViewHolder>{
    private FilterSelectorListener mFilterSelectorListener;
    private BaseFilterManager mFilterManager;
    private int mSelectedIndex;

    public FilterSelectorAdapter(FilterSelectorListener filterSelectorListener, BaseFilterManager baseFilterManager) {
        mFilterSelectorListener = filterSelectorListener;
        mFilterManager = baseFilterManager;
    }

    @Override
    public FilterSelectorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilterSelectorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter, null));
    }

    @Override
    public void onBindViewHolder(final FilterSelectorViewHolder holder, int position) {
        holder.bindDataToViews(mFilterManager.getFilters().get(position));
        int selectedPosition = mFilterManager.getSelectedIndex();
        holder.markAsSelected(position == selectedPosition);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterSelectorListener.onFilterSelected(holder.getAdapterPosition()<0 ? 0:holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
    }



    @Override
    public int getItemCount() {
        return mFilterManager.getFilters().size();
    }


    public interface FilterSelectorListener{
        void onFilterSelected(int index);
    }
}
