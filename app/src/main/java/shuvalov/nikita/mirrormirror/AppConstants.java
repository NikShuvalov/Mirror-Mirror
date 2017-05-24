package shuvalov.nikita.mirrormirror;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by NikitaShuvalov on 4/7/17.
 */

public class AppConstants {

    /**
     *
     * @return The path to save the image based on current time.
     */
    public static String getImageSavePath(){
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        return getImageDirectoryPath()+ now + ".jpg";
    }

    /**
     *
     * @return The path to the folder that stores all the images.
     */
    public static String getImageDirectoryPath(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+"/MirrorScreens/";
    }

    public static List<Bitmap> getBitmapList(Context context, int resourceArray){
        List<Bitmap> bitmapList = new ArrayList<>();
        TypedArray tarray = context.getResources().obtainTypedArray(resourceArray);
        for(int i = 0; i<tarray.length();i++){
            bitmapList.add(BitmapFactory.decodeResource(context.getResources(),tarray.getResourceId(i,-1)));
        }
        tarray.recycle();
        return bitmapList;
    }


    /**
     * Use this to find the x,y coords of the end of an arc on an oval. 0 is located at 1,0 (right-middle) of oval going clockwise.
     *
     * @param oval
     * @param endAngle angle at which the coords are located
     * @return x,y coords in PointF
     */
    public static PointF getEndPointOfOvalArc(RectF oval, float endAngle){
        return null;
    }


    /**
     * Use this to find the x,y coords of the end of an arc on a circle. 0 is located at 1,0 (right-middle) of circle going clockwise.
     * @param circle
     * @param endAngle angle at which the coords are located
     * @return x,y coords in PointF
     */
    public static PointF getEndPointOfCircleArc(RectF circle, float endAngle){
        float radius = circle.width()/2;
        double radians = Math.toRadians(endAngle);
        float width = (float)Math.cos(radians) * radius;
        float height = (float)Math.sin(radians) * radius;
        if(Math.abs(height)<1e-10){
            height = 0;
        }
        if(Math.abs(width)<1e-10){
            width = 0;
        }
        return new PointF(width, height);
    }

    public static PointF getEndPointOfCircleArc(float cx, float cy, float radius, float endAngle){
        float radians = (float)Math.toRadians(endAngle);
        float width = (float)Math.cos(radians) * radius;
        float height = (float)Math.sin(radians) * radius;
        if(Math.abs(height)<1e-10){
            height = 0;
        }
        if(Math.abs(width)<1e-10){
            width = 0;
        }
        return new PointF(cx + width, cy + height);
    }
}
