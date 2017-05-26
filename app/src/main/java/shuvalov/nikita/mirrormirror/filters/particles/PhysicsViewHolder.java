package shuvalov.nikita.mirrormirror.filters.particles;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import shuvalov.nikita.mirrormirror.R;

/**
 * Created by NikitaShuvalov on 5/24/17.
 */

public class PhysicsViewHolder extends RecyclerView.ViewHolder {
    public CardView mCardView;
    private ImageView mImageView;
    private TextView mTextView;

    public PhysicsViewHolder(View itemView) {
        super(itemView);

        mCardView = (CardView)itemView.findViewById(R.id.filter_selector_card);
        mImageView = (ImageView) itemView.findViewById(R.id.filter_preview_image);
        mTextView = (TextView)itemView.findViewById(R.id.filter_name_text);
    }

    public void bindDataToViews(ParticleEngine.PhysicsType pType){
        switch (pType){
            case SIMPLE:
                mTextView.setText("Simple");
                mImageView.setImageResource(R.drawable.icon_simple);
                break;
            case OSCILLATING:
                mTextView.setText("Wavy");
                mImageView.setImageResource(R.drawable.icon_wavy);
                break;
            case RADIATING:
                mTextView.setText("Radiating");
                mImageView.setImageResource(R.drawable.icon_radial);
                break;
        }
    }

    public void markAsSelected(boolean isSelected){
        if(isSelected){
            mCardView.setBackgroundColor(Color.argb(255,220, 220,255));
            mTextView.setTextColor(Color.WHITE);
        }else{
            mCardView.setBackgroundColor(Color.argb(225,255,255,255));
            mTextView.setTextColor(Color.BLACK);
        }
    }
}
