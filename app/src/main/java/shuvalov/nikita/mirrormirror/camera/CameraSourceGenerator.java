package shuvalov.nikita.mirrormirror.camera;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;

/**
 * Created by NikitaShuvalov on 5/8/17.
 */

public class CameraSourceGenerator {

    public static CameraSource createCameraSource(Context context, Detector detector, int cameraFacing, int previewHeight, int previewWidth){
        //ToDo: Consider scaling down previewHeight/Width to improve performance
        //ToDo: Consider allowing user to use back-facing camera
        return new CameraSource.Builder(context, detector)
                .setFacing(cameraFacing)
                .setRequestedPreviewSize(previewHeight, previewWidth)
                .setRequestedFps(30.0f)
                .setAutoFocusEnabled(true)
                .build();
    }

}
