package com.pricetolight.app.main;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pricetolight.R;
import com.pricetolight.api.modal.Licence;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.app.main.adapter.LicencesAdapter;
import com.pricetolight.databinding.ActivityLicencesBinding;

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

        binding.tibberDev.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(getResources().getString(R.string.tibberdev_url)));
            startActivity(browserIntent);
        });
        adapter = new LicencesAdapter(licences);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }



}
