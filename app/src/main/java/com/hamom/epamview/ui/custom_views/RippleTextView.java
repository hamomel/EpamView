package com.hamom.epamview.ui.custom_views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.hamom.epamview.R;

/**
 * Created by hamom on 29.11.17.
 */

public class RippleTextView extends android.support.v7.widget.AppCompatTextView {

    private Paint mPaint;
    private Paint mCirclePaint;
    private float mRadius;
    private float mRippleRadius;
    private ValueAnimator mAnimator;
    private ValueAnimator mRverseAnimator;

    public RippleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        mPaint.setStyle(Paint.Style.FILL);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(ContextCompat.getColor(context, android.R.color.black));
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(4);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float height = MeasureSpec.getSize(heightMeasureSpec);
        mRadius = height/2 - 2;
        mAnimator = ValueAnimator.ofFloat(0, mRadius);
        mAnimator.addUpdateListener(getListener());

        mRverseAnimator = ValueAnimator.ofFloat(mRippleRadius, 0);
        mRverseAnimator.setDuration(500);
        mRverseAnimator.addUpdateListener(getListener());
    }

    @NonNull
    private ValueAnimator.AnimatorUpdateListener getListener() {
        return new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRippleRadius = (float) animation.getAnimatedValue();
                Log.d("RippleTV in listener", "" + mRippleRadius);
                invalidate();
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getWidth()/2, getHeight()/2, mRippleRadius, mPaint);
        canvas.drawCircle(getWidth()/2, getHeight()/2, mRadius, mCirclePaint);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mAnimator.start();
                return true;
            case MotionEvent.ACTION_UP:
                Log.d("RippleTV :", "" + mRippleRadius);
                mAnimator.reverse();
//                if (mAnimator.isRunning()) {
//                } else {
//                    mRverseAnimator.start();
//                }
                return true;

        }
        return super.onTouchEvent(event);
    }
}
