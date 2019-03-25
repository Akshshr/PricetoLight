package com.pricetolight.app.anim;

import android.animation.TimeInterpolator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.pricetolight.app.util.MathUtil;

import java.lang.ref.WeakReference;

public class AnimationRunanble implements Runnable {
    private WeakReference<View> viewReference;
    private TimeInterpolator interpolator;
    private OnAnimationFinsihed onAnimationFinsihed;
    private long animationStart;
    private int delay;
    private float animationDuration = 1000;
    private float animationProgress = 0;
    private float from = 0;
    private float to = 1;
    private boolean cancelled = false;


    private AnimationRunanble(View view, float animationDuration, int delay, float from, float to, TimeInterpolator interpolator, OnAnimationFinsihed onAnimationFinsihed) {
        this.animationDuration = animationDuration;
        this.viewReference = new WeakReference<>(view);
        this.interpolator = interpolator;
        this.from = from;
        this.to = to;
        this.delay = delay;
        this.onAnimationFinsihed = onAnimationFinsihed;
    }

    private AnimationRunanble start() {
        if(viewReference.get() != null) {
            this.animationStart = System.currentTimeMillis() + this.delay;
            viewReference.get().postOnAnimationDelayed(this, this.delay);
        }
        return this;
    }

    public float getProgress() {
        return MathUtil.map(animationProgress, 0, 1, from, to);
    }

    public void cancel() {
        cancelled = true;
    }

    @Override
    public void run() {
        float linearProgress = MathUtil.mapClamp(
                (float) (System.currentTimeMillis() - animationStart),
                0, animationDuration, 0, 1);
        animationProgress = interpolator.getInterpolation(linearProgress);
        if(viewReference.get() != null && !cancelled) {
            viewReference.get().invalidate();
            if(linearProgress < 1) {
                viewReference.get().postOnAnimation(this);
            } else if(onAnimationFinsihed != null) {
                onAnimationFinsihed.onFinished();
            }
        }
    }

    public static class Builder {

        private View view;
        private TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();
        private OnAnimationFinsihed onAnimationFinsihed;
        private float from = 0;
        private float to = 1;
        private float duration = 0;
        private int delay = 0;

        public Builder(View view) {
            this.view = view;
        }

        public Builder withDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder withDelay(int delay) {
            this.delay = delay;
            return this;
        }

        public Builder withInterpolator(TimeInterpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public Builder onFinish(OnAnimationFinsihed onAnimationFinsihed) {
            this.onAnimationFinsihed = onAnimationFinsihed;
            return this;
        }

        public Builder between(float from, float to) {
            this.from = from;
            this.to = to;
            return this;
        }

        public AnimationRunanble start() {
            return new AnimationRunanble(view, duration, delay, from, to, interpolator, onAnimationFinsihed).start();
        }
    }

    public interface OnAnimationFinsihed {
        void onFinished();
    }
}
