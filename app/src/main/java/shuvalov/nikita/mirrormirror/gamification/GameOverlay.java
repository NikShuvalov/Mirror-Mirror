package shuvalov.nikita.mirrormirror.gamification;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;

import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;

/**
 * Created by NikitaShuvalov on 5/10/17.
 */

public class GameOverlay extends BaseOverlay {
    private Paint mBluePaint;
    private Paint mGreyPaint;
    private Paint mScorePaint;
    private Paint mRedPaint;
    private Paint mBlackPaint;
    private Rect mGoalBounds;
    private static final float mTextSize = 40f;

    private SoccerEngine mSoccerEngine;

    public GameOverlay(Context context) {
        super(context);
        mBluePaint= new Paint();
        mBluePaint.setColor(Color.BLUE);
        mGreyPaint = new Paint();
        mGreyPaint.setColor(Color.LTGRAY);
        mRedPaint = new Paint();
        mRedPaint.setColor(Color.RED);
        mBlackPaint = new Paint();
        mBlackPaint.setColor(Color.BLACK);
        mScorePaint = new Paint();
        mScorePaint.setColor(Color.RED);
        mScorePaint.setTextSize(mTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(canvas!=null) {
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
            RectF face = FaceTracker.getInstance().getFaceRect();
            if (face != null) {
                adjustHitBox(face);
                canvas.drawRect(face, mBluePaint);
            }
            mSoccerEngine.updateFacePosition(face);
            //ToDo: Draw rounded Rect for API 21+

            Ball soccerBall = mSoccerEngine.getSoccerBall();
            canvas.drawRect(mGoalBounds, mGreyPaint);
            canvas.drawText("Score: "+ mSoccerEngine.getPlayerScore(), mGoalBounds.centerX() - mTextSize , mGoalBounds.top + mTextSize, mScorePaint);

            canvas.drawCircle((float)soccerBall.getCenterX(), (float)soccerBall.getCenterY(), (float)soccerBall.getRadius(), soccerBall.getPaint());
        }
        mSoccerEngine.moveSoccerBall();
    }

    public void adjustHitBox(RectF hitBox){
        float centerX = hitBox.centerX();
        float centerY = hitBox.centerY();
        float faceRadius = SoccerEngine.FACE_LENGTH/2; //It's not really a radius because it's a rectangle, but it's easier to name it this way.
        hitBox.set(centerX-faceRadius, centerY-faceRadius, centerX+faceRadius, centerY+faceRadius);
    }

    public void setSoccerEngine(SoccerEngine soccerEngine){
        mSoccerEngine = soccerEngine;
        mGoalBounds = mSoccerEngine.getGoalBounds();
    }
}
