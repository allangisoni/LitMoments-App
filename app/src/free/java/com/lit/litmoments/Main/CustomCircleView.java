package com.lit.litmoments.Main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class CustomCircleView extends View {

    private Paint paint;
    public CustomCircleView(Context context) {
        super(context);
        paint = new Paint();
        paint = setPaint(paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(200, 200, 100,paint);
    }

    public Paint setPaint(Paint p){
        paint = p;
        return paint;
    }
}
