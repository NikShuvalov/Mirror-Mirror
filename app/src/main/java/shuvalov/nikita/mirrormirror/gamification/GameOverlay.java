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
    private Paint mBoundaryPaint;
    private Paint mScorePaint;
    private Paint mRedPaint;
    private Paint mLinePaint;
    private Rect mGoalBounds;
    private Path mTrianglePath;
    private static final float TEXT_SIZE = 40f;
    private static final float BOUNDARY_PAINT_WIDTH = 30f;

    private SoccerEngine mSoccerEngine;

    public GameOverlay(Context context) {
        super(context);
        createPaints();
        mTrianglePath = new Path();
    }

    private void createPaints(){
        mBoundaryPaint = new Paint();
        mBoundaryPaint.setColor(Color.argb(255, 220,235,220));
        mBoundaryPaint.setStrokeWidth(BOUNDARY_PAINT_WIDTH);
        mBoundaryPaint.setStyle(Paint.Style.STROKE);

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
        mScorePaint.setTextSize(TEXT_SIZE);
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
            drawBoundaryLine(canvas);
            drawFaceOutline(canvas);
            canvas.drawCircle(mGoalBounds.centerX(),mGoalBounds.centerY(), mGoalBounds.width()/2, mGoalPaint);//Draws goal, aka black hole of death.
            drawScoreBox(canvas);
            canvas.drawCircle(soccerBallCenterX, (float)soccerBall.getCenterY(), (float)soccerBall.getRadius(), soccerBall.getPaint());
            if (soccerBallBottom <= 0){
                drawIndicator(canvas, soccerBallRadius, soccerBallBottom, soccerBallCenterX);
            }
        }
        mSoccerEngine.process();
    }

    private void drawBoundaryLine(Canvas canvas){
        int y = mSoccerEngine.getBoundaryLine();
        canvas.drawLine(0,y,canvas.getWidth(), y, mBoundaryPaint);
    }

    private void drawIndicator(Canvas canvas, float soccerBallRadius, double soccerBallBottom, float soccerBallCenterX){
        mTrianglePath.moveTo(soccerBallCenterX - soccerBallRadius, soccerBallRadius + 20);
        mTrianglePath.lineTo(soccerBallCenterX, 20);
        mTrianglePath.lineTo(soccerBallCenterX + soccerBallRadius, soccerBallRadius + 20);
        mTrianglePath.close();
        canvas.drawPath(mTrianglePath, mRedPaint);
        canvas.drawPath(mTrianglePath, mLinePaint);
        canvas.drawText((int)(Math.abs(soccerBallBottom)/25) + "cm", soccerBallCenterX - soccerBallRadius, soccerBallRadius + 90, mScorePaint);
        mTrianglePath.reset();
    }

    private void drawFaceOutline(Canvas canvas){
        mSoccerEngine.updateFacePosition(FaceTracker.getInstance().getFaceRect());
        RectF face = mSoccerEngine.getFaceRect();
        if (face != null) {
            canvas.drawRect(face, mBluePaint);
        }
    }
    private void drawScoreBox(Canvas canvas){
        String scoreText = "Score: " + mSoccerEngine.getPlayerScore();
        float boxStart = canvas.getWidth()*.75f;
        float boxMargin = 16;
        canvas.drawRect(boxStart, boxMargin, (float)canvas.getWidth() - boxMargin, (boxMargin*2) + TEXT_SIZE, mScoreBoxPaint);
        canvas.drawText(scoreText, boxStart + 16, boxMargin+ TEXT_SIZE, mScorePaint);
    }

    public void setSoccerEngine(SoccerEngine soccerEngine){
        mSoccerEngine = soccerEngine;
        mGoalBounds = mSoccerEngine.getGoalBounds();
    }
}
