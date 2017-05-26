package shuvalov.nikita.mirrormirror.filters;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import shuvalov.nikita.mirrormirror.R;

/**
 * Created by NikitaShuvalov on 4/15/17.
 */

public class FilterSelectorViewHolder extends RecyclerView.ViewHolder {
    public CardView mCardView;
    private TextView mFiltNameText;
    private ImageView mFiltPreviewImgView;

    public FilterSelectorViewHolder(View itemView) {
        super(itemView);
        mCardView = (CardView)itemView.findViewById(R.id.filter_selector_card);
        mFiltNameText = (TextView)itemView.findViewById(R.id.filter_name_text);
        mFiltPreviewImgView = (ImageView)itemView.findViewById(R.id.filter_preview_image);
    }

    public void bindDataToViews(Filter f){
        mFiltNameText.setText(f.getFilterName());
        mFiltPreviewImgView.setImageResource(f.getResourceInt());
    }

    public void markAsSelected(boolean isSelected){
        if(isSelected){
            mCardView.setBackgroundColor(Color.argb(255,220, 220,255));
            mFiltNameText.setTextColor(Color.WHITE);
        }else{
            mCardView.setBackgroundColor(Color.argb(225,255,255,255));
            mFiltNameText.setTextColor(Color.BLACK);
        }
    }
}
