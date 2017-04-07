package shuvalov.nikita.mirrormirror;

import android.Manifest;
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
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.camerafacetracker.Preview;
import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.filters.FilterManager;
import shuvalov.nikita.mirrormirror.filters.OverlayMod;


//FixMe: Make it that the FilterImage flips the same way as the picture image is flipped. (Photo is flipped, filterImage isn't)
public class MainActivity extends AppCompatActivity implements View.OnClickListener, Camera.PictureCallback, Camera.ShutterCallback{
    private Camera mCamera;
    private OverlayMod mOverlayMod;
    private FrameLayout mFaceDetect, mPreviewContainer;
    private Preview mPreview;
    private int mCenterX, mCenterY;
    private ImageButton mCameraButton;
    public static final int CAMERA_PERMISSION_REQUEST = 9999;
    public static final int STORAGE_PERMISSION_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        findViews();


        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mCenterX = size.x/2;
        mCenterY = size.y/2;

        FaceTracker.getInstance().setScreenOffset(mCenterX, mCenterY);

        int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(checkPermission== PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            }
        }else{
            setUp();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case CAMERA_PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    setUp();
                }else{
                    Toast.makeText(this, "This App requires Camera access", Toast.LENGTH_LONG).show();
                }
                break;
            case STORAGE_PERMISSION_REQUEST:
                if(grantResults.length>0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this, "Permission required for screenshots", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void setUp(){
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
        mCameraButton.setOnClickListener(this);
    }

    public void findViews(){
        mPreviewContainer = (FrameLayout) findViewById(R.id.preview);
        mFaceDetect = (FrameLayout)findViewById(R.id.face_detect);
        mCameraButton = (ImageButton)findViewById(R.id.camera_button);
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
        switch (view.getId()){
            case R.id.camera_button:
                captureImage();
                break;
            default:
                FilterManager.getInstance().moveToNextPosition();
                mOverlayMod.notifyFilterChange();
        }
    }

    public void captureImage() {
        int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(checkPermission== PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
            }
        }else{
            Camera.Parameters param = mCamera.getParameters();
            param.setRotation(0);
            mCamera.setParameters(param);
            mCamera.takePicture(this, null, this);
        }
    }

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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
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
        Matrix mirrorFilter = new Matrix();
        mirrorFilter.postScale(-1, 1, mCenterX, mCenterY);
        mirrorFilter.mapRect(faceRect);
        filterBmp = Bitmap.createBitmap(filterBmp, 0, 0,filterBmp.getWidth(),filterBmp.getHeight(),mirrorFilter,true);


        Rect r = new Rect();
        faceRect.round(r);

        canvas.drawBitmap(cameraPreview,0,0,null);
        canvas.drawBitmap(filterBmp, null, r, null);
        return drawnTogether;
    }

    @Override
    public void onShutter() {
        //ToDo: Add some kind of UX element to notify user of capture
    }
}
