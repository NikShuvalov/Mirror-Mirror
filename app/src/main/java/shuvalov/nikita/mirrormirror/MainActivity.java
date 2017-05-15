package shuvalov.nikita.mirrormirror;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
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
import android.support.v4.hardware.display.DisplayManagerCompat;
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
import java.util.ArrayList;
import java.util.List;

import shuvalov.nikita.mirrormirror.browsing.BrowsingActivity;
import shuvalov.nikita.mirrormirror.camera.CameraSourceGenerator;
import shuvalov.nikita.mirrormirror.camera.FaceDetectorGenerator;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.camerafacetracker.Preview;
import shuvalov.nikita.mirrormirror.filters.AnimatedFilter;
import shuvalov.nikita.mirrormirror.filters.Filter;
import shuvalov.nikita.mirrormirror.filters.FilterManager;
import shuvalov.nikita.mirrormirror.filters.FilterOverlayFragment;
import shuvalov.nikita.mirrormirror.filters.particles.ParticleOverlayFragment;
import shuvalov.nikita.mirrormirror.gamification.GameOverlayFragment;


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
        AnimatedFilter f = new AnimatedFilter("Flames", R.drawable.flamekey0, Filter.ImagePosition.FACE, 1.25f, 1.5f, 0, -0.65f,getBitmapList(R.array.flame_animation_list));
        FilterManager.getInstance().addAnimatedFilters(f);
    }

    private List<Bitmap> getBitmapList(int resourceArray){
        List<Bitmap> bitmapList = new ArrayList<>();
        TypedArray tarray = getResources().obtainTypedArray(resourceArray);
        for(int i = 0; i<tarray.length();i++){
            bitmapList.add(BitmapFactory.decodeResource(getResources(),tarray.getResourceId(i,-1)));
        }
        tarray.recycle();
        return bitmapList;
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
            mFaceDetector = new FaceDetectorGenerator(this).getFaceDetector();

            //Note: Width and height are reversed here because we are using portrait mode instead of landscape mode.
            mCameraSource = new CameraSourceGenerator(this, mFaceDetector, CameraSource.CAMERA_FACING_FRONT,mViewHeight,mViewWidth).getCameraSource();
            setUp();
        }
        notifyOverlayChanged();
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
        mPreview = new Preview(this);
        mPreview.setCameraSource(mCameraSource);
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
        try {
            File mirrorFolder = new File(AppConstants.getImageDirectoryPath());
            if (!mirrorFolder.exists()) {
                mirrorFolder.mkdirs();
            }

            String path = AppConstants.getImageSavePath();
            File imageFile = new File(path);

            //ToDo: Allow user instead to choose whether they want to keep or discard an image.
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getFilteredImage(Bitmap cameraPreview) {
        Bitmap drawnTogether = Bitmap.createBitmap(mViewWidth, mViewHeight, cameraPreview.getConfig());
        Canvas canvas = new Canvas(drawnTogether);
        Filter filter = FilterManager.getInstance().getSelectedFilter();
        Bitmap filterBmp = BitmapFactory.decodeResource(getResources(), filter.getResourceInt());

        FaceTracker faceTracker = FaceTracker.getInstance();
        RectF faceRect = faceTracker.getFaceRect(); //ToDo: Lock faceRect so that it isn't pulled until after it's done being resized/moved?

        Matrix mirrorFilter = new Matrix();
        float scaleX = drawnTogether.getWidth()/faceTracker.getScreenWidth();
        float scaleY = drawnTogether.getHeight()/faceTracker.getScreenHeight();
        mirrorFilter.postScale(-1, 1, mViewWidth/2, mViewHeight/2);
        mirrorFilter.mapRect(faceRect);
        filterBmp = Bitmap.createBitmap(filterBmp, 0, 0, filterBmp.getWidth()*(int)scaleX, filterBmp.getHeight()*(int)scaleY, mirrorFilter, true); //ToDo: If it turns I need matrix, don't forget to add in the matrix parameters here.

        Rect fr = new Rect();
        faceRect.round(fr);

        Rect previewRect = new Rect(0,0,mViewWidth,mViewHeight);

        canvas.drawBitmap(cameraPreview, null, previewRect, null);
        canvas.drawBitmap(filterBmp, null, fr, null);
        return drawnTogether;
    }

    public void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);        //FixMe: APK 24+ doesn't allow this to work
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.browse_option:
                //ToDo: Fix this up if necessary cause things might get weird with the fragments being added
                mDrawerLayout.closeDrawers();
                Intent browseIntent = new Intent(this, BrowsingActivity.class);
                startActivity(browseIntent);
                break;
            case R.id.particle_option:
                mDrawerLayout.closeDrawers();
                if(mCurrentOverlay==GraphicType.PARTICLE){
                    Toast.makeText(this, "Already there", Toast.LENGTH_SHORT).show();
                }else{
                    mCurrentOverlay = GraphicType.PARTICLE;
                    notifyOverlayChanged();
                }
                break;
            case R.id.filter_options:
                mDrawerLayout.closeDrawers();
                if(mCurrentOverlay == GraphicType.FILTER){
                    Toast.makeText(this, "Already there", Toast.LENGTH_SHORT).show();
                }else{
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
        }
    }

    public Rect getScreenBounds(){
//        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Rect displayRect = new Rect();
        mPreviewContainer.getHitRect(displayRect);
//        Log.d("frame", "getScreenBounds: "+ mPreviewContainer.getHeight());
//        Point size = new Point();
//        display.getSize(size);
//        Log.d("frame", "defaultDisplay: " + size.y);
//        return new Rect(0, 0, size.x, size.y);
        return displayRect;
    }

    @Override
    public void onShutter() {}

    public enum GraphicType{
        PARTICLE, FILTER, GAME
    }
}