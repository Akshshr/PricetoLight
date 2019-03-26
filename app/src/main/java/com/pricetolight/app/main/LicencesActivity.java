package com.pricetolight.app.main;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ArrayAdapter;

import com.pricetolight.R;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.databinding.ActivityLicencesBinding;

import java.util.ArrayList;
import java.util.List;

public class LicencesActivity extends BaseActivity {

    private ActivityLicencesBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_licences);
        binding.toolbar.setTitle("Open source licences");

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, R.layout.row_category_spinner,getDependencies());

//        binding.recyclerView.setAdapter(categoryAdapter);
    }

    private List<String> getDependencies() {

        List<String> dependencies = new ArrayList<>();

        dependencies.add("a");
        dependencies.add("a");
        dependencies.add("a");
        dependencies.add("a");
        dependencies.add("a");

        return dependencies;

    }

    @Override
    protected void onStart() {
        super.onStart();

    }



}
