package com.dg11185.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;
import com.libzxing.zxing.decode.DecodeBitmap;

import java.io.File;
import java.io.IOException;

public class ScanPicActivity extends Activity implements View.OnClickListener{

    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_pic);
        findViewById(R.id.takepic).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dispatchTakePictureIntent();
    }

    /**
     * 拍照
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = createImageFile();
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, 100);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DecodeBitmap decodeBitmap = new DecodeBitmap(photoFile.getAbsolutePath(), DecodeBitmap.ALL_MODE);
        decodeBitmap.setFeedBackListener(new DecodeBitmap.FeedBackListener() {
            @Override
            public void feedback(Result result) {
                Toast.makeText(ScanPicActivity.this, result.getText(), Toast.LENGTH_LONG).show();
            }
        });
        new Thread(decodeBitmap).start();
    }

    private File createImageFile() {
        File file = new File(setMkdir(this, "ticket") + "temp.png");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static String setMkdir(Context context, String dir) {
        String filePath;
        if (checkSDCard()) {
            filePath = Environment.getExternalStorageDirectory()
                    + File.separator + dir + File.separator;
        } else {
            filePath = context.getCacheDir().getAbsolutePath() + File.separator
                    + dir + File.separator;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return filePath;
    }

    public static boolean checkSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
