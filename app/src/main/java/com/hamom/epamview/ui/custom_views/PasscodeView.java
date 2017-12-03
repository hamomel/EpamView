package com.hamom.epamview.ui.custom_views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.DrawableRes;
import android.support.annotation.Px;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hamom.epamview.BuildConfig;
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
    private String mUserInput = "55";

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
    @Px private int mCheckBoxHeight;
    @Px private int mButtonSize;
    private int mCheckBoxMargine;

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

        // Turn off hardware acceleration to let canvas.clipPath() work
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        // Prepare paddings
        mPaddingTop = dpToPx(24);
        mPaddingBottom = dpToPx(16);
        mPaddingHorizontal = dpToPx(16);
        mPaddingInner = dpToPx(16);
        mCheckBoxHeight = dpToPx(24);
        mButtonSize = dpToPx(72);
        mCheckBoxMargine = dpToPx(8);

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

    private void initCheckBoxes() {
        for (int i = 0; i < mPasscode.length(); i++) {
            RoundCheckBox checkBox = new RoundCheckBox(mCheckBoxLayout.getContext());
            checkBox.setColor(mMainColor);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mCheckBoxLayout.getLayoutParams());
            params.setMargins(mCheckBoxMargine, 0, mCheckBoxMargine, 0);
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
        int keyboardMaxWidth = width - mPaddingHorizontal * 2;
        int keyboardMaxHeight = height - heightUsed - mDelete.getMeasuredHeight() - mPaddingInner - mPaddingBottom;
        int keyMaxWidth = (keyboardMaxWidth - mPaddingInner * 2) / 3;
        int keyMaxHeight = (keyboardMaxHeight - mPaddingInner * 3) / 4;
        int keySize = Math.min(keyMaxHeight, keyMaxWidth);
        int keyboardRealWidth = keySize * 3 + mPaddingHorizontal * 2;
        int keyoardRealHeight = mButtonSize * 4 + mPaddingInner * 3;
        int keyTop = heightUsed + (keyboardMaxHeight - keyoardRealHeight) / 2;
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
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void onDeleteClick() {
        Toast.makeText(getContext(), "delete", Toast.LENGTH_SHORT).show();
    }
}
