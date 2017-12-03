package com.hamom.epamview.ui.custom_views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.Px;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hamom.epamview.R;
import com.hamom.epamview.utils.ConstantManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamom on 02.12.17.
 */

public class PasscodeView extends ViewGroup {
    private static String TAG = ConstantManager.TAG_PREFIX + "PasscodeView: ";
    private String mPasscode = "";
    private StringBuffer mUserInput = new StringBuffer("");
    private PasscodeCallback mCallback;
    private Vibrator mVibrator;

    private TextView mTitle;
    private TextView mError;
    private TextView mDelete;
    private LinearLayout mCheckBoxLayout;
    private RippleTextView[] mButtons = new RippleTextView[10];
    private List<RoundCheckBox> mCheckBoxes = new ArrayList<>();

    @ColorInt private int mMainColor;
    @ColorInt private int mErrorColor;
    @ColorInt private int mRippleColor;

    @Px private int mPaddingTop;
    @Px private int mPaddingBottom;
    @Px private int mPaddingHorizontal;
    @Px private int mPaddingInner;
    @Px private int mButtonSize;
    @Px private int mCheckBoxMargin;

    public PasscodeView(Context context) {
        super(context);
        resolveAttrs(context, null);
        init(context);
    }

    public PasscodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(context, attrs);
        init(context);
    }

    public PasscodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveAttrs(context, attrs);
        init(context);
    }

    private void resolveAttrs(Context context, AttributeSet attrs) {
        int defColor = ContextCompat.getColor(context, android.R.color.white);
        int defErrorColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);

        if (attrs == null) {
            mMainColor = defColor;
            mRippleColor = defColor;
            mErrorColor = defErrorColor;
        } else {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PasscodeView);

            try {
                mMainColor = array.getColor(R.styleable.RippleTextView_mainColor, defColor);
                mRippleColor = array.getColor(R.styleable.RippleTextView_rippleColor, defColor);
                mErrorColor = array.getColor(R.styleable.PasscodeView_errorColor, defErrorColor);
            } finally {
                array.recycle();
            }
        }
    }

    private void init(Context context) {
        if (getBackground() == null) {
            int color = ContextCompat.getColor(context, android.R.color.background_dark);
            setBackgroundColor(color);
        }

        mVibrator = ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));

        // Prepare paddings
        mPaddingTop = dpToPx(24);
        mPaddingBottom = dpToPx(16);
        mPaddingHorizontal = dpToPx(16);
        mPaddingInner = dpToPx(16);
        mButtonSize = dpToPx(72);
        mCheckBoxMargin = dpToPx(8);

        // Init views
        mTitle = new TextView(context);
        mTitle.setText(R.string.enter_passcode_title);
        mTitle.setTextColor(mMainColor);
        mTitle.setTextSize(20);
        addView(mTitle);

        mError = new TextView(context);
        mError.setText(R.string.wrong_passcode_message);
        mError.setTextColor(mErrorColor);
        mError.setTextSize(16);
        mError.setVisibility(INVISIBLE);
        addView(mError);

        mDelete = new TextView(context);
        mDelete.setText(R.string.delete);
        mDelete.setTextColor(mMainColor);
        mDelete.setTextSize(18);
        mDelete.setBackgroundResource(R.drawable.button_background);
        mDelete.setHapticFeedbackEnabled(true);
        mDelete.setOnClickListener(v -> onDeleteClick());
        addView(mDelete);

        mCheckBoxLayout = new LinearLayout(context);
        mCheckBoxLayout.setGravity(Gravity.CENTER);
        mCheckBoxLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(mCheckBoxLayout);

        for (int i = 0; i < 10; i++) {
            addButton(context, i);
        }
    }

    private void addButton(Context context, int number) {
        RippleTextView button = new RippleTextView(context);
        button.setText(String.valueOf(number));
        button.setMainColor(mMainColor);
        button.setRippleColor(mRippleColor);
        button.setOnClickListener(v -> onButtonClick(((TextView) v).getText()));
        addView(button);
        mButtons[number] = button;
    }


    public void setPasscode(int passcode) {
        mPasscode = String.valueOf(passcode);
        if (mPasscode.length() > 6) {
            throw new IllegalArgumentException("Passcode length mustn't be greater then 8 digits. Now it's: " + mPasscode.length());
        }
        initCheckBoxes();
    }

    public void setCallback(PasscodeCallback callback) {
        mCallback = callback;
    }

    private void initCheckBoxes() {
        for (int i = 0; i < mPasscode.length(); i++) {
            RoundCheckBox checkBox = new RoundCheckBox(mCheckBoxLayout.getContext());
            checkBox.setColor(mMainColor);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mCheckBoxLayout.getLayoutParams());
            params.setMargins(mCheckBoxMargin, 0, mCheckBoxMargin, 0);
            checkBox.setLayoutParams(params);
            mCheckBoxLayout.addView(checkBox);
            mCheckBoxes.add(checkBox);
        }

        checkCheckBoxes();
    }

    // Set checkboxes checked depend on number of char in user input
    private void checkCheckBoxes() {
        for (RoundCheckBox checkBox : mCheckBoxes) {
            if (mUserInput.length() > 0) {
                boolean checked = mCheckBoxes.indexOf(checkBox) < mUserInput.length();
                checkBox.setChecked(checked);
            } else {
                checkBox.setChecked(false);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int specWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        int maxWidth = specWidth + mPaddingHorizontal * 2;

        mTitle.setMaxWidth(maxWidth);
        mError.setMaxWidth(maxWidth);
        mDelete.setMaxWidth(maxWidth);

        measureChild(mTitle, specWidth, heightMeasureSpec);
        measureChild(mError, specWidth, heightMeasureSpec);
        measureChild(mDelete, specWidth, heightMeasureSpec);
        measureChild(mCheckBoxLayout, maxWidth, heightMeasureSpec);

        for (RippleTextView button : mButtons) {
            button.setWidth(mButtonSize);
            button.setHeight(mButtonSize);
            measureChild(button, specWidth, heightMeasureSpec);
        }

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(specWidth, specWidthMode);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(specHeight, specHeightMode);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = right - left;
        final int height = bottom - top;
        final int centerHorizontal = width / 2;

        // Counter for the used height
        int heightUsed = mPaddingTop;

        // Layout views one by one
        mTitle.layout(
                centerHorizontal - mTitle.getMeasuredWidth() / 2,
                heightUsed,
                centerHorizontal + mTitle.getMeasuredWidth() / 2,
                heightUsed + mTitle.getMeasuredHeight()
        );

        heightUsed += mTitle.getMeasuredHeight() + mPaddingInner;

        mCheckBoxLayout.layout(
                mPaddingHorizontal,
                heightUsed,
                width - mPaddingHorizontal,
                mCheckBoxLayout.getMeasuredHeight() + heightUsed
        );

        heightUsed += mCheckBoxLayout.getMeasuredHeight() + mPaddingHorizontal;

        mError.layout(
                centerHorizontal - mError.getMeasuredWidth() / 2,
                heightUsed,
                centerHorizontal + mError.getMeasuredWidth() / 2,
                heightUsed + mError.getMeasuredHeight()
        );

        heightUsed += mError.getMeasuredHeight() + mPaddingInner;

        mDelete.layout(
                width - mDelete.getMeasuredWidth() - mPaddingHorizontal,
                height - mDelete.getMeasuredHeight() - mPaddingBottom,
                width - mPaddingHorizontal,
                height - mPaddingBottom
        );


        // Layout keyboard
        int keyboardMaxHeight = height - heightUsed - mDelete.getMeasuredHeight() - mPaddingInner - mPaddingBottom;
        int keyboardRealHeight = mButtonSize * 4 + mPaddingInner * 3;
        int keyTop = heightUsed + (keyboardMaxHeight - keyboardRealHeight) / 2;
        int keyboardLeft = centerHorizontal - mButtonSize - mPaddingInner - mButtonSize / 2;
        int keyLeft = keyboardLeft;

        for (int i = 0, j = 1; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                RippleTextView button = mButtons[j];
                button.layout(
                        keyLeft,
                        keyTop,
                        keyLeft + button.getMeasuredWidth(),
                        keyTop + button.getMeasuredHeight()
                );
                keyLeft += button.getMeasuredWidth() + mPaddingInner;
                j++;
            }
            keyLeft = keyboardLeft;
            keyTop += mButtonSize + mPaddingInner;
        }

        mButtons[0].layout(
                centerHorizontal - mButtonSize / 2,
                keyTop,
                centerHorizontal + mButtonSize / 2,
                keyTop + mButtonSize
        );
    }

    @Px
    private int dpToPx(@Dimension(unit = Dimension.DP) int dp) {
        final Resources resources = getResources();
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    private void onButtonClick(CharSequence text) {
        mVibrator.vibrate(40);
        hideError();
        if (mUserInput.length() < mPasscode.length()) {
            mUserInput.append(text);
        }

        checkCheckBoxes();
        if (mUserInput.length() == mPasscode.length()) {
            checkPasscode();
        }
    }

    private void checkPasscode() {
       if (mUserInput.toString().equals(mPasscode)){
           mCallback.onCorrectPasscode();
       } else {
           mUserInput.delete(0, mUserInput.length());
           showError();
       }
    }

    private void showError() {
        mError.setVisibility(VISIBLE);
        showErrorAnimation();
    }

    private void showErrorAnimation() {
        mVibrator.vibrate(100);
        new Handler().postDelayed(() -> mVibrator.vibrate(100), 100);

        float x = mCheckBoxLayout.getX();
        float[] values = new float[] {x, x - dpToPx(6), x + dpToPx(12), x - dpToPx(6), x};
        ObjectAnimator animator = ObjectAnimator.ofFloat(mCheckBoxLayout, "x", values);
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                checkCheckBoxes();
            }
        });
        animator.start();
    }

    private void hideError() {
        mError.setVisibility(INVISIBLE);
    }

    private void onDeleteClick() {
        mVibrator.vibrate(40);
        if (mUserInput.length() > 0) {
            mUserInput.deleteCharAt(mUserInput.length() - 1);
            checkCheckBoxes();
        }
    }

    public interface PasscodeCallback {
        void onCorrectPasscode();
    }
}
