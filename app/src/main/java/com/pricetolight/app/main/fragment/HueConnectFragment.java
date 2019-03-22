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

    private PHHueSDK phHueSDK;
    ArrayList<PHGroup> phGroups = new ArrayList<>();
    List<PHLight> allLights = new ArrayList<>();
    private boolean lastSearchWasIPScan = false;

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

    private void lightsCameraAction() {
        ((ConnectHueActivity) getActivity()).scrollToNextPage();
        Toast.makeText(getActivity(), "lights camera action", Toast.LENGTH_SHORT).show();
    }

    private void doBridgeSearch() {
        //TODO Update UI to indicate app is looking for the bridge...

        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        // Start the UPNP Searching of local bridges.
        sm.search(true, true);
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
        binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_hue_connect, container, false);

        phHueSDK = PHHueSDK.create();

        // Register the PHSDKListener to receive callbacks from the bridge.
        phHueSDK.getNotificationManager().registerSDKListener(listener);

        // Set the Device Name (name of your app). This will be stored in your bridge whitelist entry.
        phHueSDK.setAppName("PriceToLights");
        phHueSDK.setDeviceName(android.os.Build.MODEL);

        // Try to automatically connect to the last known bridge.  For first time use this will be empty so a bridge search is automatically started.
        String lastIpAddress = getAppPreferences().getLastConnectedIPAddressHue().get();
        String lastUsername = getAppPreferences().getUserNameHue().get();

        // Automatically try to connect to the last connected IP Address.  For multiple bridge support a different implementation is required.
        if (lastIpAddress != null && !lastIpAddress.equals("")) {
            PHAccessPoint lastAccessPoint = new PHAccessPoint();
            lastAccessPoint.setIpAddress(lastIpAddress);
            lastAccessPoint.setUsername(lastUsername);

            if (!phHueSDK.isAccessPointConnected(lastAccessPoint)) {
                Toast.makeText(getActivity(), "Error: AccessPointConnected", Toast.LENGTH_SHORT).show();
                phHueSDK.connect(lastAccessPoint);
            } else {
                lightsCameraAction();
            }
        } else {  // First time use, so perform a bridge search.
            doBridgeSearch();
        }
        return binding.getRoot();
    }



    private PHSDKListener listener = new PHSDKListener() {
        @Override
        public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {
            Log.w(TAG, "Philips Hue " + "Philips Hue" + "On CacheUpdated");
        }

        @Override
        public void onBridgeConnected(PHBridge phBridge, String username) {

            phHueSDK.setSelectedBridge(phBridge);
            phHueSDK.enableHeartbeat(phBridge, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(phBridge.getResourceCache().getBridgeConfiguration().getIpAddress(), System.currentTimeMillis());
            getAppPreferences().setLastConnectedIPAddressHue(phBridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            getAppPreferences().setUserNameHue(username);

            lightsCameraAction();
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {
            Log.w(TAG, "Philips Hue " + " Authentication Required.");
            phHueSDK.startPushlinkAuthentication(phAccessPoint);
//            startActivity(new Intent(SmartLightsActivity.this, PHPushlinkActivity.class));
        }

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
            Log.w(TAG, "Philips Hue " + " Access Points Found. " + accessPoint.size());

            if (accessPoint != null && accessPoint.size() > 0) {
                phHueSDK.getAccessPointsFound().clear();
                phHueSDK.getAccessPointsFound().addAll(accessPoint);
                getAppPreferences().setUserNameHue(accessPoint.get(0).getUsername());
                getAppPreferences().setLastConnectedIPAddressHue(accessPoint.get(0).getIpAddress());
                PHAccessPoint lastAccessPoint = new PHAccessPoint();
                lastAccessPoint.setIpAddress(accessPoint.get(0).getIpAddress());
                lastAccessPoint.setUsername(accessPoint.get(0).getUsername());
                phHueSDK.connect(lastAccessPoint);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //TODO Update adapter add access point
                        //phHueSDK.getAccessPointsFound()

                        //adapter.updateData(phHueSDK.getAccessPointsFound());
                    }
                });

            }

        }

        @Override
        public void onConnectionResumed(PHBridge phBridge) {
            if (getActivity().isFinishing())
                return;

            Log.v(TAG, "Philips Hue " + "onConnectionResumed" + phBridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            phHueSDK.getLastHeartbeat().put(phBridge.getResourceCache().getBridgeConfiguration().getIpAddress(), System.currentTimeMillis());
            for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++) {

                if (phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress().equals(phBridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                    phHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }

        }

        @Override
        public void onConnectionLost(PHAccessPoint phAccessPoint) {
            Log.v(TAG, "Philips Hue " + "onConnectionLost : " + phAccessPoint.getIpAddress());
            if (!phHueSDK.getDisconnectedAccessPoint().contains(phAccessPoint)) {
                phHueSDK.getDisconnectedAccessPoint().add(phAccessPoint);
            }
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> list) {
            for (PHHueParsingError parsingError : list) {
                Log.e(TAG, "Philips Hue " + "ParsingError : " + parsingError.getMessage());
            }
        }

        @Override
        public void onError(int code, final String message) {
            if (code == PHHueError.NO_CONNECTION) {
            } else if (code == PHHueError.AUTHENTICATION_FAILED || code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
            } else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
            } else if (code == PHMessageType.BRIDGE_NOT_FOUND) {

                if (!lastSearchWasIPScan) {
                    //TODO:  Perform an IP Scan (backup mechanism) if UPNP and Portal Search fails.
                    phHueSDK = PHHueSDK.getInstance();
                    PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                    sm.search(false, false, true);
                    lastSearchWasIPScan = true;
                } else {
                    //BRIDGE NOT FOUND HERE...
                }


            }
        }

    };


}
