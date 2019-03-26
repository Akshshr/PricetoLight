package com.pricetolight.app.hue;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pricetolight.R;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.databinding.ActivityConfigureHueBinding;

public class ConfigureHueActivity extends BaseActivity {

    private ActivityConfigureHueBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_configure_hue);
        binding.toolbar.setTitle("Configure hue light");

    }
}
