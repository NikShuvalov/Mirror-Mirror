package shuvalov.nikita.mirrormirror;

import android.os.Environment;

import java.util.Date;

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
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/" +"MirrorScreens/"+ now + ".jpg";
    }

    /**
     *
     * @return The path to the folder that stores all the images.
     */
    public static String getImageDirectoryPath(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+"/MirrorScreens/";
    }
}
