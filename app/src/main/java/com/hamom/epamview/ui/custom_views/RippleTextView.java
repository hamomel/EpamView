package com.hamom.epamview.ui.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import com.hamom.epamview.R;

/**
 * Created by hamom on 29.11.17.
 */

public class RippleTextView extends android.support.v7.widget.AppCompatTextView {

    private static final int DEFAULT_CIRCLE_WIDTH_DP = 2;
    private static final int RIPPLE_ALPHA = 50;
    private int mMainColor;
    private int mRippleColor;
    private Paint mRipplePaint;
    private Paint mCirclePaint;
    private Path mPath;
    private float mCircleRadius;
    private float mRippleRadius;
    private float mCircleWidth;
    private ValueAnimator mAnimator;
    private RectF mRect;

    public RippleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RippleTextView);

        float circleWidth;
        try {
            int defColor = ContextCompat.getColor(context, android.R.color.white);
            mMainColor = array.getColor(R.styleable.RippleTextView_mainColor, defColor);
            mRippleColor = array.getColor(R.styleable.RippleTextView_rippleColor, defColor);
            circleWidth = array.getDimension(R.styleable.RippleTextView_circleWidth, 0);
        } finally {
            array.recycle();
        }

        float scale = getScale(context);
        mCircleWidth = circleWidth > 0 ? circleWidth : DEFAULT_CIRCLE_WIDTH_DP * scale;
    }

    private float getScale(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return metrics.density;
    }

    private void init() {
        setTextColor(mMainColor);
        setGravity(Gravity.CENTER);
        setClickable(true);
        setTextSize(getHeight() / 6);

        mCircleRadius = getHeight() / 2 - mCircleWidth;

        mRipplePaint = new Paint();
        mRipplePaint.setColor(mRippleColor);
        mRipplePaint.setStyle(Paint.Style.FILL);
        mRipplePaint.setAlpha(RIPPLE_ALPHA);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mMainColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleWidth);

        mAnimator = ValueAnimator.ofFloat(0, mCircleRadius);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.setDuration(200);
        mAnimator.addUpdateListener(getListener());

        mRect = new RectF(getY(), getX(), getY() + getWidth(), getX() + getHeight());
        mPath.addOval(mRect, Path.Direction.CCW);
    }

    public int getMainColor() {
        return mMainColor;
    }

    public void setMainColor(int mainColor) {
        mMainColor = mainColor;
    }

    public int getRippleColor() {
        return mRippleColor;
    }

    public void setRippleColor(int rippleColor) {
        mRippleColor = rippleColor;
    }

    public float getCircleWidth() {
        return mCircleWidth;
    }

    public void setCircleWidth(float circleWidth) {
        mCircleWidth = circleWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int min = Math.min(height, width);
        int w = resolveSizeAndState(min, widthMeasureSpec, 0);
        int h = resolveSizeAndState(min, heightMeasureSpec, 0);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        init();
    }

    @NonNull
    private ValueAnimator.AnimatorUpdateListener getListener() {
        return animation -> {
            mRippleRadius = (float) animation.getAnimatedValue();
            invalidate();
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(mPath);
        canvas.drawCircle(getWidth()/2, getHeight()/2, mRippleRadius, mRipplePaint);
        canvas.drawCircle(getWidth()/2, getHeight()/2, mCircleRadius, mCirclePaint);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mAnimator.start();
                return true;
            case MotionEvent.ACTION_UP:
                mAnimator.reverse();
                return true;
                default:
                    return super.onTouchEvent(event);
        }
    }
}
