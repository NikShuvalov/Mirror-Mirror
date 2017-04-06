package shuvalov.nikita.mirrormirror;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.security.Permission;
import java.util.Date;
import java.util.jar.Manifest;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, Camera.PictureCallback{
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
        mPreviewContainer.setOnLongClickListener(this);
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

    @Override
    public boolean onLongClick(View view) {
        int checkPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(checkPermission== PackageManager.PERMISSION_DENIED){
            Toast.makeText(this, "Permission required for screenshots", Toast.LENGTH_SHORT).show();
        }else{
            Camera.Parameters param = mCamera.getParameters();
            param.setRotation(0);
            mCamera.setParameters(param);
            mCamera.takePicture(null, this, this);
//            takeScreenShot();
        }
        return true;
    }

//    public void takeScreenShot(){
//        Date now = new Date();
//        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
//
//        try {
//            // image naming and path  to include sd card  appending name you choose for file
//            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
//
//            // create bitmap screen capture
//            View v1 = getWindow().getDecorView().getRootView();
//            v1.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
//            v1.setDrawingCacheEnabled(false);
//
//            File imageFile = new File(mPath);
//
//            FileOutputStream outputStream = new FileOutputStream(imageFile);
//            int quality = 100;
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
//            outputStream.flush();
//            outputStream.close();
//
//            openScreenshot(imageFile);
//        } catch (Throwable e) {
//            // Several error may come out with file handling or OOM
//            e.printStackTrace();
//        }
//    }

    public void openScreenshot(File imageFile){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            File mirrorFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+"/MirrorScreens/");
            if(!mirrorFolder.exists()){
                mirrorFolder.mkdirs();
            }

            String mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/" +"MirrorScreens/"+ now + ".jpg";
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            Bitmap unfiltered = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            unfiltered = Bitmap.createBitmap(unfiltered, 0, 0, unfiltered.getWidth(),
                    unfiltered.getHeight(), matrix, true);
            Bitmap bitmap = getFilteredImage(unfiltered);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            openScreenshot(imageFile);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public  Bitmap getFilteredImage(Bitmap cameraPreview){
        Bitmap drawnTogether = Bitmap.createBitmap(cameraPreview.getWidth(), cameraPreview.getHeight(), cameraPreview.getConfig());
        Canvas canvas = new Canvas(drawnTogether);
        Filter filter = FilterManager.getInstance().getSelectedFilter();
        Bitmap filterBmp = BitmapFactory.decodeResource(getResources(), filter.getResourceInt());

        RectF faceRect = FaceTracker.getInstance().getFaceRect();
        Rect r = new Rect();
        faceRect.round(r);
        canvas.drawBitmap(cameraPreview,0,0,null);
        canvas.drawBitmap(filterBmp, null, r, null);
        return drawnTogether;
    }

}
