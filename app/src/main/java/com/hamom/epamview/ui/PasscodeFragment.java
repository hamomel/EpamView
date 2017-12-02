package com.hamom.epamview.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hamom.epamview.R;
import com.hamom.epamview.ui.custom_views.RoundCheckBox;

/**
 * Created by hamom on 30.11.17.
 */

public class PasscodeFragment extends Fragment implements View.OnClickListener{

    private char[] mPasscode = new char[] {1,2,3,4};
    private char[] mUserInput;

    private LinearLayout mCheckBoxLayout;
    private TextView mErrorTV;

    public static PasscodeFragment getInstance() {
        return new PasscodeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pathcode, container, false);
        mCheckBoxLayout = view.findViewById(R.id.checkbox_layout);
        mErrorTV = view.findViewById(R.id.error_tv);
        initView();
        return view;
    }

    private void initView() {
        for (int i = 0; i < mPasscode.length; i++) {
            mCheckBoxLayout.addView(getCheckBox());
        }
    }

    private View getCheckBox() {
        float scale = getScale(getActivity());
        int size = (int) (24 * scale);
        RoundCheckBox checkBox = new RoundCheckBox(getActivity());

        checkBox.setHeight(size);
        checkBox.setWidth(size);
        return checkBox;
    }

    private float getScale(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return metrics.density;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.delete) {
            deleteUserInput();
        } else {
            addUserInput(((TextView) v).getText());
        }
    }

    private void deleteUserInput() {

    }

    private void addUserInput(CharSequence text) {

    }
}
