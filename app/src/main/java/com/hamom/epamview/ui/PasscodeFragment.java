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
import com.hamom.epamview.ui.custom_views.PasscodeView;
import com.hamom.epamview.ui.custom_views.RoundCheckBox;

/**
 * Created by hamom on 30.11.17.
 */

public class PasscodeFragment extends Fragment {

    private PasscodeView mPasscodeView;

    public static PasscodeFragment getInstance() {
        return new PasscodeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pathcode, container, false);
        mPasscodeView = view.findViewById(R.id.passcode_view);
        mPasscodeView.setPasscode(1234);

        return view;
    }

}
