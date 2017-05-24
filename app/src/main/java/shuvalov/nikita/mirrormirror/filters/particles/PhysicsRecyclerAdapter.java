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

public class PhysicsRecyclerAdapter extends RecyclerView.Adapter<PhysicsViewHolder> {
    private ParticleEngine.PhysicsType[] mSupportedPhysicTypes;

    public PhysicsRecyclerAdapter(ParticleEngine.PhysicsType[] supportedPhysicTypes) {
        mSupportedPhysicTypes = supportedPhysicTypes;
    }

    @Override
    public PhysicsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhysicsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter, null));
    }

    @Override
    public void onBindViewHolder(PhysicsViewHolder holder, int position) {
        ParticleEngine.PhysicsType pType = mSupportedPhysicTypes[position];
        holder.bindDataToViews(pType);
        holder.markAsSelected(pType == ParticleManager.getInstance().getPhysicsType());
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do the thing.
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSupportedPhysicTypes.length;
    }
}
