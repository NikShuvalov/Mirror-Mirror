package shuvalov.nikita.mirrormirror.filters.particles;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;
import shuvalov.nikita.mirrormirror.overlay.ParticleOverlay;

/**
 * Created by NikitaShuvalov on 5/24/17.
 */

public class ParticleRecyclerAdapter extends RecyclerView.Adapter<ParticleViewHolder> {
    private ArrayList<Particle> mAvailableParticles;
    private EngineIgnitionListener mEngineIgnitionListener;

    public ParticleRecyclerAdapter(EngineIgnitionListener engineIgnitionListener, ArrayList<Particle> availableParticles) {
        mAvailableParticles = availableParticles;
        mEngineIgnitionListener = engineIgnitionListener;
    }

    @Override
    public ParticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ParticleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter,null));
    }

    @Override
    public void onBindViewHolder(ParticleViewHolder holder, final int position) {
        holder.bindDataToViews(mAvailableParticles.get(position));
        holder.markAsSelected(position == ParticleManager.getInstance().getCurrentParticleIndex());
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParticleManager.getInstance().setCurrentParticleIndex(position);
                if(position == 0){
                    mEngineIgnitionListener.onEngineShutDown();
                }else{
                    mEngineIgnitionListener.onEngineIgnition();
                }
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mAvailableParticles.size();
    }

    public interface EngineIgnitionListener{
        void onEngineIgnition();
        void onEngineShutDown();
    }
}
