package com.example.jraw_test_2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.text.AttributedCharacterIterator;
import java.util.ArrayList;


// TODO: save path and store it as bitmap?


public class PaintView extends View {

    private Path path = new Path();
    private Paint brush = new Paint();
    private Rect boundary = new Rect();
    private int w, h;

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);

        brush.setAntiAlias(true);
        brush.setColor(Color.BLACK);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(6f);
    }

    /*
    using onSizeChanged() since it's called after the width and height are set.
    e.g., getWidth() -> 0 at the time View is initialized
    */
    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w,h,ow,oh);
        w = getWidth();
        h = getHeight();
        boundary.set(0, h/2-w/2, w, h/2+w/2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(boundary, brush);
        canvas.drawPath(path, brush);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (boundary.contains((int) pointX, (int) pointY)) path.moveTo(pointX, pointY);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (boundary.contains((int) pointX, (int) pointY)) path.lineTo(pointX, pointY);
                event.setAction(MotionEvent.ACTION_UP);
                path.moveTo(pointX,pointY);
                break;
            default:
                return false;
        }
        postInvalidate();
        return false;
    }
}
