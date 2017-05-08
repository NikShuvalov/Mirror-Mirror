package shuvalov.nikita.mirrormirror.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.filters.particles.Particle;
import shuvalov.nikita.mirrormirror.filters.particles.ParticleEngine;

/**
 * Created by NikitaShuvalov on 5/8/17.
 */

public class ParticleOverlay extends BaseOverlay {
    private Bitmap mBitmap;
    private RectF mRectF;
    private ParticleEngine mParticleEngine;

    public ParticleOverlay(Context context) {
        super(context);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flamekey0);
        mRectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //ToDo: Track last position of face and determine movement appropriately.
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        ArrayList<Particle> particles = mParticleEngine.getParticles();
        RectF faceRect =FaceTracker.getInstance().getFaceRect();
        for(Particle p: particles){
            float scale = p.getScale();
            int sideLen = (int)scale*100;
            int top = (int)p.getYLoc()-sideLen/2;
            int left = (int)p.getXLoc()-sideLen/2;
            mRectF.set(left, top, left+sideLen, top+sideLen);
            if((faceRect!=null && faceRect.contains(mRectF)) && scale<1.0){ //If the particle is located in the bounds of the face and has a large "distance" emulate it becoming hidden.
                //FixMe: No fucking clue why bitmaps aren't being omitted when they're over the face. It prevents drawing to canvas if I remove scale from the condition, but, at the same time, even with scale this block of code is run; the particles are just still getting drawn.
                Log.d("scale", "onDraw: "+scale);
            }else{
                canvas.drawBitmap(mBitmap, null, mRectF, null);
            }
        }
        mParticleEngine.moveParticles();
    }

    public void setParticleEngine(ParticleEngine p){
        mParticleEngine = p;
    }
}
