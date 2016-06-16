package com.dg11185.lib.demo.zxing;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dg11185.lib.demo.R;

/**
 * 扫描之前针对6.0系统，要申请运行时权限
 */
public class MainActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing_main);
        init();
    }

    private void init(){
        LinearLayout container = (LinearLayout)findViewById(R.id.container);
        for (int i = 0; i < container.getChildCount(); i++){
            container.getChildAt(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (((Button)v).getText().toString()){
            case "扫描条形码":
            case "扫描单个二维码":
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            100);
                } else {
                    intent = new Intent(this, ScanSingleActivity.class);
                    startActivity(intent);
                }
                break;
            case "扫描两个二维码":
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            200);
                } else {
                    intent = new Intent(this, ScanActivity.class);
                    startActivity(intent);
                }
                break;
            case "扫描图片二维码":
                intent = new Intent(this, ScanPicActivity.class);
                startActivity(intent);
                break;
            case "生成图片二维码":
                intent = new Intent(this, GenPicActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 100:
                    Intent intent1 = new Intent(this, ScanSingleActivity.class);
                    startActivity(intent1);
                    break;
                case 200:
                    Intent intent2 = new Intent(this, ScanActivity.class);
                    startActivity(intent2);
                    break;
            }
        }
    }
}
