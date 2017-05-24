package shuvalov.nikita.mirrormirror.filters.particles;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.R;

/**
 * Created by NikitaShuvalov on 5/24/17.
 */

public class ParticleRecyclerAdapter extends RecyclerView.Adapter<ParticleViewHolder> {
    private ArrayList<Particle> mAvailableParticles;

    public ParticleRecyclerAdapter(ArrayList<Particle> availableParticles) {
        mAvailableParticles = availableParticles;
    }

    @Override
    public ParticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ParticleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter,null));
    }

    @Override
    public void onBindViewHolder(ParticleViewHolder holder, int position) {
        holder.bindDataToViews(mAvailableParticles.get(position));
        holder.markAsSelected(position == ParticleManager.getInstance().getCurrentParticleIndex());
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Select
            }
        });

    }

    @Override
    public int getItemCount() {
        return mAvailableParticles.size();
    }
}
