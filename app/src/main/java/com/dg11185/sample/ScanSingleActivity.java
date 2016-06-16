package com.dg11185.lib.demo.zxing;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dg11185.lib.demo.R;
import com.libzxing.zxing.ui.ScannerView;

public class ScanSingleActivity extends Activity {

    private ScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_single);
        mScannerView = (ScannerView) findViewById(R.id.scanner);
        //开始扫描之后才显示扫描框，不让有时候扫描框没有画好就显示了；
        mScannerView.setOnScanListener(new ScannerView.OnScanListener() {
            @Override
            public void onStart() {
                findViewById(R.id.pane).setVisibility(View.VISIBLE);
            }

            @Override
            public void onStop() {

            }
        });
        mScannerView.setFeedBackListener(new ScannerView.FeedBackListener() {
            @Override
            public void feedback(Bundle bundle) {
                Toast.makeText(ScanSingleActivity.this, bundle.getString("lResult"), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.startScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.pauseScan();
    }
}
