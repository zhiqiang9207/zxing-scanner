package com.dg11185.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.libzxing.zxing.ui.ScannerView;

public class ScanSingleActivity extends Activity {

    private ScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_single);
        mScannerView = (ScannerView) findViewById(R.id.scanner);
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
