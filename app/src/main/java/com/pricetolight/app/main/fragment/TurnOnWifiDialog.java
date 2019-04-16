package com.pricetolight.app.main.fragment;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pricetolight.R;
import com.pricetolight.databinding.FragmentTurnOnWifiDialogBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class TurnOnWifiDialog extends DialogFragment {

    private FragmentTurnOnWifiDialogBinding binding;
    public static final String TAG = TurnOnWifiDialog.class.getSimpleName();

    public TurnOnWifiDialog() {
        // Required empty public constructor
    }



    public static TurnOnWifiDialog newInstance() {
        TurnOnWifiDialog fragment = new TurnOnWifiDialog();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_turn_on_wifi_dialog, container, false);

        binding.openSettings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        return binding.getRoot();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getDialog()!=null && getDialog().getWindow()!=null) {
            int width = getResources().getDimensionPixelSize(R.dimen.dialog_width);
            int height = getResources().getDimensionPixelSize(R.dimen.dialog_height);
            getDialog().getWindow().setLayout(width, height);
        }
    }

}
