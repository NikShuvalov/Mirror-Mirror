package shuvalov.nikita.mirrormirror.gamification;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
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
    private Paint mLinePaint;
    private Rect mGoalBounds;
    private Path mTrianglePath;
    private static final float mTextSize = 40f;

    private SoccerEngine mSoccerEngine;

    public GameOverlay(Context context) {
        super(context);
        mBluePaint= new Paint();
        mBluePaint.setColor(Color.BLUE);
        mBluePaint.setStyle(Paint.Style.STROKE);
        mBluePaint.setStrokeWidth(2f);

        mGreyPaint = new Paint();
        mGreyPaint.setColor(Color.LTGRAY);
        mRedPaint = new Paint();
        mRedPaint.setColor(Color.RED);

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5);

        mScorePaint = new Paint();
        mScorePaint.setColor(Color.RED);
        mScorePaint.setTextSize(mTextSize);

        mTrianglePath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(canvas!=null) {
            Ball soccerBall = mSoccerEngine.getSoccerBall();
            float soccerBallRadius = (float)soccerBall.getRadius();
            double soccerBallBottom = soccerBall.getCenterY() + soccerBallRadius;
            float soccerBallCenterX = (float)soccerBall.getCenterX();
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
            RectF face = FaceTracker.getInstance().getFaceRect();
            if (face != null) {
                adjustHitBox(face);
                canvas.drawRect(face, mBluePaint);
            }
            mSoccerEngine.updateFacePosition(face);

            canvas.drawRect(mGoalBounds, mGreyPaint);
            canvas.drawText("Score: "+ mSoccerEngine.getPlayerScore(), mGoalBounds.centerX() - mTextSize , mGoalBounds.top + mTextSize, mScorePaint);

            canvas.drawCircle(soccerBallCenterX, (float)soccerBall.getCenterY(), (float)soccerBall.getRadius(), soccerBall.getPaint());
            if (soccerBallBottom <= 0){

                mTrianglePath.moveTo(soccerBallCenterX - soccerBallRadius, soccerBallRadius + 20);
                mTrianglePath.lineTo(soccerBallCenterX, 20);
                mTrianglePath.lineTo(soccerBallCenterX + soccerBallRadius, soccerBallRadius + 20);
                mTrianglePath.close();
                canvas.drawPath(mTrianglePath, mRedPaint);
                canvas.drawPath(mTrianglePath, mLinePaint);
                canvas.drawText((int)(Math.abs(soccerBallBottom)/25) + "cm", soccerBallCenterX - soccerBallRadius, soccerBallRadius + 90, mScorePaint);
                mTrianglePath.reset();
            }
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
