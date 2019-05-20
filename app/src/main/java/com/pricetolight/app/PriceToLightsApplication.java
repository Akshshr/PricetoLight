package com.pricetolight.app;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.pricetolight.BuildConfig;
import com.pricetolight.api.Api;
import com.pricetolight.app.base.ActivityLifeCycleBookKeeper;
import com.pricetolight.app.base.AppPreferences;
import com.pricetolight.app.base.UserManager;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTimeZone;

import java.util.List;

import io.fabric.sdk.android.Fabric;


public class PriceToLightsApplication extends Application {
    public AppPreferences appPreferences;
    private Api api;
    private UserManager userManager;
    public PHHueSDK phHueSDK;
    private ActivityLifeCycleBookKeeper activityLifeCycleBookKeeper;


    public static final String TAG = PriceToLightsApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        initThirdPartyLibraries();
        this.appPreferences = new AppPreferences(this);
        this.userManager = new UserManager(appPreferences);
        this.api = new Api(BuildConfig.API_HOST, userManager);
        setupActivityLifeCycleBookKeeper(userManager);
        Fabric.with(this, new Crashlytics());
    }

    public void setupPHSDK() {
        if (phHueSDK == null) {
            phHueSDK = PHHueSDK.create();
            // Register the PHSDKListener to receive callbacks from the bridge.
            phHueSDK.getNotificationManager().registerSDKListener(listener);

            // Set the Device Name (name of your app). This will be stored in your bridge whitelist entry.
            phHueSDK.setAppName("PriceToLights");
            phHueSDK.setDeviceName(android.os.Build.MODEL);
        }
        // Try to automatically connect to the last known bridge.  For first time use this will be empty so a bridge search is automatically started.
        if (isPHSdkConnected()) {
            lightsCameraAction();
        }
    }

    public Boolean isPHSdkConnected() {
        String lastIpAddress = getAppPreferences().getLastConnectedIPAddressHue().get();
        String lastUsername = getAppPreferences().getUserNameHue().get();
        // Automatically try to connect to the last connected IP Address.  For multiple bridge support a different implementation is required.
        if (lastIpAddress != null && !lastIpAddress.equals("") && lastUsername != null) {
            PHAccessPoint lastAccessPoint = new PHAccessPoint();
            lastAccessPoint.setIpAddress(lastIpAddress);
            lastAccessPoint.setUsername(lastUsername);

            if (!phHueSDK.isAccessPointConnected(lastAccessPoint)) {
//                Toast.makeText(getActivity(), "Error: AccessPointConnected", Toast.LENGTH_SHORT).show();
                phHueSDK.connect(lastAccessPoint);
            } else {
                return true;
            }
        } else {
            doBridgeSearch();
        }
        return false;
    }


    private void doBridgeSearch() {
        //TODO Update UI to indicate app is looking for the bridge...

        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        // Start the UPNP Searching of local bridges.
        sm.search(true, true);
    }


    private void lightsCameraAction() {

//        ((ConnectHueActivity) getActivity()).scrollToNextPage();
//        Toast.makeText(getActivity(), "lights camera action", Toast.LENGTH_SHORT).show();
    }

    private boolean lastSearchWasIPScan = false;
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
//            phBridge.getResourceCache().getAllLights()
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
                if (!phHueSDK.isAccessPointConnected(lastAccessPoint))
                phHueSDK.connect(lastAccessPoint);
            }

        }

        @Override
        public void onConnectionResumed(PHBridge phBridge) {
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


    private void initThirdPartyLibraries() {
        JodaTimeAndroid.init(this);
        DateTimeZone.setDefault(DateTimeZone.forID("Europe/Stockholm"));

    }

    private void setupActivityLifeCycleBookKeeper(UserManager userManager) {
        activityLifeCycleBookKeeper = new ActivityLifeCycleBookKeeper();
        activityLifeCycleBookKeeper.setLoggedIn(userManager.isLoggedIn());
        userManager.getTokenObservable().subscribe(token -> activityLifeCycleBookKeeper.setLoggedIn(userManager.isLoggedIn()));
    }

    public Api getApi() {
        return api;
    }

    public AppPreferences getAppPreferences() {
        return appPreferences;
    }

    public UserManager getUserManager() {
        return userManager;
    }

}
