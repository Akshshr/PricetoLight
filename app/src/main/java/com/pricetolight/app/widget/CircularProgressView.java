package com.pricetolight.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.pricetolight.R;
import com.pricetolight.app.anim.AnimationRunanble;
import com.pricetolight.app.util.MathUtil;
import com.pricetolight.app.util.Util;

public class CircularProgressView extends View {

    private RectF rect = new RectF();
    private Paint bgPaint;
    private Paint progressPaint;
    private Paint labelPaint;
    private Paint labelTextPaint;
    private float progress = 0;
    private float labelWidth;
    private float labelHeight;
    private float labelCornerRadius;
    private boolean showLabel = false;
    private AnimationRunanble animationRunnable;

    public CircularProgressView(Context context) {
        super(context);
        init();
    }

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(Util.dpToPx(getContext(), 14));
        bgPaint.setColor(Color.WHITE);
        progressPaint = new Paint(bgPaint);

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        labelPaint.setStyle(Paint.Style.FILL);
        labelPaint.setColor(Color.WHITE);

        labelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        labelTextPaint.setStyle(Paint.Style.FILL);
        labelTextPaint.setColor(Color.BLACK);
        labelTextPaint.setTextSize(getResources().getDimension(R.dimen.fontSize10));
        labelTextPaint.setTextAlign(Paint.Align.CENTER);

        labelWidth = Util.dpToPx(getContext(), 40);
        labelHeight = Util.dpToPx(getContext(), 16);
        labelCornerRadius = Util.dpToPx(getContext(), 4);
    }

    public void setProgressColor(@ColorInt int color) {
        progressPaint.setColor(color);
        invalidate();
    }

    public void setBackgroundColor(@ColorInt int color) {
        bgPaint.setColor(color);
        invalidate();
    }

    public void setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
        invalidate();
    }


    public void setProgress(double progress) {

        setProgress(progress, 350, 300);
    }

    public void setProgress(double progress, int duration, int delay) {
        if(Double.isNaN(progress)) {
            return;
        }
        if(this.progress != progress) {
            if(animationRunnable != null) {
                animationRunnable.cancel();
            }
            animationRunnable = new AnimationRunanble.Builder(this)
                    .withDuration(duration)
                    .withDelay(delay)
                    .withInterpolator(new DecelerateInterpolator())
                    .between(this.progress, (float) progress)
                    .start();
        }
        this.progress = (float) progress;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rect = new RectF(
                bgPaint.getStrokeWidth() / 2,
                bgPaint.getStrokeWidth() / 2,
                w - bgPaint.getStrokeWidth() / 2,
                h - bgPaint.getStrokeWidth() / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float progress = animationRunnable != null ? animationRunnable.getProgress() : this.progress;
        canvas.drawArc(rect, -90, progress * 360 * 1.01f, false, progressPaint);
        canvas.drawArc(rect, -90 + progress * 360, (1 - progress) * 360, false, bgPaint);

        if(showLabel) {
            float scaledX = (float) Math.cos(Math.PI / 2 - progress * 2 * Math.PI);
            float scaledY = (float) Math.sin(Math.PI / 2 - progress * 2 * Math.PI);

            float targetX = MathUtil.map(scaledX, -1, 1, 0, getWidth());
            float targetY = MathUtil.map(scaledY, -1, 1, getHeight(), 0);

            canvas.drawRoundRect(
                    targetX - labelWidth / 2,
                    targetY - labelHeight / 2,
                    targetX + labelWidth / 2,
                    targetY + labelHeight / 2,
                    labelCornerRadius,
                    labelCornerRadius,
                    labelPaint);

            canvas.drawText(Math.round(progress * 100) + "%", targetX, targetY + labelTextPaint.getTextSize() / 2, labelTextPaint);
        }
    }
}
