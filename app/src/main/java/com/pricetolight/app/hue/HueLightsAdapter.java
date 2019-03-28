package com.pricetolight.app.hue;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.philips.lighting.model.PHLight;
import com.pricetolight.R;
import com.pricetolight.databinding.RowHueBinding;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

public class HueLightsAdapter extends RecyclerView.Adapter<HueLightsAdapter.ViewHolder> {

    private List<PHLight> phLights;

    private PublishSubject<PHLight> lightClickSubject = PublishSubject.create();

    public HueLightsAdapter(List<PHLight> phLights) {
        this.phLights = phLights;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RowHueBinding binding;

        public ViewHolder(RowHueBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bind(PHLight light) {
            final Context context = binding.getRoot().getContext();
            binding.groumName.setText(light.getName());
            binding.chip.setText(light.getLightType().toString());
            binding.hueProperty.setText(light.getLastKnownLightState().isOn() ? "ON" : "OFF");

            if(!light.supportsColor()) {
                binding.background.setAlpha(0.5f);
                binding.getRoot().setOnClickListener(v -> Toast.makeText(context, "None colored lamps are not supported", Toast.LENGTH_SHORT).show());
            }else{
                binding.getRoot().setOnClickListener(v -> Toast.makeText(context, "u picke this light", Toast.LENGTH_SHORT).show());
            }
        }

    }

    @Override
    public HueLightsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new HueLightsAdapter.ViewHolder(DataBindingUtil
                .inflate(layoutInflater, R.layout.row_hue, parent, false));
    }

    @Override
    public void onBindViewHolder(HueLightsAdapter.ViewHolder holder, int position) {
        holder.bind(phLights.get(position));
    }

    @Override
    public int getItemCount() {
        return phLights.size();

    }


    public Observable<PHLight> getPHLightbservable() {
        return lightClickSubject.asObservable();
    }

}
