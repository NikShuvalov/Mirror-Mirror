package shuvalov.nikita.mirrormirror.particles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.SystemClock;

import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;
import shuvalov.nikita.mirrormirror.particles.Particle;
import shuvalov.nikita.mirrormirror.particles.ParticleEngine;

/**
 * Created by NikitaShuvalov on 5/8/17.
 */

public class ParticleOverlay extends BaseOverlay {
    private RectF mParticleRect;
    private ParticleEngine mParticleEngine;

    public ParticleOverlay(Context context) {
        super(context);
        mParticleRect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        RectF faceRect =FaceTracker.getInstance().getFaceRect();
        ArrayList<Particle> particles = mParticleEngine.getParticles();
        for (int i= 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            double scale = p.getScale();
            int sideLen = (int) scale * 100;
            int top = (int) p.getYLoc() - sideLen / 2;
            int left = (int) p.getXLoc() - sideLen / 2;
            mParticleRect.set(left, top, left + sideLen, top + sideLen);
            if ((faceRect != null && faceRect.contains(mParticleRect)) && scale < 1.75) { //If the particle is located in the bounds of the face and has a large "distance" emulate it becoming hidden.
                //If scale is less than 1 than the bitmap is never drawn in the first place, hence why checking for scale <1 made no difference.
            } else {
                canvas.drawBitmap(p.getCurrentBitMap(SystemClock.elapsedRealtime()), null, mParticleRect, null);
            }
        }
        if(faceRect!=null){
            mParticleEngine.updateFacePosition(faceRect.centerX(), faceRect.centerY());
        }else{
            mParticleEngine.updateFacePosition(Float.MIN_VALUE, Float.MIN_VALUE);
        }
        mParticleEngine.moveParticles();
    }

    public void setParticleEngine(ParticleEngine p){
        mParticleEngine = p;
    }

}
