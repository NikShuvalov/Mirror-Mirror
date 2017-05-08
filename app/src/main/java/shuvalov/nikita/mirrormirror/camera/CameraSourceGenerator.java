package shuvalov.nikita.mirrormirror.camera;

import android.content.Context;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;

/**
 * Created by NikitaShuvalov on 5/8/17.
 */

public class CameraSourceGenerator {
    public CameraSource mCameraSource;

    public CameraSourceGenerator(Context context, Detector detector, int cameraFacing,int previewHeight, int previewWidth){
        mCameraSource = new CameraSource.Builder(context, detector)
                .setFacing(cameraFacing)
                .setRequestedPreviewSize(previewHeight, previewWidth)
                .setRequestedFps(60.0f)
                .setAutoFocusEnabled(true)
                .build();
    }

    public CameraSource getCameraSource() {
        return mCameraSource;
    }
}
