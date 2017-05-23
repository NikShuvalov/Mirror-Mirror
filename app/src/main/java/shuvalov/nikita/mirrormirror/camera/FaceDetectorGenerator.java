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
    public FaceDetector mFaceDetector;

    public FaceDetectorGenerator(Context context){
        mFaceDetector = new FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .setMode(FaceDetector.ACCURATE_MODE)
                .setProminentFaceOnly(true)
                .setMinFaceSize(0.35f)
                .build();

        Detector.Processor<Face> processor;
        Tracker<Face> tracker = FaceTracker.getInstance();
        processor = new LargestFaceFocusingProcessor.Builder(mFaceDetector,tracker).build();
        mFaceDetector.setProcessor(processor);
    }

    public FaceDetector getFaceDetector() {
        return mFaceDetector;
    }

}
