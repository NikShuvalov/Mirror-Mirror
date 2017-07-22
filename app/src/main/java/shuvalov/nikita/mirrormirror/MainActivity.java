package shuvalov.nikita.mirrormirror;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import shuvalov.nikita.mirrormirror.browsing.BrowseFragment;
import shuvalov.nikita.mirrormirror.camera.CameraSourceGenerator;
import shuvalov.nikita.mirrormirror.camera.FaceDetectorGenerator;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.camerafacetracker.Preview;
import shuvalov.nikita.mirrormirror.componentfilters.ComponentOverlayFragment;
import shuvalov.nikita.mirrormirror.filters.AnimatedFilter;
import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.filters.FilterManager;
import shuvalov.nikita.mirrormirror.filters.FilterOverlayFragment;
import shuvalov.nikita.mirrormirror.filters.particles.ParticleOverlayFragment;


public class MainActivity extends AppCompatActivity implements  CameraSource.PictureCallback, CameraSource.ShutterCallback{
    private FrameLayout mPreviewContainer;
    private Preview mPreview;
    public static final int CAMERA_PERMISSION_REQUEST = 9999;
    public static final int STORAGE_PERMISSION_REQUEST = 2;
    public CameraSource mCameraSource;
    private FaceDetector mFaceDetector;
    public GraphicType mCurrentOverlay;

    public static final String MAIN_FRAGMENT = "Main Fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCurrentOverlay = GraphicType.FILTER;
        findViews();

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        FaceTracker faceTracker = FaceTracker.getInstance();
        faceTracker.setScreenSize(size.y, size.x);
        faceTracker.changeDetectionMode(GraphicType.FILTER);



        //FixMe: Not sure why filterManager is referenced at all here. MainActivity doesn't use it at all, besides for adding a filter; It's because gettingBitMapList requires context.
        AnimatedFilter f = new AnimatedFilter("Flames", R.drawable.flamekey0, Filter.FilterType.FACE, 1.25f, 1.5f, 0, -0.65f, AppConstants.getBitmapList(this, R.array.flame_animation_list));
        FilterManager.getInstance().addAnimatedFilters(f);
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
            prepCameraSource();
            displayPreview();
        }
        notifyOverlayChanged();
    }

    /**
     * Updates the detector mode as well as handles creating a new camerasource and applying it to the preview.
     */
    private void prepCameraSource(){ //Don't think I should change the detection mode after the fact since it will be detrimental to performance.
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        mFaceDetector = FaceDetectorGenerator.createFaceDetector(this, FaceDetector.ACCURATE_MODE);
        //Note: Width and height are reversed here because we are using portrait mode instead of landscape mode.
        mCameraSource = CameraSourceGenerator.createCameraSource(this, mFaceDetector, CameraSource.CAMERA_FACING_FRONT,size.y,size.x);
        if(mPreview == null) {
            mPreview = new Preview(this);
        }
        mPreview.setCameraSource(mCameraSource);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayPreview();
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

    public void displayPreview() {
        if(mPreviewContainer.getChildCount()==0) {
            mPreviewContainer.addView(mPreview);
        }
    }

    public void findViews() {
        mPreviewContainer = (FrameLayout) findViewById(R.id.preview);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FilterManager.getInstance().clearSelectionIndex();
        FaceTracker.getInstance().pause();
        if(mCameraSource!=null){
            mCameraSource.stop();
            mCameraSource.release();
        }
        if(mFaceDetector!=null && mFaceDetector.isOperational()){
            mFaceDetector.release();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPreviewContainer.removeView(mPreview);
    }

    public void captureImage() {
        int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (checkPermission == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
            }
        } else {
            mCameraSource.takePicture(this, this);
        }
    }

    @Override
    public void onPictureTaken(byte[] bytes) {
        try {
            File mirrorFolder = new File(AppConstants.getImageDirectoryPath());
            if (!mirrorFolder.exists()) {
                mirrorFolder.mkdirs();
            }

            Bitmap unfiltered = BitmapFactory.decodeByteArray(bytes, 0, bytes.length); //This gets the photo as seen from the camera
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            unfiltered = Bitmap.createBitmap(unfiltered, 0, 0, unfiltered.getWidth(),
                    unfiltered.getHeight(), matrix, true);
            Bitmap bitmap = getFilteredImage(unfiltered);//Puts the filter on top of the photo
            verifyWithUser(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getFilteredImage(Bitmap cameraPreview) {
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);
        Bitmap drawnTogether = Bitmap.createBitmap(p.x, p.y, cameraPreview.getConfig());
        Canvas canvas = new Canvas(drawnTogether);

        Filter filter = FilterManager.getInstance().getSelectedFilter();
        Bitmap filterBmp = null;
        FaceTracker faceTracker = FaceTracker.getInstance();
        RectF filterRect = faceTracker.getFaceRect();
        Rect previewRect = new Rect(0,0,p.x,p.y);

        Matrix mirrorFilter = new Matrix();
        mirrorFilter.postScale(-1, 1, p.x/2, p.y/2);
        mirrorFilter.mapRect(filterRect);
        if(filter!=null){
            float scaleX = drawnTogether.getWidth()/faceTracker.getScreenWidth();
            float scaleY = drawnTogether.getHeight()/faceTracker.getScreenHeight();
            filterBmp = BitmapFactory.decodeResource(getResources(), filter.getResourceInt()); //Create a bitmap using the Filter object's info
            filterBmp = Bitmap.createBitmap(filterBmp, 0, 0, filterBmp.getWidth()*(int)scaleX, filterBmp.getHeight()*(int)scaleY, mirrorFilter, true); //Apply scaling and the matrix filter... again?
        }
        canvas.drawBitmap(cameraPreview, null, previewRect, null);
        if(filterBmp!=null ) {
            canvas.drawBitmap(filterBmp, null, filterRect, null);
        }
        return drawnTogether;
    }

    public void notifyOverlayChanged(){
        FaceTracker.getInstance().changeDetectionMode(mCurrentOverlay);
        switch(mCurrentOverlay){
            case PARTICLE:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ParticleOverlayFragment.newInstance()).commit();
                break;
            case FILTER:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FilterOverlayFragment.newInstance(), MAIN_FRAGMENT).commit();
                break;
            case COMPONENT:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ComponentOverlayFragment.newInstance()).commit();
                break;
            case BROWSE:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, BrowseFragment.newInstance()).commit();
                break;
        }
    }

    public void changeOverlay(GraphicType graphicType){
        mCurrentOverlay = graphicType;
    }

    @Override
    public void onShutter() {}

    public enum GraphicType{
        PARTICLE, FILTER, COMPONENT, BROWSE
    }

    public CameraSource getCameraSource(){
        return mCameraSource;
    }

    @Override
    public void onBackPressed() {
        if(mCurrentOverlay == GraphicType.FILTER) {
            FilterOverlayFragment frag = (FilterOverlayFragment)getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT);
            if(frag.isVisible()){
                if(!frag.onBackPressed()){
                    super.onBackPressed();
                }
            }else {
                super.onBackPressed();
            }
        }else{
            mCurrentOverlay = GraphicType.FILTER;
            notifyOverlayChanged();
        }
    }

    private void verifyWithUser(final Bitmap bitmap){
        final AlertDialog alertDialog = new AlertDialog.Builder(this).setView(R.layout.picture_confirmation).create();
        alertDialog.show();
        ImageButton confirm = (ImageButton)alertDialog.findViewById(R.id.confirm_picture);
        ImageButton reject = (ImageButton)alertDialog.findViewById(R.id.reject_picture);
        ImageView picPreview = (ImageView)alertDialog.findViewById(R.id.picture_view);
        picPreview.setImageBitmap(bitmap);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String path = AppConstants.getImageSavePath();
                    File imageFile = new File(path);
                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    Toast.makeText(MainActivity.this, "Picture Saved", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          Toast.makeText(MainActivity.this, "Picture discarded", Toast.LENGTH_SHORT).show();
                                          alertDialog.dismiss();
                                      }
                                  });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}