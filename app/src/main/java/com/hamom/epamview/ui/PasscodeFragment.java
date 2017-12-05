package com.hamom.epamview.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hamom.epamview.R;
import com.hamom.epamview.ui.custom_views.PasscodeView;
import com.hamom.epamview.ui.custom_views.SecondActivity;

/**
 * Created by hamom on 30.11.17.
 */

public class PasscodeFragment extends Fragment {

    public static PasscodeFragment getInstance() {
        return new PasscodeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pathcode, container, false);
        PasscodeView passcodeView = view.findViewById(R.id.passcode_view);
        passcodeView.setPasscode(1234);
        passcodeView.setCallback(() -> showNextScreen());
        passcodeView.setUncheckedDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.linkedin));
        passcodeView.setCheckedDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.vk));

        return view;
    }

    private void showNextScreen() {
        Intent intent = new Intent(getActivity(), SecondActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}
