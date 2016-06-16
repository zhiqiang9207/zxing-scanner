package com.dg11185.lib.demo.zxing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.dg11185.lib.demo.R;

/**
 * Created by Andy on 16/4/14.
 */
public class ScanPane extends View{
    private int loc_y = 0;
    private Bitmap scanFrame = null;
    private Bitmap rayta = null;
    private int width;
    private int height;
    private Paint paint;
    private int sec_w = 0;
    private int start_x = 0;
    private int start_y = 0;
    private Matrix matrix;
    private boolean med = true;

    public ScanPane(Context context) {
        super(context);
    }

    public ScanPane(Context context, AttributeSet attrs) {
        super(context, attrs);
        scanFrame = BitmapFactory.decodeResource(context.getResources(), R.mipmap.saomiaok);
        rayta = BitmapFactory.decodeResource(context.getResources(), R.mipmap.saomiaog);
        paint = new Paint();
        matrix = new Matrix();
    }

    public ScanPane(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        med = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(med){
            float scaleRate = (float)height /(float) scanFrame.getHeight();//缩放的比例
            matrix.postScale(scaleRate, scaleRate);
            scanFrame = Bitmap.createBitmap(scanFrame, 0, 0, scanFrame.getWidth(), scanFrame.getHeight(), matrix, true);
            rayta = Bitmap.createBitmap(rayta, 0, 0, rayta.getWidth(), rayta.getHeight(), matrix, true);
            sec_w = width - scanFrame.getWidth();
            start_x = (scanFrame.getWidth() - rayta.getWidth()) / 2;
            start_y = 20;
            loc_y = start_y - rayta.getHeight();
            med = !med;
        }
        canvas.drawBitmap(scanFrame, 0, 0, paint);
        canvas.drawBitmap(scanFrame, sec_w, 0, paint);
        canvas.save();
        canvas.clipRect(0, start_y, width, height - start_y);
        canvas.drawBitmap(rayta, start_x, loc_y, paint);
        canvas.drawBitmap(rayta, sec_w + start_x, loc_y, paint);
        canvas.restore();
        loc_y = (loc_y < height - start_y) ? (loc_y + 5) : (start_y - rayta.getHeight());
        postInvalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
