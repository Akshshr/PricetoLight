package com.pricetolight.app.hue;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import com.google.gson.Gson;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHLight;
import com.pricetolight.R;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.databinding.ActivityConfigureHueBinding;

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

        PHHueSDK phHueSDK = PHHueSDK.getInstance();

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

        super.onBackPressed();
    }


    private void onLightSelect(PHLight phLight) {
        this.choosenPHLight=phLight;
        binding.done.setVisibility(View.VISIBLE);
        binding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                onBackPressed();
            }
        });
    }



}
