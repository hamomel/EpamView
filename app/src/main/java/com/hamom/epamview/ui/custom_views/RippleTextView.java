package com.hamom.epamview.ui.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import com.hamom.epamview.R;
import com.hamom.epamview.utils.ConstantManager;

/**
 * Created by hamom on 29.11.17.
 */

public class RippleTextView extends android.support.v7.widget.AppCompatTextView {
    private static String TAG = ConstantManager.TAG_PREFIX + "RippleTextView: ";

    @Dimension(unit = Dimension.DP) private static final int DEFAULT_CIRCLE_WIDTH_DP = 2;
    private static final int RIPPLE_ALPHA = 50;

    @ColorInt private int mMainColor;
    @ColorInt private int mRippleColor;

    private Paint mRipplePaint;
    private Paint mCirclePaint;
    private Path mPath;
    private ValueAnimator mAnimator;

    @Px private int mCircleRadius;
    @Px private int mRippleRadius;
    @Px private int mCircleX;
    @Px private int mCircleY;
    @Px private int mRippleX;
    @Px private int mRippleY;
    @Px private int mCircleWidth;

    public RippleTextView(Context context) {
        super(context);
    }

    public RippleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RippleTextView);

        try {
            int defColor = ContextCompat.getColor(context, android.R.color.white);
            mMainColor = array.getColor(R.styleable.RippleTextView_mainColor, defColor);
            mRippleColor = array.getColor(R.styleable.RippleTextView_rippleColor, defColor);
            mCircleWidth = (int) array.getDimension(R.styleable.RippleTextView_circleWidth, 0);
        } finally {
            array.recycle();
        }
    }

    @Px
    private int dpToPx(@Dimension(unit = Dimension.DP) int dp) {
        final Resources resources = getResources();
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    private void init() {
        setTextColor(mMainColor);
        setGravity(Gravity.CENTER);
        setClickable(true);
        setTextSize(getHeight() / 6);

        mCircleWidth = mCircleWidth > 0 ? mCircleWidth : dpToPx(DEFAULT_CIRCLE_WIDTH_DP);
        mCircleRadius = getHeight() / 2 - mCircleWidth;
        mCircleX = getWidth() / 2;
        mCircleY = getHeight() / 2;

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

        RectF rect = new RectF(0, 0, getWidth(), getHeight());
        mPath = new Path();
        mPath.addOval(rect, Path.Direction.CCW);
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

    public void setCircleWidth(int circleWidth) {
        mCircleWidth = circleWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int modeW = MeasureSpec.getMode(widthMeasureSpec);
        int modeH = MeasureSpec.getMode(heightMeasureSpec);

        int min = Math.min(heightMeasureSpec, widthMeasureSpec);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(min, modeW);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(min, modeH);
        super.onMeasure(min, min);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        init();
    }

    @NonNull
    private ValueAnimator.AnimatorUpdateListener getListener() {
        return animation -> {
            // TODO: 02.12.17 calculate radius factor dynamical
            mRippleRadius = (int) ((Float)animation.getAnimatedValue() * 2);
            invalidate();
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(mPath);
        canvas.drawCircle(mRippleX, mRippleY, mRippleRadius, mRipplePaint);
        canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePaint);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                calculateRippleCenter(event.getX(), event.getY());
                mAnimator.start();
                return true;
            case MotionEvent.ACTION_UP:
                mAnimator.reverse();
                return true;
                default:
                    return super.onTouchEvent(event);
        }
    }

    private void calculateRippleCenter(float x, float y) {

        mRippleX = (int) x;
        mRippleY = (int) y;
    }
}
