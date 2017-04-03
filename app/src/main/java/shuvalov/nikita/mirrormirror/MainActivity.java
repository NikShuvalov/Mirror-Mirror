package shuvalov.nikita.mirrormirror;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Camera mCamera;
    private OverlayMod mOverlayMod;
    private FrameLayout mFaceDetect, mPreviewContainer;
    private Preview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//
//        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//        speechRecognizer.setRecognitionListener(new RecognitionListener() {
//            String text;
//            @Override
//            public void onReadyForSpeech(Bundle bundle) {
//
//            }
//
//            @Override
//            public void onBeginningOfSpeech() {
//
//            }
//
//            @Override
//            public void onRmsChanged(float v) {
//
//            }
//
//            @Override
//            public void onBufferReceived(byte[] bytes) {
//
//            }
//
//            @Override
//            public void onEndOfSpeech() {
//                mTextView.setText("Finished: "+ text);
//            }
//
//            @Override
//            public void onError(int i) {
//
//            }
//
//            @Override
//            public void onResults(Bundle bundle) {
//                ArrayList<String> recognizedSpeech = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                text = "";
//                if(recognizedSpeech!=null && !recognizedSpeech.isEmpty()){
//                    for(String word: recognizedSpeech){
//                        text+=word+" ";
//                    }
//                    mTextView.setText(text);
//                }
//            }
//
//            @Override
//            public void onPartialResults(Bundle bundle) {
//                ArrayList<String> recognizedSpeech = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                String text = "";
//                if(recognizedSpeech!=null && !recognizedSpeech.isEmpty()){
//                    for(String word: recognizedSpeech){
//                        text+=word+" ";
//                    }
//                    mTextView.setText(text);
//                }
//            }
//
//            @Override
//            public void onEvent(int i, Bundle bundle) {
//
//
//            }
//        });
////        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
////        speechRecognizer.startListening(intent);

        //TODo: THis code will be better off in onResume

    }

    @Override
    protected void onResume() {
        super.onResume();

        mPreviewContainer = (FrameLayout) findViewById(R.id.preview);
        mFaceDetect = (FrameLayout)findViewById(R.id.face_detect);

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int x = size.x;
        int y = size.y;


        FaceTracker.getInstance().setScreenOffset(x/2, y/2);

        int cameraId = getIdForRequestedCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        Log.d("b", "onCreate: "+cameraId);
        mCamera = Camera.open(cameraId);
        mCamera.setDisplayOrientation(90);
        mOverlayMod = new OverlayMod(this);
        mOverlayMod.setZOrderOnTop(true);
        mPreview = new Preview(this, mCamera);

        mPreview.prepareForDisplay(mCamera);

        mPreviewContainer.addView(mPreview);
        mFaceDetect.addView(mOverlayMod);
        mPreviewContainer.setOnClickListener(this);
    }

    private static int getIdForRequestedCamera(int facing) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFaceDetect.removeAllViews();
        mPreviewContainer.removeView(mPreview);
        mCamera.stopPreview();
        mCamera.release();
        mCamera= null;
    }

    @Override
    public void onClick(View view) {
        FilterManager.getInstance().moveToNextPosition();
        mOverlayMod.notifyFilterChange();
    }
}
