package shuvalov.nikita.mirrormirror.gamification;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.FaceDetector;

import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.camera.CameraSourceGenerator;
import shuvalov.nikita.mirrormirror.camera.FaceDetectorGenerator;
import shuvalov.nikita.mirrormirror.camerafacetracker.Preview;

import static shuvalov.nikita.mirrormirror.MainActivity.CAMERA_PERMISSION_REQUEST;
import static shuvalov.nikita.mirrormirror.MainActivity.STORAGE_PERMISSION_REQUEST;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{
    private FrameLayout mOverlayContainer, mPreviewContainer;
    private GameOverlay mGameOverlay;
    private FaceDetector mFaceDetector;
    private CameraSource mCameraSource;
    private Preview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        findViews();
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        generatePreview();
        setUpOverlay(new Rect(0, 0, size.x, size.y));
    }

    private void generatePreview(){
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        mFaceDetector = FaceDetectorGenerator.createFaceDetector(this, FaceDetector.ACCURATE_MODE);
        mCameraSource = CameraSourceGenerator.createCameraSource(this, mFaceDetector,CameraSource.CAMERA_FACING_FRONT, size.y, size.x);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (checkPermission == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            }
        } else {
            setUpPreview();
        }
    }

    private void setUpOverlay(Rect screenRect){
        mGameOverlay = new GameOverlay(this, screenRect);
        mGameOverlay.setZOrderMediaOverlay(true);
        mOverlayContainer.addView(mGameOverlay);
        mGameOverlay.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpPreview();
                } else {
                    Toast.makeText(this, "This App requires Camera access", Toast.LENGTH_LONG).show();
                }
                break;
            case STORAGE_PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission required for screenshots", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void setUpPreview() {
        if(mPreview == null){
            mPreview = new Preview(this);
        }
        mPreview.setCameraSource(mCameraSource);
        if(mPreviewContainer.getChildCount()==0) {
            mPreviewContainer.addView(mPreview);
        }
    }

    public void findViews() {
        mOverlayContainer = (FrameLayout) findViewById(R.id.overlay_container);
        mPreviewContainer = (FrameLayout)findViewById(R.id.preview_container);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameOverlay.stopGraphicThread();
        if (mCameraSource != null) {
            mCameraSource.stop();
            mCameraSource.release();
        }
        if (mFaceDetector != null && mFaceDetector.isOperational()) {
            mFaceDetector.release();
        }
        if(mGameOverlay!=null) {
            mGameOverlay.getHolder().removeCallback(mGameOverlay);
        }
    }

    @Override
    public void onClick(View view) {
        mGameOverlay.onScreenClick();
    }
}