package shuvalov.nikita.mirrormirror.particles;

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

public class ParticleViewHolder extends RecyclerView.ViewHolder {
    public CardView mCardView;
    private ImageView mImageView;
    private TextView mTextView;

    public ParticleViewHolder(View itemView) {
        super(itemView);
        mCardView = (CardView)itemView.findViewById(R.id.filter_selector_card);
        mImageView = (ImageView) itemView.findViewById(R.id.filter_preview_image);
        mTextView = (TextView)itemView.findViewById(R.id.filter_name_text);
    }

    public void bindDataToViews(Particle p){
        if(p!=null) {
            mTextView.setText(p.getName());
            mImageView.setImageBitmap(p.getPreviewBitmap());
        }else{
            mTextView.setText("Empty");
            mImageView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
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
