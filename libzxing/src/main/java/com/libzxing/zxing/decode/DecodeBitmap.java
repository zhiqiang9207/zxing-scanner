package com.libzxing.zxing.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * Created by Andy on 16/5/19.
 */
public class DecodeBitmap implements Runnable {

    public static final int BARCODE_MODE = 0X100;
    public static final int QRCODE_MODE = 0X200;
    public static final int ALL_MODE = 0X300;

    private Map<DecodeHintType, Object> hints;
    private Bitmap mBitmap;
    private String filePath;
    private MultiFormatReader multiFormatReader = null;

    private FeedBackListener listener;

    /**
     * 通过bitmap识别
     * @param bitmap
     * @param decodeMode
     */
    public DecodeBitmap(Bitmap bitmap, int decodeMode) {
        init(decodeMode);
        mBitmap = bitmap;
    }

    /**
     * 通过路径识别
     * @param path
     * @param decodeMode
     */
    public DecodeBitmap(String path, int decodeMode){
        init(decodeMode);
        filePath = path;
    }

    private void init(int decodeMode){
        hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
        Collection<BarcodeFormat> decodeFormats = new ArrayList<BarcodeFormat>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        decodeFormats.addAll(EnumSet.of(BarcodeFormat.AZTEC));
        decodeFormats.addAll(EnumSet.of(BarcodeFormat.PDF_417));
        switch (decodeMode) {
            case BARCODE_MODE:
                decodeFormats.addAll(DecodeFormatManager.getBarCodeFormats());
                break;
            case QRCODE_MODE:
                decodeFormats.addAll(DecodeFormatManager.getQrCodeFormats());
                break;
            case ALL_MODE:
                decodeFormats.addAll(DecodeFormatManager.getBarCodeFormats());
                decodeFormats.addAll(DecodeFormatManager.getQrCodeFormats());
                break;
            default:
                break;
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
    }

    @Override
    public void run() {
        Looper.prepare();
        Result result = null;
        if (mBitmap == null){
            result = decodeImage(filePath);
        }else{
            result = decodeImage(mBitmap);
        }
        if (listener != null) {
            listener.feedback(result);
        }
        Looper.loop();
    }

    public interface FeedBackListener {
        void feedback(Result result);
    }

    public void setFeedBackListener(FeedBackListener listener) {
        this.listener = listener;
    }

    /**
     * 通过图片路径识别途中二维码
     * @param path
     * @return
     */
    private Result decodeImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        bitmap = BitmapFactory.decodeFile(path, options);
        return decodeImage(bitmap);
    }

    /**
     * 扫描decode bitmap
     * @param bitmap
     * @return
     */
    private Result decodeImage(Bitmap bitmap){
        int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), data);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        try {
            return multiFormatReader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } finally {
            multiFormatReader.reset();
        }
        return null;
    }
}
