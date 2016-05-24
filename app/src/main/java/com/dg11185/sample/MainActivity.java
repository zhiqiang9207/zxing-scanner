package com.dg11185.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                intent = new Intent(this, ScanSingleActivity.class);
                startActivity(intent);
                break;
            case "扫描两个二维码":
                intent = new Intent(this, ScanActivity.class);
                startActivity(intent);
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
}
