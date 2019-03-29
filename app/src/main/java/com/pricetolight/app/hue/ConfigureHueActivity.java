package com.pricetolight.app.hue;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

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
import com.pricetolight.app.util.IntentKeys;
import com.pricetolight.databinding.ActivityConfigureHueBinding;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ConfigureHueActivity extends BaseActivity {

    public static final String TAG = ConfigureHueActivity.class.getSimpleName();
    private ActivityConfigureHueBinding binding;
    private HueLightsAdapter adapter;
    public static final int LIGHTS_RESULT = 1001;
    private PHLight choosenPHLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_configure_hue);
        binding.toolbar.setTitle("Configure hue light");

        PHHueSDK phHueSDK=PHHueSDK.getInstance();

        List<PHLight> lights;
        lights = phHueSDK.getAllBridges().get(0).getResourceCache().getAllLights();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new HueLightsAdapter(lights, true);
        binding.recyclerView.setAdapter(adapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.getPHLightbservable()
                .takeUntil(getLifecycleEvents(ActivityEvent.DESTROY))
                .subscribe(this::onLightSelect,
                        this::handleError);
    }

    @Override
    public void onBackPressed() {
        Intent evChargerIntent = new Intent();
        evChargerIntent.putExtra(IntentKeys.LIGHTS_RESULT, (Serializable) choosenPHLight);
        setResult(RESULT_OK, evChargerIntent);
        finish();
        super.onBackPressed();
    }


    private void onLightSelect(PHLight phLight) {
        this.choosenPHLight=phLight;

        binding.done.setVisibility(View.VISIBLE);
        binding.done.setOnClickListener(View -> onBackPressed());
    }



}
