package com.dg11185.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.libzxing.zxing.ui.ScannerView;

public class ScanActivity extends FragmentActivity {

    private ScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        mScannerView = (ScannerView) findViewById(R.id.scanner);
        mScannerView.setFeedBackListener(new ScannerView.FeedBackListener() {
            @Override
            public void feedback(Bundle bundle) {
                Toast.makeText(ScanActivity.this, bundle.getString("lResult") + "\n" + bundle.getString("rResult"), Toast.LENGTH_LONG).show();
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
