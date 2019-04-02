package com.pricetolight.app.main.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.lighting.model.PHLight;
import com.pricetolight.R;
import com.pricetolight.app.base.BaseFragment;
import com.pricetolight.app.hue.ConfigureLightsAdapter;
import com.pricetolight.app.main.ConnectHueActivity;
import com.pricetolight.databinding.FragmentHuePairResultBinding;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;


public class HuePairResultFragment extends BaseFragment {

    public static final String TAG = HuePairResultFragment.class.getSimpleName();

    public static final String ARG_PARAM1 = "param1";

    private FragmentHuePairResultBinding binding;

    private String mParam1;

    private OnHuePairResultListener onHuePairResultListener;

    public HuePairResultFragment() {
        // Required empty public constructor
    }

    public static HuePairResultFragment newInstance(String param1, String param2) {
        HuePairResultFragment fragment = new HuePairResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_hue_pair_result, container, false);


        ((ConnectHueActivity)getActivity()).getAllLightsPublishSubject().asObservable()
                .takeUntil(getLifecycleEvents(FragmentEvent.STOP))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLights, this::handleError);

        if(((ConnectHueActivity)getActivity()).getHueSDK()!=null &&
                ((ConnectHueActivity)getActivity()).getHueSDK().getSelectedBridge()!=null &&
                ((ConnectHueActivity)getActivity()).getHueSDK().getSelectedBridge().getResourceCache()!=null &&
                ((ConnectHueActivity)getActivity()).getHueSDK().getSelectedBridge().getResourceCache().getAllLights()!=null) {
            List<PHLight> lights = ((ConnectHueActivity) getActivity()).getHueSDK().getSelectedBridge().getResourceCache().getAllLights();
            onLights(lights);
        }

        binding.done.setOnClickListener(View -> getActivity().finish());
        return binding.getRoot();
    }

    private void onLights(List<PHLight> phLights) {
        if(phLights!=null) {
            binding.summaryValue.setText(String.valueOf(phLights.size()));
            if (phLights.size()>0) {
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                binding.recyclerView.setAdapter(new ConfigureLightsAdapter(phLights, false));
            }
        }

    }

    public void onButtonPressed(boolean isSuccessful) {
        if (onHuePairResultListener != null) {
            onHuePairResultListener.onHuePairResultInteraction(isSuccessful);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHuePairResultListener) {
            onHuePairResultListener = (OnHuePairResultListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTurnOffServiceListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onHuePairResultListener = null;
    }

    public interface OnHuePairResultListener {
        // TODO: Update argument type and name
        void onHuePairResultInteraction(boolean isSuccessful);
    }
}
