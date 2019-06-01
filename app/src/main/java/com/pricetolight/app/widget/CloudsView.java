package com.pricetolight.app.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.pricetolight.R;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.BitmapFactory.decodeResource;

public class CloudsView extends View {
    public static boolean animationEnabled = true;

    private static final float SPEED_DP_PER_SEC = 20f;
    private final List<Cloud> clouds = new ArrayList<>();

    private Paint paint;
    private float density;
    private double timeStamp = -1;

    public CloudsView(Context context) {
        super(context);
        init(context);
    }

    public CloudsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CloudsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        density = context.getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long time = System.currentTimeMillis();
        if (timeStamp != -1) {
            drawClouds(canvas, time);
        } else {
            initClouds();
        }

        timeStamp = time;
        if (animationEnabled) {
            invalidate();
        }
    }

    private void initClouds() {
        int width = getWidth();
        int height = getHeight();

        Resources res = getContext().getResources();
        Bitmap cloudTop = decodeResource(res, R.drawable.cloud1);
        Bitmap cloudMiddle = decodeResource(res, R.drawable.cloud2);
        Bitmap cloudBottom = decodeResource(res, R.drawable.cloud3);

        clouds.add(new Cloud(cloudTop, 0.3f, 0));
        clouds.add(new Cloud(cloudMiddle, 0.6f, height / 2 - cloudMiddle.getHeight() / 2));
        clouds.add(new Cloud(cloudBottom, 0.8f, height - cloudBottom.getHeight()));

        float percent = 0.1f;
        for (Cloud cloud : clouds) {
            cloud.x = width * percent;
            percent += 0.25f;
        }
    }

    private void drawClouds(Canvas canvas, long time) {
        for (Cloud cloud : clouds) {
            updatePosition(cloud, (time - timeStamp) / 1000d);
            canvas.drawBitmap(cloud.bitmap, cloud.x, cloud.y, paint);
        }
    }

    private void updatePosition(Cloud cloud, double timeDelta) {
        cloud.x += density * SPEED_DP_PER_SEC * cloud.speedMultiplier * timeDelta;

        int width = getWidth();
        if (cloud.x > width) {
            cloud.x = -cloud.bitmap.getWidth();
        }
        int height = getHeight();

    }

    private static class Cloud {
        final Bitmap bitmap;
        final float speedMultiplier;
        final int y;
        float x;

        Cloud(Bitmap bitmap, float speedMultiplier, int y) {
            this.bitmap = bitmap;
            this.speedMultiplier = speedMultiplier;
            this.y = y;
        }
    }
}
