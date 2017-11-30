package com.hamom.epamview.ui.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.hamom.epamview.R;

/**
 * Created by hamom on 30.11.17.
 */

public class RoundCheckBox extends android.support.v7.widget.AppCompatCheckBox {


    private static final int DEFAULT_CIRCLE_WIDTH_DP = 2;
    private Paint mUnCheckedPaint;
    private Paint mCheckedPaint;
    private int mColor;
    private float mCircleWidth;
    private float mRadius;
    private int mCenterX;
    private int mCenterY;

    public RoundCheckBox(Context context) {
        super(context);
    }

    public RoundCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundCheckBox);

        int defColor = ContextCompat.getColor(context, android.R.color.white);
        float circleWidth;
        try {
            mColor = array.getColor(R.styleable.RoundCheckBox_color, defColor);
            circleWidth = array.getDimension(R.styleable.RoundCheckBox_width, 0);
        } finally {
            array.recycle();
        }

        mCircleWidth = circleWidth > 0 ? circleWidth : DEFAULT_CIRCLE_WIDTH_DP * getScale(context);
    }

    private float getScale(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return metrics.density;
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

    private void init() {
        setClickable(false);
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

    @Override
    protected void onDraw(Canvas canvas) {

        if (isChecked()) {
            canvas.drawCircle(mCenterX, mCenterY, mRadius, mCheckedPaint);
        } else {
            canvas.drawCircle(mCenterX, mCenterY, mRadius, mUnCheckedPaint);
        }
    }
}
