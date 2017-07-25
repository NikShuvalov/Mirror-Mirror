package shuvalov.nikita.mirrormirror.browsing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.security.Permission;
import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.AppConstants;
import shuvalov.nikita.mirrormirror.R;

public class BrowsingActivity extends AppCompatActivity implements BrowseSwipeListener.OnSwipeListener, View.OnClickListener{
    private ImageView mImageView;
    private static final int READ_STORAGE_REQUEST = 42;
    private BrowsingTracker mBrowsingTracker;
    private RelativeLayout mBottomPanel;
    private ImageButton mShareButton;
    private boolean mPanelShowing;
    private static final long KILOBYTE = 1024 * 1024;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing);

        mPanelShowing = true;
        mBrowsingTracker= BrowsingTracker.getInstance();

        findViews();
        permissionValidate();
        if(mPanelShowing) hideBottomPanel();
        loadImage();
        mBottomPanel.setOnClickListener(this);
        mShareButton.setOnClickListener(this);
    }


    public void hideBottomPanel(){
        mPanelShowing = false;
        Animation panelHideAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_panel_hide);
        panelHideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBottomPanel.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBottomPanel.setAnimation(panelHideAnim);
        mBottomPanel.setVisibility(View.GONE);
    }

    public void showBottomPanel(){
        mPanelShowing= true;
        Animation panelShowAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_panel_show);
        panelShowAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBottomPanel.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBottomPanel.setAnimation(panelShowAnim);
        mBottomPanel.setVisibility(View.VISIBLE);
    }

    public void findViews(){
        mImageView = (ImageView)findViewById(R.id.image_view);
        mBottomPanel = (RelativeLayout)findViewById(R.id.bottom_panel_holder);
        mShareButton = (ImageButton)findViewById(R.id.share_button);
        mImageView.setOnTouchListener(new BrowseSwipeListener(this));
    }

    public void permissionValidate(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_REQUEST);
            }
        }else{
            debugLoadImage();
        }
    }

    public void debugLoadImage(){
        File mirrorFolder = new File(AppConstants.getImageDirectoryPath());
        if(mirrorFolder.exists() && mirrorFolder.isDirectory()){
            File[] files = mirrorFolder.listFiles();
            compileImageFiles(files);
        }
    }

    public void compileImageFiles(File[] files) {
        if(files.length>0) {
            ArrayList<File> imageFiles = new ArrayList<>();
            for (File f : files) {
                if (!f.isDirectory() &&
                        f.getName().endsWith(".jpg") &&
                        f.length()>KILOBYTE) { //Ignores directories, non-jpgs, and files under 1kb
                    imageFiles.add(f);
                }
            }
            //Since the user can't delete images from the app itself, if the amount of files in external storage == amount of files in the tracker, then every image is already loaded and there's no need to reload them.
            //FixMe: Once user can delete images more readily might want to just clear cache and reload images every time. Or better yet look at timestamp of most recent image in both cache and external storage to determine if images need to be recached.
            if(mBrowsingTracker.albumSize() != imageFiles.size()){
                mBrowsingTracker.clearCache();
                mBrowsingTracker.setImageFiles(imageFiles);
            }
        }else{
            Toast.makeText(this, "No files were found", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadImage(){
        if(!mBrowsingTracker.isAlbumEmpty()){
            File firstFile = mBrowsingTracker.getCurrentImageFile();
            try {
                Log.d("Howdy", "loadImage: "+ String.valueOf(firstFile==null));
                if(firstFile!=null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(firstFile.getAbsolutePath());
                    mImageView.setImageBitmap(bitmap);
                }else{
                    Point size = new Point();
                    getWindowManager().getDefaultDisplay().getSize(size);
                    Bitmap bitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(Color.BLACK);
                    canvas.drawText("Empty Picture", mImageView.getWidth()*.1f,mImageView.getHeight()/2, null);
                    mImageView.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Log.d("Howdy", "loadImage: Empty Album ");
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            Log.d("Howdy", "ScreenSize:" + size.x + ","+ size.y);
            Bitmap bitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.BLACK);
            canvas.drawText("No pictures in album", mImageView.getWidth()*.1f,mImageView.getHeight()/2, null);
            mImageView.setImageBitmap(bitmap);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length>0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            debugLoadImage();
        }else{
            Toast.makeText(this, "Can't browse images without permission", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSwipe(BrowseSwipeListener.SWIPE_DIRECTION d) {
        switch(d){
            case RIGHT:
                mBrowsingTracker.moveToPreviousPicture();
                loadImage();
                break;
            case LEFT:
                mBrowsingTracker.moveToNextPicture();
                loadImage();
                break;
            case CLICK:
                if(!mPanelShowing) showBottomPanel();
                else hideBottomPanel();
                break;
        }
    }


    public void shareImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        File file = mBrowsingTracker.getCurrentImageFile();
        if(file!=null && file.length()>KILOBYTE) {
            Uri imageUri = Uri.fromFile(mBrowsingTracker.getCurrentImageFile());
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.setType("image/jpeg");
            startActivity(Intent.createChooser(intent, "Share to: "));
        }else{
            Toast.makeText(this, "Can't share an empty image", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View view) {
        shareImage();
    }
}