package shuvalov.nikita.mirrormirror;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;


public class MainActivity extends AppCompatActivity {
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout previewContainer = (FrameLayout) findViewById(R.id.preview);

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
        int cameraId = getIdForRequestedCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        Log.d("b", "onCreate: "+cameraId);
        mCamera = Camera.open(cameraId);
        mCamera.setDisplayOrientation(90);
        OverlayMod overlayMod = new OverlayMod(this);
        Preview preview = new Preview(this, mCamera, overlayMod);

        preview.prepareForDisplay(mCamera);

        previewContainer.addView(preview);
        previewContainer.addView(overlayMod);
        previewContainer.bringChildToFront(overlayMod);


//        Intent cameraIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
//        intent.putExtra("android.intent.extras.USE_FRONT_CAMERA",true);
//        intent.putExtra("android.intent.extras.LENS_FACING_FRONT",1);
//        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING",cameraId);
//        startActivityForResult(cameraIntent, 5);
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
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
        mCamera.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.release();
        }
    }
}
