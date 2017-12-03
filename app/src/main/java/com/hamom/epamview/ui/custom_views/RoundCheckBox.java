package com.hamom.epamview.ui.custom_views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.Px;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.hamom.epamview.R;

/**
 * Created by hamom on 30.11.17.
 */

public class RoundCheckBox extends android.support.v7.widget.AppCompatCheckBox {

    @Dimension(unit = Dimension.DP) private static final int DEFAULT_CIRCLE_WIDTH_DP = 2;
    private Paint mUnCheckedPaint;
    private Paint mCheckedPaint;

    @ColorInt private int mColor;

    @Px private int mCircleWidth;
    @Px private int mRadius;
    @Px private int mCenterX;
    @Px private int mCenterY;

    public RoundCheckBox(Context context) {
        super(context);
    }

    public RoundCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundCheckBox);

        int defColor = ContextCompat.getColor(context, android.R.color.white);
        try {
            mColor = array.getColor(R.styleable.RoundCheckBox_color, defColor);
            mCircleWidth = (int) array.getDimension(R.styleable.RoundCheckBox_circleWidth, 0);
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

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public int getCircleWidth() {
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

    private void init() {
        setClickable(false);
        mCircleWidth = mCircleWidth > 0 ? mCircleWidth : dpToPx(DEFAULT_CIRCLE_WIDTH_DP);
        mRadius = getHeight() / 2 - mCircleWidth;
        mCenterY = getHeight() / 2;
        mCenterX = getWidth() / 2;

        mCheckedPaint = new Paint();
        mCheckedPaint.setColor(mColor);
        mCheckedPaint.setStyle(Paint.Style.FILL);

        mUnCheckedPaint = new Paint();
        mUnCheckedPaint.setColor(mColor);
        mUnCheckedPaint.setStyle(Paint.Style.STROKE);
        mUnCheckedPaint.setStrokeWidth(mCircleWidth);
    }

    // onDraw() doesn't work on API24 without it
    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isChecked()) {
            canvas.drawCircle(mCenterX, mCenterY, mRadius, mCheckedPaint);
        } else {
            canvas.drawCircle(mCenterX, mCenterY, mRadius, mUnCheckedPaint);
        }
    }
}
