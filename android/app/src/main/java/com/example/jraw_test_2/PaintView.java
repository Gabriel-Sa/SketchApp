package com.example.jraw_test_2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.text.AttributedCharacterIterator;
import java.util.ArrayList;


// TODO: save path and store it as bitmap?


public class PaintView extends View {

    private Path path = new Path();
    private Paint brush = new Paint();

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);

        brush.setAntiAlias(true);
        brush.setColor(Color.BLACK);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(6f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, brush);
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
            default:
                return false;
        }
        postInvalidate();
        return false;
    }
}
