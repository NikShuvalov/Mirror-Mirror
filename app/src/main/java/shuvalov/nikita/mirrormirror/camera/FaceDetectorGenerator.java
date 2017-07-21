package shuvalov.nikita.mirrormirror.camera;

import android.content.Context;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;

/**
 * Created by NikitaShuvalov on 5/8/17.
 */

public class FaceDetectorGenerator {

    public static FaceDetector createFaceDetector(Context context, int faceDetectorMode){
        FaceDetector faceDetector = new FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .setMode(faceDetectorMode)
                .setProminentFaceOnly(true)
                .setMinFaceSize(0.35f)
                .build();

        Detector.Processor<Face> processor;
        Tracker<Face> tracker = FaceTracker.getInstance();
        processor = new LargestFaceFocusingProcessor.Builder(faceDetector,tracker).build();
        faceDetector.setProcessor(processor);
        return faceDetector;
    }

}
