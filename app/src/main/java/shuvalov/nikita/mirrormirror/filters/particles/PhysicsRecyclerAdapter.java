package shuvalov.nikita.mirrormirror.filters.particles;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;

/**
 * Created by NikitaShuvalov on 5/24/17.
 */

public class PhysicsRecyclerAdapter extends RecyclerView.Adapter<PhysicsViewHolder> {
    private ParticleEngine.PhysicsType[] mSupportedPhysicTypes;
    private ParticleEngine mParticleEngine;

    public PhysicsRecyclerAdapter(ParticleEngine.PhysicsType[] supportedPhysicTypes, ParticleEngine particleEngine) {
        mSupportedPhysicTypes = supportedPhysicTypes;
        mParticleEngine = particleEngine;
    }

    @Override
    public PhysicsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhysicsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter, null));
    }

    @Override
    public void onBindViewHolder(PhysicsViewHolder holder, final int position) {
        ParticleEngine.PhysicsType pType = mSupportedPhysicTypes[position];
        holder.bindDataToViews(pType);
        holder.markAsSelected(pType == ParticleManager.getInstance().getPhysicsType());
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParticleManager particleManager = ParticleManager.getInstance();
                particleManager.setPhysicsType(position);
                mParticleEngine.changePhysicsType(particleManager.getPhysicsType());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSupportedPhysicTypes.length;
    }
}
