package com.pricetolight.app.hue;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.google.gson.Gson;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.pricetolight.R;
import com.pricetolight.api.modal.AppHueLight;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.app.util.IntentKeys;
import com.pricetolight.databinding.ActivityConfigureHueBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConfigureHueActivity extends BaseActivity {

    public static final String TAG = ConfigureHueActivity.class.getSimpleName();
    private ActivityConfigureHueBinding binding;
    private ConfigureLightsAdapter adapter;
    public static final int LIGHTS_RESULT = 1001;
    private PHLight choosenPHLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_configure_hue);
        binding.toolbar.setTitle("Configure hue light");

        PHHueSDK phHueSDK=PHHueSDK.getInstance();

        List<PHLight> lights= new ArrayList<>();
        if (phHueSDK.getAllBridges().size()>0)
        lights = phHueSDK.getAllBridges().get(0).getResourceCache().getAllLights();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ConfigureLightsAdapter(lights, true);
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

        Gson gson = new Gson();
        String myJson = gson.toJson(choosenPHLight);

        Intent lightIntent = new Intent();
        lightIntent.putExtra("myjson", myJson);
        setResult(RESULT_OK, lightIntent);

//        Gson gson = new Gson();
//        String myJSONLight = gson.toJson(choosenPHLight);
//        Intent lightIntent = new Intent();
//        lightIntent.putExtra(IntentKeys.LIGHT_CHOSEN, myJSONLight);
//        setResult(RESULT_OK, lightIntent);
//        finish();
        super.onBackPressed();
    }


    private void onLightSelect(PHLight phLight) {
        this.choosenPHLight=phLight;
        binding.done.setVisibility(View.VISIBLE);
        binding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {

                onBackPressed();
//                float xy[] = PHUtilities.calculateXYFromRGB(Color.red(250), Color.green(0), Color.blue(0), choosenPHLight.getModelNumber());
//                PHLightState lightState = new PHLightState();
//                lightState.setX(xy[0]);
//                lightState.setY(xy[1]);
//
//                PHHueSDK phHueSDK =PHHueSDK.getInstance();
//                phHueSDK.getSelectedBridge().updateLightState(choosenPHLight, lightState);
//                scheduleJob();
            }
        });
    }



}
