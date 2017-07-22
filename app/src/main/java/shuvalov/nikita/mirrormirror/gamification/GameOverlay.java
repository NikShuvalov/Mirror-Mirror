package shuvalov.nikita.mirrormirror.gamification;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;

import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;

import static android.content.ContentValues.TAG;

/**
 * Created by NikitaShuvalov on 5/10/17.
 */

public class GameOverlay extends BaseOverlay{
    private Path mTrianglePath;
    private Paint mAlphaPaint;
    private static final float TEXT_SIZE = 40f;
    private static final float BOUNDARY_PAINT_WIDTH = 30f;
    private static final int OVERLAY_ALPHA = 150;
    private float mStartBlinkTime;
    private boolean mStartBlink;

    private SoccerEngine mSoccerEngine;

    public GameOverlay(Context context, Rect screenBounds) {
        super(context);
        mAlphaPaint = new Paint();
        mAlphaPaint.setAlpha(OVERLAY_ALPHA);
        mTrianglePath = new Path();
        mStartBlinkTime = 0;
        mStartBlink = true;
        new EngineLoaderTask().execute(screenBounds); //ToDo: Perform this in loading activity
    }
//
//    private void createTutorialArrows(){
//        Rect goalBounds = mSoccerEngine.getGoalBounds();
//        float goalRadius = goalBounds.width()/2f;
//        PointF goalArrowOrigin = new PointF(goalBounds.centerX() + goalRadius*2, goalBounds.centerY());
//        mGoalArrowPath = new Path();
//        mGoalArrowPath.moveTo(goalArrowOrigin.x, goalArrowOrigin.y);
//        mGoalArrowPath.rLineTo(goalRadius, goalRadius);
//        mGoalArrowPath.rLineTo(0 ,-goalRadius/2);
//        mGoalArrowPath.arcTo(goalArrowOrigin.x, goalBounds.centerY() + goalBounds.width()/4, goalArrowOrigin.x + goalRadius*2, goalBounds.centerY() + goalBounds.width()/4 + goalRadius*2,270, 90, false);
//        mGoalArrowPath.rLineTo(0, goalRadius);
//        mGoalArrowPath.rLineTo(goalRadius, 0);
//        mGoalArrowPath.rLineTo(0,-goalRadius);
//        mGoalArrowPath.arcTo(goalArrowOrigin.x - goalRadius, goalBounds.centerY() - goalRadius/2, goalArrowOrigin.x + goalRadius*3, goalBounds.centerY() + goalBounds.width()/4 +goalRadius*3, 0,-90,false);
//        mGoalArrowPath.rLineTo(0, -goalRadius/2);
//        mGoalArrowPath.close();
//
//        Ball soccerBall = mSoccerEngine.getSoccerBall();
//        PointF ballCenter = new PointF((float)soccerBall.getCenterX(), (float)soccerBall.getCenterY());
//        float ballRadius = (float) soccerBall.getRadius();
//
//        mBallArrowPath = new Path();
//        mBallArrowPath.moveTo(ballCenter.x - ballRadius, ballCenter.y - ballRadius);
//        mBallArrowPath.rLineTo(0,  - ballRadius * 2);
//        mBallArrowPath.rLineTo( -ballRadius/1.5f, ballRadius/1.5f);
//        mBallArrowPath.rLineTo(-ballRadius * 3 , - ballRadius * 3);
//        mBallArrowPath.rLineTo(- ballRadius/1.5f,  ballRadius/1.5f);
//        mBallArrowPath.rLineTo(ballRadius  * 3, ballRadius * 3);
//        mBallArrowPath.rLineTo(-ballRadius /1.5f, ballRadius /1.5f);
//        mBallArrowPath.close();
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: " + Thread.currentThread().getName());
        if(canvas!=null && mSoccerEngine!=null) {
            GamePalette gamePalette = GamePalette.getInstance();
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
            if (mSoccerEngine.mTutorialCanvas == null) {
                mSoccerEngine.createTutorialCanvas(); //ToDo: Perform this in loading activity
            }
            if (mSoccerEngine.isTutorialMode()) {
                canvas.drawBitmap(mSoccerEngine.getTutorialOverlayBitmap(), null, mSoccerEngine.getScreenBounds(), mAlphaPaint);
                if (mStartBlinkTime < 50 && mStartBlink) {
                    canvas.drawText("Tap to Start", canvas.getWidth() / 3f, canvas.getHeight() * .7f, gamePalette.getStartPaint());
                    mStartBlinkTime++;
                } else if (mStartBlinkTime >= 50) {
                    mStartBlink = !mStartBlink;
                    mStartBlinkTime = 0;
                } else {
                    mStartBlinkTime++;
                }
            }else {
                mSoccerEngine.process();
            }
            Rect goalBounds = mSoccerEngine.getGoalBounds();
            Ball soccerBall = mSoccerEngine.getSoccerBall();
            float soccerBallRadius = (float) soccerBall.getRadius();
            double soccerBallBottom = soccerBall.getCenterY() + soccerBallRadius;
            float soccerBallCenterX = (float) soccerBall.getCenterX();
            float soccerBallCenterY = (float) soccerBall.getCenterY();
            drawBoundaryLine(canvas);
            mSoccerEngine.updateFacePosition(FaceTracker.getInstance().getFaceRect());
            drawFaceOutline(canvas);
            canvas.drawCircle(goalBounds.centerX(), goalBounds.centerY(), goalBounds.width() / 2, gamePalette.getGoalPaint());//Draws goal, aka black hole of death.
            drawScoreBox(canvas);
            canvas.drawCircle(soccerBallCenterX, soccerBallCenterY, soccerBallRadius, soccerBall.getPaint());
            if (soccerBallBottom <= 0) {
                drawIndicator(canvas, soccerBallRadius, soccerBallBottom, soccerBallCenterX);
            }
        }
    }

//    private void drawFaceArrow(Canvas canvas, float ballRadius){
//        RectF face = mSoccerEngine.getFaceRect();
//        Path faceArrow = new Path();
//        if(face!=null) {
//            GamePalette gamePalette = GamePalette.getInstance();
//            faceArrow.moveTo(face.centerX(), canvas.getHeight() / 2 - (ballRadius * 2));
//            faceArrow.rLineTo(ballRadius, -ballRadius);
//            faceArrow.rLineTo(-ballRadius / 2, 0);
//            faceArrow.rLineTo(0, -ballRadius * 2);
//            faceArrow.rLineTo(-ballRadius, 0);
//            faceArrow.rLineTo(0, ballRadius * 2);
//            faceArrow.rLineTo(-ballRadius / 2, 0);
//            faceArrow.close();
//
//            canvas.drawPath(faceArrow, gamePalette.getArrowPaint());
//            canvas.drawPath(faceArrow, gamePalette.getArrowOutlinePaint());
//            canvas.drawText("Your Face", (face.centerX() + face.left)/2f, canvas.getHeight()/2 - (ballRadius *6) + TEXT_SIZE, gamePalette.getIdentifierPaint());
//        }
//    }

    private void drawBoundaryLine(Canvas canvas){
        int y = mSoccerEngine.getBoundaryLine();
        canvas.drawLine(0,y,canvas.getWidth(), y, GamePalette.getInstance().getBoundaryPaint());
    }

    private void drawIndicator(Canvas canvas, float soccerBallRadius, double soccerBallBottom, float soccerBallCenterX){
        GamePalette gamePalette = GamePalette.getInstance();
        mTrianglePath.moveTo(soccerBallCenterX - soccerBallRadius, soccerBallRadius + 20);
        mTrianglePath.lineTo(soccerBallCenterX, 20);
        mTrianglePath.lineTo(soccerBallCenterX + soccerBallRadius, soccerBallRadius + 20);
        mTrianglePath.close();
        canvas.drawPath(mTrianglePath, gamePalette.getRedPaint());
        canvas.drawPath(mTrianglePath, gamePalette.getLinePaint());
        canvas.drawText((int)(Math.abs(soccerBallBottom)/25) + "cm", soccerBallCenterX - soccerBallRadius, soccerBallRadius + 90, gamePalette.getScorePaint());
        mTrianglePath.reset();
    }

    private void drawFaceOutline(Canvas canvas){
        RectF face = mSoccerEngine.getFaceRect();
        if (face != null) {
            canvas.drawRect(face, GamePalette.getInstance().getBluePaint());
        }
    }

    private void drawScoreBox(Canvas canvas){
        GamePalette gamePalette = GamePalette.getInstance();
        String scoreText = "Score: " + mSoccerEngine.getPlayerScore();
        float boxStart = canvas.getWidth()*.75f;
        float boxMargin = 16;
        canvas.drawRect(boxStart, boxMargin, (float)canvas.getWidth() - boxMargin, (boxMargin*2) + TEXT_SIZE, gamePalette.getScoreBoxPaint());
        canvas.drawText(scoreText, boxStart + 16, boxMargin+ TEXT_SIZE, gamePalette.getScorePaint());
    }

    private void setSoccerEngine(SoccerEngine soccerEngine){
        mSoccerEngine = soccerEngine;
    }

    public void onScreenClick(){
        if(mSoccerEngine.isTutorialMode()){
            mSoccerEngine.exitTutorial();
        }else{
            mSoccerEngine.respawnBall();
        }
    }

    private class EngineLoaderTask extends AsyncTask<Rect, Void, SoccerEngine>{

        @Override
        protected SoccerEngine doInBackground(Rect... screenBounds) {
            Log.d("Test", "doInBackground: " + Thread.currentThread().getName());
            return new SoccerEngine(screenBounds[0]);
        }

        @Override
        protected void onPostExecute(SoccerEngine soccerEngine) {
            Log.d("Test", "onPostExecute: " + Thread.currentThread().getName());
            setSoccerEngine(soccerEngine);
            FaceTracker.getInstance().start();
            super.onPostExecute(soccerEngine);
        }
    }
}
