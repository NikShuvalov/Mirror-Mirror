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
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileOutputStream;


import shuvalov.nikita.mirrormirror.browsing.BrowseFragment;
import shuvalov.nikita.mirrormirror.browsing.BrowsingActivity;
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
import shuvalov.nikita.mirrormirror.gamification.GameOverlayFragment;
import shuvalov.nikita.mirrormirror.video.VideoFragment;


public class MainActivity extends AppCompatActivity implements  CameraSource.PictureCallback, CameraSource.ShutterCallback, NavigationView.OnNavigationItemSelectedListener{
    private FrameLayout mPreviewContainer;
    private Preview mPreview;
    private int mViewWidth, mViewHeight;
    public static final int CAMERA_PERMISSION_REQUEST = 9999;
    public static final int STORAGE_PERMISSION_REQUEST = 2;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;
    private Toolbar mToolbar;
    public CameraSource mCameraSource;
    private FaceDetector mFaceDetector;
    public GraphicType mCurrentOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCurrentOverlay = GraphicType.FILTER;
        //toDo: Should put a static image for animated filters to put in the recycler.
        AnimatedFilter f = new AnimatedFilter("Flames", R.drawable.flamekey0, Filter.FilterType.FACE, 1.25f, 1.5f, 0, -0.65f, AppConstants.getBitmapList(this, R.array.flame_animation_list));
        FilterManager.getInstance().addAnimatedFilters(f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViews();
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mViewWidth = size.x;
        mViewHeight = size.y;
        FaceTracker faceTracker = FaceTracker.getInstance();
        faceTracker.setScreenSize(mViewHeight, mViewWidth);
        faceTracker.changeDetectionMode(GraphicType.FILTER);

        int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (checkPermission == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            }
        } else {
            updateDetectorMode(FaceDetector.FAST_MODE);
            setUp();
        }
        notifyOverlayChanged();
    }

    /**
     * Updates the detector mode as well as handles creating a new camerasource and applying it to the preview.
     */
    private void updateDetectorMode(int faceDetectorMode){ //Don't think I should change the detection mode after the fact since it will be detrimental to performance.
        mFaceDetector = new FaceDetectorGenerator(this, faceDetectorMode).getFaceDetector();
        //Note: Width and height are reversed here because we are using portrait mode instead of landscape mode.
        mCameraSource = new CameraSourceGenerator(this, mFaceDetector, CameraSource.CAMERA_FACING_FRONT,mViewHeight,mViewWidth).getCameraSource();
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
                    setUp();
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

    public void setUp() {
        setUpNavigationDrawer();
        mPreviewContainer.addView(mPreview);
    }

    public void setUpNavigationDrawer() {
        mNavView.setNavigationItemSelectedListener(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_closed);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    public void findViews() {
        mPreviewContainer = (FrameLayout) findViewById(R.id.preview);
        mNavView = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreviewContainer.removeView(mPreview);
        if(mCameraSource!=null){
            mCameraSource.stop();
        }
        if(mFaceDetector!=null && mFaceDetector.isOperational()){
            mFaceDetector.release();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mCameraSource!=null){
            mCameraSource.release();
        }
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
        Log.d("Pic", "onPictureTaken: Bytes " + bytes.length);
        try {
            File mirrorFolder = new File(AppConstants.getImageDirectoryPath());
            if (!mirrorFolder.exists()) {
                mirrorFolder.mkdirs();
            }

            String path = AppConstants.getImageSavePath();
            File imageFile = new File(path);

            //ToDo: Allow user instead to choose whether they want to keep or discard an image.
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            Bitmap unfiltered = BitmapFactory.decodeByteArray(bytes, 0, bytes.length); //This gets the photo as seen from the camera

            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            unfiltered = Bitmap.createBitmap(unfiltered, 0, 0, unfiltered.getWidth(),
                    unfiltered.getHeight(), matrix, true);

            Bitmap bitmap = getFilteredImage(unfiltered);//Puts the filter on top of the photo
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            outputStream.flush();
            outputStream.close();
            openScreenshot(imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getFilteredImage(Bitmap cameraPreview) {
        Bitmap drawnTogether = Bitmap.createBitmap(mViewWidth, mViewHeight, cameraPreview.getConfig());
        Canvas canvas = new Canvas(drawnTogether);

        Filter filter = FilterManager.getInstance().getSelectedFilter();
        Bitmap filterBmp = null;
        FaceTracker faceTracker = FaceTracker.getInstance();
        RectF filterRect = faceTracker.getFaceRect();
        Rect previewRect = new Rect(0,0,mViewWidth,mViewHeight);

        Matrix mirrorFilter = new Matrix();
        mirrorFilter.postScale(-1, 1, mViewWidth/2, mViewHeight/2);
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

    public void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(Intent.createChooser(intent, "View with..."));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            //ToDo: Consider whether or not it's worth changing Browsing into a fragment; Camerasource is still active in background.
            //I can either stop the cameraSource and have it restart once we return to a fragment that requires it
            // OR
            // Go back to the previous architecture where browsing was an activity.

            case R.id.browse_option:
                mDrawerLayout.closeDrawers();
//                if(mCurrentOverlay == GraphicType.BROWSE){
//                    Toast.makeText(this, "Already there", Toast.LENGTH_SHORT).show();
//                }else{
//                    mCurrentOverlay = GraphicType.BROWSE;
//                    notifyOverlayChanged();
//                }
                Intent browseIntent = new Intent(this, BrowsingActivity.class);
                startActivity(browseIntent);
                break;
//            case R.id.particle_option:
//                mDrawerLayout.closeDrawers();
//                if(mCurrentOverlay==GraphicType.PARTICLE){
//                    Toast.makeText(this, "Already there", Toast.LENGTH_SHORT).show();
//                }else{
//                    mCurrentOverlay = GraphicType.PARTICLE;
//                    notifyOverlayChanged();
//                }
//                break;
            case R.id.filter_options:
                mDrawerLayout.closeDrawers();
                if (mCurrentOverlay == GraphicType.FILTER) {
                    Toast.makeText(this, "Already there", Toast.LENGTH_SHORT).show();
                } else {
                    mCurrentOverlay = GraphicType.FILTER;
                    notifyOverlayChanged();
                }
                break;
            case R.id. game_option:
                mDrawerLayout.closeDrawers();
                if(mCurrentOverlay== GraphicType.GAME){
                    Toast.makeText(this, "Already there", Toast.LENGTH_SHORT).show();
                }else{
                    mCurrentOverlay = GraphicType.GAME;
                    notifyOverlayChanged();
                }
                break;
            case R.id.component_option:
                mDrawerLayout.closeDrawers();
                if (mCurrentOverlay == GraphicType.COMPONENT) {
                    Toast.makeText(this, "Already there", Toast.LENGTH_SHORT).show();
                } else {
                    mCurrentOverlay = GraphicType.COMPONENT;
                    notifyOverlayChanged();
                }
                break;
        }
        return false;
    }

    public void notifyOverlayChanged(){
        FaceTracker.getInstance().changeDetectionMode(mCurrentOverlay);
        switch(mCurrentOverlay){
            case PARTICLE:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ParticleOverlayFragment.newInstance()).commit();
                break;
            case FILTER:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FilterOverlayFragment.newInstance()).commit();
                break;
            case GAME:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, GameOverlayFragment.newInstance()).commit();
                break;
            case COMPONENT:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ComponentOverlayFragment.newInstance()).commit();
                break;
            case BROWSE:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, BrowseFragment.newInstance()).commit();
                break;
            case VIDEO:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, VideoFragment.newInstance()).commit();
                break;
        }
    }

    public Rect getScreenBounds(){
        Rect displayRect = new Rect();
        mPreviewContainer.getHitRect(displayRect);
        return displayRect;
    }

    @Override
    public void onShutter() {}

    public enum GraphicType{
        PARTICLE, FILTER, GAME, COMPONENT, BROWSE, VIDEO
    }

    //Create a presenter class for the camera
    public CameraSource getCameraSource(){
        return mCameraSource;
    }
}