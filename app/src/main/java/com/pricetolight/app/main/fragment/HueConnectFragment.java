package com.pricetolight.app.main.fragment;


import android.animation.ValueAnimator;
import android.databinding.DataBindingUtil;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.pricetolight.R;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.app.base.BaseFragment;
import com.pricetolight.app.main.ConnectHueActivity;
import com.pricetolight.databinding.FragmentHueConnectBinding;

import java.util.ArrayList;
import java.util.List;


public class HueConnectFragment extends BaseFragment {

    public static final String TAG = HueConnectFragment.class.getSimpleName();

    ArrayList<PHGroup> phGroups = new ArrayList<>();
    List<PHLight> allLights = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";

    private FragmentHueConnectBinding binding;

    private String mParam1;

    public HueConnectFragment() {
        // Required empty public constructor
    }

    public static HueConnectFragment newInstance(String param1) {
        HueConnectFragment fragment = new HueConnectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                binding.hueButtonBlue.setAlpha((Float) animation.getAnimatedValue());
                binding.searchingTitle.setAlpha((Float) animation.getAnimatedValue());
            }
        });

        animator.setDuration(1000);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(-1);
        animator.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hue_connect, container, false);


        return binding.getRoot();
    }





}
