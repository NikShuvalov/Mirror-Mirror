package shuvalov.nikita.mirrormirror.game_mode;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by NikitaShuvalov on 7/21/17.
 */

public class GamePalette {
    private static GamePalette sGamePalette;

    private Paint mBluePaint, mScoreBoxPaint, mGoalPaint;
    private Paint mArrowPaint, mArrowOutlinePaint;
    private Paint mStartPaint, mIdentifierPaint;
    private Paint mBoundaryPaint;
    private Paint mScorePaint;
    private Paint mRedPaint;
    private Paint mLinePaint;

    private static final float BOUNDARY_PAINT_WIDTH = 30f;
    private static final float TEXT_SIZE = 40f;

    private GamePalette() {
        createPaints();
    }

    public static GamePalette getInstance() {
        if(sGamePalette == null){
            sGamePalette = new GamePalette();
        }
        return sGamePalette;
    }

    private void createPaints(){
        mBoundaryPaint = new Paint();
        mBoundaryPaint.setColor(Color.argb(255, 220,235,220));
        mBoundaryPaint.setStrokeWidth(BOUNDARY_PAINT_WIDTH);
        mBoundaryPaint.setStyle(Paint.Style.STROKE);

        mArrowPaint =new Paint();
        mArrowPaint.setColor(Color.argb(255, 255, 255,255));

        mArrowOutlinePaint = new Paint();
        mArrowOutlinePaint.setColor(Color.argb(255,0, 0, 0));
        mArrowOutlinePaint.setStyle(Paint.Style.STROKE);
        mArrowOutlinePaint.setStrokeWidth(4f);

        mBluePaint= new Paint();
        mBluePaint.setColor(Color.BLUE);
        mBluePaint.setStyle(Paint.Style.STROKE);
        mBluePaint.setStrokeWidth(2f);

        mGoalPaint = new Paint();
        mGoalPaint.setColor(Color.argb(255,25,25,25));

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

        mStartPaint = new Paint();
        mStartPaint.setColor(Color.YELLOW);
        mStartPaint.setTextSize(TEXT_SIZE * 1.5f);

        mIdentifierPaint = new Paint();
        mIdentifierPaint.setColor(Color.WHITE);
        mIdentifierPaint.setTextSize(TEXT_SIZE);
    }

    public Paint getBluePaint() {
        return mBluePaint;
    }

    public Paint getScoreBoxPaint() {
        return mScoreBoxPaint;
    }

    public Paint getGoalPaint() {
        return mGoalPaint;
    }

    public Paint getArrowPaint() {
        return mArrowPaint;
    }

    public Paint getArrowOutlinePaint() {
        return mArrowOutlinePaint;
    }

    public Paint getStartPaint() {
        return mStartPaint;
    }

    public Paint getIdentifierPaint() {
        return mIdentifierPaint;
    }

    public Paint getBoundaryPaint() {
        return mBoundaryPaint;
    }

    public Paint getScorePaint() {
        return mScorePaint;
    }

    public Paint getRedPaint() {
        return mRedPaint;
    }

    public Paint getLinePaint() {
        return mLinePaint;
    }
}
