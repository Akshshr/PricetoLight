package com.pricetolight.app.hue;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.philips.lighting.model.PHLight;
import com.pricetolight.R;
import com.pricetolight.databinding.RowHueBinding;

import java.util.ArrayList;

public class HueLightsAdapter extends RecyclerView.Adapter<HueLightsAdapter.ViewHolder> {

    private ArrayList<PHLight> phLights;

    public HueLightsAdapter(ArrayList<PHLight> phLights) {
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

}
