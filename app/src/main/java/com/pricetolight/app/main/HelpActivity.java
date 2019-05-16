package com.pricetolight.app.main;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.pricetolight.R;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.databinding.ActivityHelpBinding;

public class HelpActivity extends BaseActivity {

    private ActivityHelpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_help);

        binding.toolbar.setTitle(getResources().getString(R.string.more_about));

        binding.coffeeCta.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(getResources().getString(R.string.bmc_link)));
            startActivity(browserIntent);
        });
        binding.bugsCta.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(getResources().getString(R.string.bugs_link)));
            startActivity(browserIntent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
