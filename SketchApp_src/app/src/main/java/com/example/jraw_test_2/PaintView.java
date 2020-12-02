package com.example.jraw_test_2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;


public class PaintView extends View {

    public static final String TAG = "PaintView";

    private Path path;
    private ArrayList<Path> paths;
    private Paint mPaint;
    public Canvas canvas;
    public Bitmap mBitmap;

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);

        path = new Path();
        paths = new ArrayList<Path>();
        mPaint = new Paint();
        paintViewConfig(); // setup Paint details like brush size
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "PaintView onSizeChanged()");

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(mBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Path p : paths) canvas.drawPath(p, mPaint);
        canvas.drawPath(path, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(pointX, pointY);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(pointX, pointY);
                break;
            case MotionEvent.ACTION_UP:
                paths.add(path);
                path = new Path();
                break;
        }
        postInvalidate();
        return false;
    }

    public void undo() {
        if (paths.size() > 0) paths.remove(paths.size()-1);
        invalidate();
    }

    public void clear() {
        paths = new ArrayList<Path>();
        path = new Path();
        invalidate();
    }

    private void paintViewConfig() {
        Log.d(TAG, "PaintView paintViewConfig()");

        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(16f);
    }
}
