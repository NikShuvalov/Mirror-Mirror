package shuvalov.nikita.mirrormirror;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

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
}
