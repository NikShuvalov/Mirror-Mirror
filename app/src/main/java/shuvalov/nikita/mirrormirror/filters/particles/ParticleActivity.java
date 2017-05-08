package shuvalov.nikita.mirrormirror.filters.particles;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.FaceDetector;

import shuvalov.nikita.mirrormirror.R;
import shuvalov.nikita.mirrormirror.browsing.BrowsingActivity;
import shuvalov.nikita.mirrormirror.camera.CameraSourceGenerator;
import shuvalov.nikita.mirrormirror.camera.FaceDetectorGenerator;
import shuvalov.nikita.mirrormirror.camerafacetracker.FaceTracker;
import shuvalov.nikita.mirrormirror.camerafacetracker.Preview;
import shuvalov.nikita.mirrormirror.filters.OverlayMod;

public class ParticleActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{
    private FrameLayout mFaceDetect, mPreviewContainer;
    private Preview mPreview;
    private ImageButton mCameraButton;
    public static final int CAMERA_PERMISSION_REQUEST = 9999;
    public static final int STORAGE_PERMISSION_REQUEST = 2;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;
    private Toolbar mToolbar;
//    private RecyclerView mFilterRecycler;
//    private boolean mFilterSelectorVisible;
    public CameraSource mCameraSource;
    private FaceDetector mFaceDetector;
    private ParticleEngine mParticleEngine;
    private int mViewWidth, mViewHeight;

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
        mViewWidth = size.x;
        mViewHeight = size.y;

        FaceTracker.getInstance().setScreenSize(mViewHeight, mViewWidth);

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
            getParticlesReady();
        }
    }

    public void getParticlesReady(){
        Rect screenBounds = new Rect(0,0,mViewHeight, mViewWidth);//This might need to be reversed, the whole landscape/portrait shifting has got me confused.
        mParticleEngine = new ParticleEngine(ParticleEngine.PhysicsType.SIMPLE, screenBounds, null);
        mParticleEngine.populateParticles(new Particle(R.drawable.flamekey0, 200,mViewWidth,0,0,1));
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
        OverlayMod overlayMod = new OverlayMod(this);
        overlayMod.setZOrderMediaOverlay(true);

        mPreview = new Preview(this);
        mPreview.setCameraSource(mCameraSource);

        mPreviewContainer.addView(mPreview);
        mFaceDetect.addView(overlayMod);

        mPreviewContainer.setOnClickListener(this);
        mCameraButton.setOnClickListener(this);
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
        mFaceDetect = (FrameLayout) findViewById(R.id.face_detect);
        mCameraButton = (ImageButton) findViewById(R.id.camera_button);
        mNavView = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mFilterRecycler = (RecyclerView) findViewById(R.id.filters_recycler);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera_button:
                Toast.makeText(this, "Just for show for now", Toast.LENGTH_SHORT).show();
//                captureImage();
                break;
//            default:
//                if (!mFilterSelectorVisible) {
//                    replaceBottomView(mCameraButton, mFilterRecycler);
//                    mFilterSelectorVisible = true;
//                } else {
//                    replaceBottomView(mFilterRecycler, mCameraButton);
//                    mFilterSelectorVisible = false;
//                }
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        mFaceDetect.removeAllViews();
        mPreviewContainer.removeView(mPreview);
        if(mCameraSource!=null){
            mCameraSource.stop();
        }
        if(mFaceDetector!=null && mFaceDetector.isOperational()){
            mFaceDetector.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCameraSource!=null){
            mCameraSource.release();
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.browse_option:
                Intent browseIntent = new Intent(this, BrowsingActivity.class);
                startActivity(browseIntent);
                break;
            case R.id.particle_option:
                Toast.makeText(this, "Already in particle activity", Toast.LENGTH_SHORT).show();
                break;
        }
        mDrawerLayout.closeDrawers();
        return false;
    }
}
