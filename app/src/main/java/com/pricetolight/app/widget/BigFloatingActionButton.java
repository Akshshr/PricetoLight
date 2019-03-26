package com.pricetolight.app.widget;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

public class BigFloatingActionButton extends FloatingActionButton {

    public BigFloatingActionButton(Context context) {
        super(context);
    }

    public BigFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BigFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        setMeasuredDimension((int) (width * 1.5f), (int) (height * 1.5f));
    }

}