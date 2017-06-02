package shuvalov.nikita.mirrormirror.gamification;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;

import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;

/**
 * Created by NikitaShuvalov on 5/10/17.
 */

public class GameOverlay extends BaseOverlay {
    private Paint mBluePaint, mScoreBoxPaint, mGoalPaint;
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
        createPaints();
        mTrianglePath = new Path();
    }

    private void createPaints(){
        mBluePaint= new Paint();
        mBluePaint.setColor(Color.BLUE);
        mBluePaint.setStyle(Paint.Style.STROKE);
        mBluePaint.setStrokeWidth(2f);

        mGoalPaint = new Paint();
        mGoalPaint.setColor(Color.argb(255,25,25,25));

        mGreyPaint = new Paint();
        mGreyPaint.setColor(Color.LTGRAY);

        mRedPaint = new Paint();
        mRedPaint.setColor(Color.RED);

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5);

        mScoreBoxPaint = new Paint();
        mScoreBoxPaint.setColor(Color.argb(255, 255, 200, 255));

        mScorePaint = new Paint();
        mScorePaint.setColor(Color.RED);
        mScorePaint.setTextSize(mTextSize);
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
            canvas.drawCircle(mGoalBounds.centerX(),mGoalBounds.centerY(), mGoalBounds.width()/2, mGoalPaint);
            drawScoreBox(canvas);
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
        mSoccerEngine.process();
    }

    private void drawScoreBox(Canvas canvas){
        String scoreText = "Score: " + mSoccerEngine.getPlayerScore();
        float boxStart = canvas.getWidth()*.75f;
        float boxMargin = 16;
        canvas.drawRect(boxStart, boxMargin, (float)canvas.getWidth() - boxMargin, (boxMargin*2) + mTextSize, mScoreBoxPaint);
        canvas.drawText(scoreText, boxStart + 16, boxMargin+ mTextSize, mScorePaint);
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
