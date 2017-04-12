package shuvalov.nikita.mirrormirror.browsing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.Permission;
import java.util.ArrayList;

import shuvalov.nikita.mirrormirror.AppConstants;
import shuvalov.nikita.mirrormirror.R;

public class BrowsingActivity extends AppCompatActivity implements BrowseSwipeListener.OnSwipeListener{
    private ImageView mImageView;
    private static final int READ_STORAGE_REQUEST = 42;
    private BrowsingTracker mBrowsingTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing);

        mBrowsingTracker= BrowsingTracker.getInstance();

        findViews();
        permissionValidate();
        loadImage();
    }

    public void findViews(){
        mImageView = (ImageView)findViewById(R.id.image_view);
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
                if (!f.isDirectory() && f.getName().endsWith(".jpg")) {
                    imageFiles.add(f);
                }
            }
            mBrowsingTracker.setImageFiles(imageFiles);
        }else{
            Toast.makeText(this, "No files were found", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadImage(){
        if(!mBrowsingTracker.isAlbumEmpty()){
            File firstFile = mBrowsingTracker.getCurrentImageFile();
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(firstFile.getAbsolutePath());
                mImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        Log.d("MainActivity", "onSwipe: "+ d);
        switch(d){
            case RIGHT:
                mBrowsingTracker.moveToPreviousPicture();
                loadImage();
                break;
            case LEFT:
                mBrowsingTracker.moveToNextPicture();
                loadImage();
                break;

        }
    }
}