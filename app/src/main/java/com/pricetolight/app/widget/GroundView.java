package com.pricetolight.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.pricetolight.R;
import com.pricetolight.app.util.Util;

public class GroundView extends View {

    private static final String TAG = GroundView.class.getSimpleName();

    private Path path;
    private Paint paint;
    private float curveHeight;
    private boolean greenModeEnabled;

    public GroundView(Context context) {
        super(context);
        init();
    }

    public GroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getDefaultColor());

        curveHeight = Util.dpToPx(getContext(), 82);
        setWillNotDraw(false);
    }

    @ColorInt
    private int getDefaultColor() {
        return ContextCompat.getColor(getContext(),R.color.blue700);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        path.rewind();
        path.moveTo(0, curveHeight);
        path.quadTo(width / 2, 0, width, curveHeight);
        path.lineTo(width, height);
        path.lineTo(0, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
        super.onDraw(canvas);
    }
}
