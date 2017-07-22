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
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.overlay.BaseOverlay;

/**
 * Created by NikitaShuvalov on 5/10/17.
 */

public class GameOverlay extends BaseOverlay{
//    private Paint mBluePaint, mScoreBoxPaint, mGoalPaint;
//    private Paint mArrowPaint, mArrowOutlinePaint;
//    private Paint mStartPaint, mIdentifierPaint;
//    private Paint mBoundaryPaint;
//    private Paint mScorePaint;
//    private Paint mRedPaint;
//    private Paint mLinePaint;
    private Path mTrianglePath;
    private static final float TEXT_SIZE = 40f;
    private static final float BOUNDARY_PAINT_WIDTH = 30f;
    private Path mGoalArrowPath, mBallArrowPath;
    private float mStartBlinkTime;
    private boolean mStartBlink;

    private SoccerEngine mSoccerEngine;

    public GameOverlay(Context context, Rect screenBounds) {
        super(context);
//        createPaints();
        mTrianglePath = new Path();
        mStartBlinkTime = 0;
        mStartBlink = true;
        Log.d("Test", "GameOverlay: "+  Thread.currentThread().getName());
        new EngineLoaderTask().execute(screenBounds);
    }

//    private void createPaints(){
//        mBoundaryPaint = new Paint();
//        mBoundaryPaint.setColor(Color.argb(255, 220,235,220));
//        mBoundaryPaint.setStrokeWidth(BOUNDARY_PAINT_WIDTH);
//        mBoundaryPaint.setStyle(Paint.Style.STROKE);
//
//        mArrowPaint =new Paint();
//        mArrowPaint.setColor(Color.argb(255, 255, 255,255));
//
//        mArrowOutlinePaint = new Paint();
//        mArrowOutlinePaint.setColor(Color.argb(255,0, 0, 0));
//        mArrowOutlinePaint.setStyle(Paint.Style.STROKE);
//        mArrowOutlinePaint.setStrokeWidth(4f);
//
//        mBluePaint= new Paint();
//        mBluePaint.setColor(Color.BLUE);
//        mBluePaint.setStyle(Paint.Style.STROKE);
//        mBluePaint.setStrokeWidth(2f);
//
//        mGoalPaint = new Paint();
//        mGoalPaint.setColor(Color.argb(255,25,25,25));
//
//        mRedPaint = new Paint();
//        mRedPaint.setColor(Color.RED);
//
//        mLinePaint = new Paint();
//        mLinePaint.setColor(Color.BLACK);
//        mLinePaint.setStyle(Paint.Style.STROKE);
//        mLinePaint.setStrokeWidth(5);
//
//        mScoreBoxPaint = new Paint();
//        mScoreBoxPaint.setColor(Color.argb(255, 255, 200, 255));
//
//
//        mScorePaint = new Paint();
//        mScorePaint.setColor(Color.RED);
//        mScorePaint.setTextSize(TEXT_SIZE);
//
//        mStartPaint = new Paint();
//        mStartPaint.setColor(Color.YELLOW);
//        mStartPaint.setTextSize(TEXT_SIZE * 1.5f);
//
//        mIdentifierPaint = new Paint();
//        mIdentifierPaint.setColor(Color.WHITE);
//        mIdentifierPaint.setTextSize(TEXT_SIZE);
//    }

    private void createTutorialArrows(){
        Rect goalBounds = mSoccerEngine.getGoalBounds();
        float goalRadius = goalBounds.width()/2f;
        PointF goalArrowOrigin = new PointF(goalBounds.centerX() + goalRadius*2, goalBounds.centerY());
        mGoalArrowPath = new Path();
        mGoalArrowPath.moveTo(goalArrowOrigin.x, goalArrowOrigin.y);
        mGoalArrowPath.rLineTo(goalRadius, goalRadius);
        mGoalArrowPath.rLineTo(0 ,-goalRadius/2);
        mGoalArrowPath.arcTo(goalArrowOrigin.x, goalBounds.centerY() + goalBounds.width()/4, goalArrowOrigin.x + goalRadius*2, goalBounds.centerY() + goalBounds.width()/4 + goalRadius*2,270, 90, false);
        mGoalArrowPath.rLineTo(0, goalRadius);
        mGoalArrowPath.rLineTo(goalRadius, 0);
        mGoalArrowPath.rLineTo(0,-goalRadius);
        mGoalArrowPath.arcTo(goalArrowOrigin.x - goalRadius, goalBounds.centerY() - goalRadius/2, goalArrowOrigin.x + goalRadius*3, goalBounds.centerY() + goalBounds.width()/4 +goalRadius*3, 0,-90,false);
        mGoalArrowPath.rLineTo(0, -goalRadius/2);
        mGoalArrowPath.close();

        Ball soccerBall = mSoccerEngine.getSoccerBall();
        PointF ballCenter = new PointF((float)soccerBall.getCenterX(), (float)soccerBall.getCenterY());
        float ballRadius = (float) soccerBall.getRadius();

        mBallArrowPath = new Path();
        mBallArrowPath.moveTo(ballCenter.x - ballRadius, ballCenter.y - ballRadius);
        mBallArrowPath.rLineTo(0,  - ballRadius * 2);
        mBallArrowPath.rLineTo( -ballRadius/1.5f, ballRadius/1.5f);
        mBallArrowPath.rLineTo(-ballRadius * 3 , - ballRadius * 3);
        mBallArrowPath.rLineTo(- ballRadius/1.5f,  ballRadius/1.5f);
        mBallArrowPath.rLineTo(ballRadius  * 3, ballRadius * 3);
        mBallArrowPath.rLineTo(-ballRadius /1.5f, ballRadius /1.5f);
        mBallArrowPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(canvas!=null && mSoccerEngine!=null) {
            GamePalette gamePalette = GamePalette.getInstance();
            if(mSoccerEngine.getTutorialCanvas()== null){
                mSoccerEngine.createTutorialCanvas();
            }
            if (mSoccerEngine.isTutorialMode()) {
                canvas = mSoccerEngine.getTutorialCanvas();
//                canvas.drawColor(Color.argb(100, 0,0,0));
//
//                canvas.drawPath(mGoalArrowPath, gamePalette.getArrowPaint());
//                canvas.drawPath(mGoalArrowPath, gamePalette.getArrowOutlinePaint());
//
//                canvas.drawPath(mBallArrowPath, gamePalette.getArrowPaint());
//                canvas.drawPath(mBallArrowPath, gamePalette.getArrowOutlinePaint());
//
//                canvas.drawText("The Goal", goalBounds.centerX () + (int)(goalBounds.width()*1.75), goalBounds.centerY() + (int)(goalBounds.width()*1.25) + TEXT_SIZE, gamePalette.getIdentifierPaint());
//                canvas.drawText("The Ball", soccerBallCenterX - (5 * soccerBallRadius) - TEXT_SIZE, soccerBallCenterY - (5 * soccerBallRadius) - TEXT_SIZE, gamePalette.getIdentifierPaint());
//                drawFaceArrow(canvas,soccerBallRadius);
//
//                canvas.drawText("You can't pass this line", canvas.getWidth()/3, canvas.getHeight()/2, gamePalette.getIdentifierPaint());
                if(mStartBlinkTime< 50 && mStartBlink){
                    canvas.drawText("Tap to Start", canvas.getWidth()/3f, canvas.getHeight()*.7f, gamePalette.getStartPaint());
                    mStartBlinkTime ++;
                }else if (mStartBlinkTime >= 50){
                    mStartBlink = !mStartBlink;
                    mStartBlinkTime = 0;
                }else{
                    mStartBlinkTime ++;
                }
            } else {
                Rect goalBounds = mSoccerEngine.getGoalBounds();
                Ball soccerBall = mSoccerEngine.getSoccerBall();
                float soccerBallRadius = (float) soccerBall.getRadius();
                double soccerBallBottom = soccerBall.getCenterY() + soccerBallRadius;
                float soccerBallCenterX = (float) soccerBall.getCenterX();
                float soccerBallCenterY = (float)soccerBall.getCenterY();
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                drawBoundaryLine(canvas);
                mSoccerEngine.updateFacePosition(FaceTracker.getInstance().getFaceRect());
                drawFaceOutline(canvas);
                canvas.drawCircle(goalBounds.centerX(), goalBounds.centerY(), goalBounds.width() / 2, gamePalette.getGoalPaint());//Draws goal, aka black hole of death.
                drawScoreBox(canvas);
                canvas.drawCircle(soccerBallCenterX, soccerBallCenterY, soccerBallRadius, soccerBall.getPaint());
                if (soccerBallBottom <= 0) {
                    drawIndicator(canvas, soccerBallRadius, soccerBallBottom, soccerBallCenterX);
                }
                mSoccerEngine.process();
            }
        }else{
            //ToDo: Loading symbol here.
        }
    }

    private void drawFaceArrow(Canvas canvas, float ballRadius){
        RectF face = mSoccerEngine.getFaceRect();
        Path faceArrow = new Path();
        if(face!=null) {
            GamePalette gamePalette = GamePalette.getInstance();
            faceArrow.moveTo(face.centerX(), canvas.getHeight() / 2 - (ballRadius * 2));
            faceArrow.rLineTo(ballRadius, -ballRadius);
            faceArrow.rLineTo(-ballRadius / 2, 0);
            faceArrow.rLineTo(0, -ballRadius * 2);
            faceArrow.rLineTo(-ballRadius, 0);
            faceArrow.rLineTo(0, ballRadius * 2);
            faceArrow.rLineTo(-ballRadius / 2, 0);
            faceArrow.close();

            canvas.drawPath(faceArrow, gamePalette.getArrowPaint());
            canvas.drawPath(faceArrow, gamePalette.getArrowOutlinePaint());
            canvas.drawText("Your Face", (face.centerX() + face.left)/2f, canvas.getHeight()/2 - (ballRadius *6) + TEXT_SIZE, gamePalette.getIdentifierPaint());
        }
    }

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
        createTutorialArrows();
    }

    public void onScreenClick(){
        if(mSoccerEngine.isTutorialMode()){ //Should the soccer engine handle Tutorial mode at all?
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
