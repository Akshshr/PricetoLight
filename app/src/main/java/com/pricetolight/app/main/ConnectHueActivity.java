package com.pricetolight.app.main;

import android.animation.ValueAnimator;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.pricetolight.app.MainActivity;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.app.main.adapter.HueConnectFragmentAdapter;
import com.pricetolight.app.main.fragment.HueConnectFragment;
import com.pricetolight.app.main.fragment.HuePairResultFragment;
import com.pricetolight.databinding.ActivityConnectHueBinding;

import java.util.ArrayList;
import java.util.List;

public class ConnectHueActivity extends BaseActivity implements HuePairResultFragment.OnHuePairResultListener {

    private static final String TAG = ConnectHueActivity.class.getSimpleName();

    private ActivityConnectHueBinding binding;
    HueConnectFragmentAdapter hueConnectFragmentAdapter;

    HueConnectFragment hueConnectFragment;
    HuePairResultFragment huePairResultFragment;

    ArrayList<PHGroup> phGroups = new ArrayList<>();
    List<PHLight> allLights = new ArrayList<>();

    public ArrayList<PHGroup> getPhGroups() {
        return phGroups;
    }

    public void setPhGroups(ArrayList<PHGroup> phGroups) {
        this.phGroups = phGroups;
    }

    public List<PHLight> getAllLights() {
        return allLights;
    }

    public void setAllLights(List<PHLight> allLights) {
        this.allLights = allLights;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_connect_hue);

        hueConnectFragmentAdapter = new HueConnectFragmentAdapter(getFragmentManager());
        hueConnectFragment = new HueConnectFragment();
        huePairResultFragment  = new HuePairResultFragment();

        hueConnectFragmentAdapter.addFragment(hueConnectFragment, HueConnectFragment.TAG); //Hue connect fragment
        hueConnectFragmentAdapter.addFragment(huePairResultFragment, HuePairResultFragment.TAG); //Hue result fragment

        binding.viewpager.setAdapter(hueConnectFragmentAdapter);
        binding.viewpager.setOnTouchListener((v, event) -> true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void scrollToNextPage() {
        binding.viewpager.setCurrentItem(binding.viewpager.getCurrentItem() + 1);
    }

    public void scrollToPreviousPage() {
        binding.viewpager.setCurrentItem(binding.viewpager.getCurrentItem() - 1);
    }

    @Override
    public void onHuePairResultInteraction(boolean isSuccessful) {
        //do something here after pair result......
    }
}
