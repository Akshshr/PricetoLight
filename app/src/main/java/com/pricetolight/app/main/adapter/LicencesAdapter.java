package com.pricetolight.app.main.adapter;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pricetolight.R;
import com.pricetolight.api.modal.Licence;
import com.pricetolight.databinding.RowLicenceBinding;

import java.util.List;

public class LicencesAdapter extends RecyclerView.Adapter<LicencesAdapter.ViewHolder> {

    private final List<Licence> lisenceList;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RowLicenceBinding binding;

        public ViewHolder(RowLicenceBinding  binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bind(Licence licence) {
            final Context context = binding.getRoot().getContext();

            binding.dependency.setText(licence.getDependency());
            binding.type.setText(licence.getType());

        }
    }

    public LicencesAdapter(List<Licence> licenceList) {
        this.lisenceList = licenceList;
    }


    @Override
    public LicencesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        return new LicencesAdapter.ViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.row_licence, parent, false));
    }

    @Override
    public void onBindViewHolder(LicencesAdapter.ViewHolder holder, int position) {
        holder.bind(lisenceList.get(position));
    }

    @Override
    public int getItemCount() {
        return lisenceList.size();
    }


}
