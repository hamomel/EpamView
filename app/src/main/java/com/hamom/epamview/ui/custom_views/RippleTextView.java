package com.hamom.epamview.ui.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
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
        setClickable(true);
        setHapticFeedbackEnabled(true);
    }

    public RippleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        setHapticFeedbackEnabled(true);
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

    @Px private int dpToPx(@Dimension(unit = Dimension.DP) int dp) {
        final Resources resources = getResources();
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    private void init() {
        setTextColor(mMainColor);
        setGravity(Gravity.CENTER);
        setTextSize(getHeight() / 6);

        // Turn off hardware acceleration to let canvas.clipPath() work
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        mCircleWidth = mCircleWidth > 0 ? mCircleWidth : dpToPx(DEFAULT_CIRCLE_WIDTH_DP);
        mCircleRadius = getMeasuredHeight() / 2 - mCircleWidth;
        mCircleX = getMeasuredWidth() / 2;
        mCircleY = getMeasuredHeight() / 2;

        mRipplePaint = new Paint();
        mRipplePaint.setColor(mRippleColor);
        mRipplePaint.setStyle(Paint.Style.FILL);
        mRipplePaint.setAlpha(RIPPLE_ALPHA);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mMainColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleWidth);

        mAnimator = new ValueAnimator();
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.setDuration(100);
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

        // Make it square
        int min = Math.min(heightMeasureSpec, widthMeasureSpec);
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
            mRippleRadius = ((Float) animation.getAnimatedValue()).intValue();
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
        if (!isInCircle(event.getX(), event.getY())) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                calculateRipple(event.getX(), event.getY());
                mAnimator.start();
                return true;
            case MotionEvent.ACTION_UP:
                mAnimator.reverse();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private boolean isInCircle(float x, float y) {
        double radDist = getTouchEventDist(x, y);
        return radDist < mCircleRadius;
    }

    private double getTouchEventDist(float x, float y) {
        float xDist = Math.abs(x - mCircleX);
        float yDist = Math.abs(y - mCircleY);
        return Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
    }

    private void calculateRipple(float x, float y) {
        int maxRippleRadius = (int) (mCircleRadius + getTouchEventDist(x, y));
        mAnimator.setFloatValues(0, maxRippleRadius);
        mRippleX = (int) x;
        mRippleY = (int) y;
    }
}
