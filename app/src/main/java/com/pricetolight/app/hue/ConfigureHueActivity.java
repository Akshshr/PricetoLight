package com.pricetolight.app.hue;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.philips.lighting.annotations.Bridge;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.model.PHScene;
import com.pricetolight.R;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.databinding.ActivityConfigureHueBinding;

import java.util.List;
import java.util.Map;

public class ConfigureHueActivity extends BaseActivity {

    public static final String TAG = ConfigureHueActivity.class.getSimpleName();
    private ActivityConfigureHueBinding binding;
    private HueLightsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_configure_hue);
        binding.toolbar.setTitle("Configure hue light");

        PHHueSDK phHueSDK=PHHueSDK.getInstance();

        List<PHLight> lights;
//        List<PHLight> lights = phHueSDK.getAllBridges().get(0).getResourceCache().getAllLights();
        lights = phHueSDK.getAllBridges().get(0).getResourceCache().getAllLights();
        PHLight light=lights.get(5);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new HueLightsAdapter(lights);
        binding.recyclerView.setAdapter(adapter);


//        PHLightState phLightState= light.getLastKnownLightState();
//        PHLightState phLightState= new PHLightState();
        PHLightState lightState = new PHLightState();
//        phLightState.setHue(200);

        lightState.setBrightness(50, true);
//        light.setLastKnownLightState(phLightState);

//        phHueSDK.getSelectedBridge().updateLightState(light, lightState,  null);
        phHueSDK.getSelectedBridge().updateLightState(light, lightState,  new PHLightListener() {
            @Override
            public void onReceivingLightDetails(PHLight phLight) {
                Log.d(TAG, "onReceivingLightDetails: ");
            }

            @Override
            public void onReceivingLights(List<PHBridgeResource> list) {
                Log.d(TAG, "onReceivingLights: ");
            }

            @Override
            public void onSearchComplete() {
                Log.d(TAG, "onSearchComplete: ");
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "onError: ");
            }

            @Override
            public void onStateUpdate(Map<String, String> map, List<PHHueError> list) {
                Log.d(TAG, "onStateUpdate: ");
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.getPHLightbservable()
                .takeUntil(getLifecycleEvents(ActivityEvent.DESTROY))
                .subscribe(this::onLightSelect,
                        this::handleError);
    }

    private void onLightSelect(PHLight phLight) {

    }



}
