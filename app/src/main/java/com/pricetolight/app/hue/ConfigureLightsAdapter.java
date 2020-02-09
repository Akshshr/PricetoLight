package com.pricetolight.app.hue;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.philips.lighting.model.PHLight;
import com.pricetolight.R;
import com.pricetolight.databinding.RowHueBinding;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

public class ConfigureLightsAdapter extends RecyclerView.Adapter<ConfigureLightsAdapter.ViewHolder> {

    private final Boolean clickable;
    private List<PHLight> phLights;

    private PublishSubject<PHLight> lightClickSubject = PublishSubject.create();

    public ConfigureLightsAdapter(List<PHLight> phLights, Boolean clickable) {
        this.phLights = phLights;
        this.clickable = clickable;
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
            binding.hueProperty.setText(light.getLastKnownLightState().isOn() ? context.getResources().getString(R.string.on) : context.getResources().getString(R.string.off));

            if (!light.supportsColor()) {
                binding.background.setAlpha(0.5f);
                binding.getRoot().setOnClickListener(v -> Toast.makeText(context, context.getResources().getString(R.string.toast_color_not_supported), Toast.LENGTH_SHORT).show());
            } else {
                binding.background.setImageDrawable(context.getDrawable(R.drawable.bg_hue_row_color));
                binding.getRoot().setOnClickListener(v -> {
                    binding.checkmark.setVisibility(View.VISIBLE);
                    binding.background.setAlpha(0.4f);
                    lightClickSubject.onNext(light);
                });
            }
        }
    }

    @NonNull
    @Override
    public ConfigureLightsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ConfigureLightsAdapter.ViewHolder(DataBindingUtil
                .inflate(layoutInflater, R.layout.row_hue, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConfigureLightsAdapter.ViewHolder holder, int position) {
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
