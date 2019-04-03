package com.pricetolight.app.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.pricetolight.R;
import com.pricetolight.app.PriceToLightsApplication;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.app.main.adapter.HueConnectFragmentAdapter;
import com.pricetolight.app.main.fragment.HueConnectFragment;
import com.pricetolight.app.main.fragment.HuePairResultFragment;
import com.pricetolight.databinding.ActivityConnectHueBinding;

import java.util.ArrayList;
import java.util.List;

import rx.subjects.PublishSubject;

public class ConnectHueActivity extends BaseActivity implements HuePairResultFragment.OnHuePairResultListener,PHSDKListener {

    private static final String TAG = ConnectHueActivity.class.getSimpleName();

    private ActivityConnectHueBinding binding;
    HueConnectFragmentAdapter hueConnectFragmentAdapter;

    HueConnectFragment hueConnectFragment;
    HuePairResultFragment huePairResultFragment;
    PHHueSDK phHueSDK;

    ArrayList<PHGroup> phGroups = new ArrayList<>();
    List<PHLight> allLights = new ArrayList<>();

    private PublishSubject<List<PHLight>> allLightsPublishSubject = PublishSubject.create();


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

    public PublishSubject<List<PHLight>> getAllLightsPublishSubject() {
        return allLightsPublishSubject;
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
        ((PriceToLightsApplication)getApplication()).setupPHSDK();
        phHueSDK = PHHueSDK.getInstance();
        phHueSDK.getNotificationManager().registerSDKListener(this);
        if (((PriceToLightsApplication)getApplication()).isPHSdkConnected()){
            scrollToNextPage();
        }
    }

    public PHHueSDK getHueSDK(){
        return PHHueSDK.getInstance();

    }    @Override
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
        Log.d(TAG, "onBridgeConnected: ");
    }

    @Override
    public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {
        Log.d(TAG, "onBridgeConnected: ");

    }

    @Override
    public void onBridgeConnected(PHBridge phBridge, String s) {
        Log.d(TAG, "onBridgeConnected: ");
//        setAllLights(phBridge.getResourceCache().getAllLights());
        getAllLightsPublishSubject().onNext(phBridge.getResourceCache().getAllLights());
        scrollToNextPage();
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {
        Log.d(TAG, "onBridgeConnected: ");

    }

    @Override
    public void onAccessPointsFound(List<PHAccessPoint> list) {
        Log.d(TAG, "onBridgeConnected: ");

        if (list != null && list.size() > 0) {
            phHueSDK.getAccessPointsFound().clear();
            phHueSDK.getAccessPointsFound().addAll(list);
            getAppPreferences().setUserNameHue(list.get(0).getUsername());
            getAppPreferences().setLastConnectedIPAddressHue(list.get(0).getIpAddress());
            PHAccessPoint lastAccessPoint = new PHAccessPoint();
            lastAccessPoint.setIpAddress(list.get(0).getIpAddress());
            lastAccessPoint.setUsername(list.get(0).getUsername());
            phHueSDK.connect(lastAccessPoint);

        }

    }

    @Override
    public void onError(int i, String s) {
        Log.d(TAG, "onError: ");
    }

    @Override
    public void onConnectionResumed(PHBridge phBridge) {
        Log.d(TAG, "onConnectionResumed: ");
    }

    @Override
    public void onConnectionLost(PHAccessPoint phAccessPoint) {
        Log.d(TAG, "onConnectionLost: ");
    }

    @Override
    public void onParsingErrors(List<PHHueParsingError> list) {
        Log.d(TAG, "onBridgeConnected: ");

    }
}
