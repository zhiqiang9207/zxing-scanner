package com.libzxing.zxing.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.libzxing.R;
import com.libzxing.zxing.camera.CameraManager;
import com.libzxing.zxing.decode.DecodeThread;
import com.libzxing.zxing.utils.CaptureViewHandler;
import com.libzxing.zxing.utils.InactivityTimer;

import java.io.IOException;

/**
 * Created by Andy on 16/4/8.
 */
public class ScannerView extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG = ScannerView.class.getSimpleName();
    private Rect mCorpArea = null;
    private CameraManager cameraManager;
    private Activity mActivity;
    private InactivityTimer inactivityTimer;
    public CaptureViewHandler handler;
    private boolean isDoubleScan = true;
    private boolean isCenterInHorizontal = false;
    private boolean isCenterInVertical = false;
    private int width = 0;
    private int height = 0;
    private final Object lock = new Object();

    private int crop_x, crop_y, crop_width, crop_height, crop_marginLeft, crop_marginTop; //截取区域的起点，长宽

    private FeedBackListener mFeedBackListener = null;

    private boolean isStarted = false;

    public interface FeedBackListener{
        /***
         * 如果是单个扫描框，结果使用bundle.getString("lResult");
         * 如果是两个扫描框，结果使用bundle.getString("lResult")以及bundle.getString("rResult")
         * @param bundle
         */
        void feedback(Bundle bundle);
    }

    /*设置handler*/
    public void setFeedBackListener(FeedBackListener feedBackListener){
        mFeedBackListener = feedBackListener;
    }

    public void setDoubleScan(boolean isDoubleScan){
        this.isDoubleScan = isDoubleScan;
    }

    public ScannerView(Context context) {
        super(context);
        mActivity = (Activity)context;
    }


    public ScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity)context;
        initAttribute(context, attrs);
    }

    public ScannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity = (Activity)context;
        initAttribute(context, attrs);
    }

    private void initAttribute(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.crop);
        crop_marginLeft = a.getDimensionPixelOffset(R.styleable.crop_marginLeft, 0);
        crop_marginTop = a.getDimensionPixelOffset(R.styleable.crop_marginTop, 0);
        crop_width = a.getDimensionPixelOffset(R.styleable.crop_frame_width, 0);
        crop_height = a.getDimensionPixelOffset(R.styleable.crop_frame_height, 0);

        isDoubleScan = a.getBoolean(R.styleable.crop_scan_double, false);
        isCenterInHorizontal = a.getBoolean(R.styleable.crop_centerInHorizontal, false);
        isCenterInVertical = a.getBoolean(R.styleable.crop_centerInVertical, false);
        a.recycle();
        if(crop_width == 0 && crop_height == 0){
            throw new IllegalStateException("crop area can't be empty");
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        if (isCenterInHorizontal){
            crop_x = (width - crop_width) / 2 + crop_marginLeft;
        }else{
            crop_x = crop_marginLeft;
        }
        if(isCenterInVertical){
            crop_y = (height - crop_height) / 2 + crop_marginTop;
        }else{
            crop_y = crop_marginTop;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 获取剪切区域
     * @return
     */
    public Rect getCropArea(){
        return mCorpArea;
    }

    /**
     * 设置剪切区域
     * @param area 这个area的定制，要使用cameraManager.getCameraResolution().y和cameraManager.getCameraResolution().x来确定最终剪切区域在图片中的位置，具体请参照initCrop;
     */
    public void setCropArea(Rect area) {
        mCorpArea = area;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * 加载相机
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureViewHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }
            initCrop();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    /**
     * 展示错误信息
     */
    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getString(R.string.app_name));
        builder.setMessage("Camera error");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mActivity.finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                mActivity.finish();
            }
        });
        builder.show();
    }

    public Handler getHandler() {
        return handler;
    }

    public FeedBackListener getResultHandler(){
        return mFeedBackListener;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public boolean isDoubleScan(){
        return isDoubleScan;
    }

    /**
     * 初始化截取的矩形区域
     */
    public void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = crop_x * cameraWidth / getWidth();
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = crop_y * cameraHeight / getHeight();

        /** 计算最终截取的矩形的宽度 */
        int width = crop_width * cameraWidth / getWidth();
        /** 计算最终截取的矩形的高度 */
        int height = crop_height * cameraHeight / getHeight();

        /** 生成最终的截取的矩形 */
        mCorpArea = new Rect(x, y, width + x, height + y);
    }

    /**
     * 开始扫描，延迟200毫秒等待页面加载完成；
     */
    public void startScan(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    if(!isStarted) {
                        if (cameraManager == null) cameraManager = new CameraManager(mActivity);
                        if (inactivityTimer == null)
                            inactivityTimer = new InactivityTimer(mActivity);
                        inactivityTimer.onResume();
                        initCamera(getHolder());
                        getHolder().addCallback(ScannerView.this);
                        isStarted = true;
                    }
                }
            }
        }, 200);
    }


    /**
     * 停止扫描
     */
    public synchronized void pauseScan(){
        isStarted = false;
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }if(inactivityTimer != null && cameraManager != null) {
            inactivityTimer.onPause();
            cameraManager.closeDriver();
            getHolder().removeCallback(this);
        }
    }

    /**
     * 再次扫描
     */
    public void scanAgain(){
        cameraManager.requestPreviewFrame(handler.decodeThread.getHandler(), R.id.decode);
    }
}
