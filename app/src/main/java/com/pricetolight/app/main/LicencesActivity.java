package com.pricetolight.app.main;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ArrayAdapter;

import com.pricetolight.R;
import com.pricetolight.api.modal.Licence;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.app.main.adapter.LicencesAdapter;
import com.pricetolight.databinding.ActivityLicencesBinding;

import java.util.ArrayList;
import java.util.List;

public class LicencesActivity extends BaseActivity {

    private ActivityLicencesBinding binding;
    private LicencesAdapter adapter;
    private final List<Licence> licences= Licence.createLicences();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_licences);
        binding.toolbar.setTitle(getResources().getString(R.string.licence_title));

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new LicencesAdapter(licences);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }



}
