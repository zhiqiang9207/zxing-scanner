package com.dg11185.lib.demo.zxing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.dg11185.lib.demo.R;
import com.libzxing.zxing.encoding.EncodingUtils;

public class GenPicActivity extends Activity implements View.OnClickListener {

    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gen_pic);
        text = (EditText) findViewById(R.id.text);
        findViewById(R.id.gen).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bitmap bitmap = EncodingUtils.createQRCode(text.getText().toString(), 540, 540,
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        ((ImageView) findViewById(R.id.result)).setImageBitmap(bitmap);
    }
}
