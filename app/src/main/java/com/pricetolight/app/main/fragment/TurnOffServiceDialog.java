package com.pricetolight.app.main.fragment;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pricetolight.R;
import com.pricetolight.app.util.IntentKeys;
import com.pricetolight.databinding.FragmentTurnOffServiceDialogBinding;

import java.util.Objects;

public class TurnOffServiceDialog extends DialogFragment {

    private OnTurnOffServiceListener turnOffServiceListener;

    public static final String TAG = TurnOffServiceDialog.class.getSimpleName();
    private FragmentTurnOffServiceDialogBinding binding;

    public TurnOffServiceDialog() {
        // Required empty public constructor
    }

    public static TurnOffServiceDialog newInstance() {
        TurnOffServiceDialog fragment = new TurnOffServiceDialog();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getDialog()!=null && getDialog().getWindow()!=null) {
            int width = getResources().getDimensionPixelSize(R.dimen.dialog_width);
            int height = getResources().getDimensionPixelSize(R.dimen.dialog_height);
            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_turn_off_service_dialog, container, false);

        binding.yesButton.setOnClickListener(View -> onButtonPressed(true));
        binding.noButton.setOnClickListener(View -> onButtonPressed(false));

        if(isJobServiceOn(Objects.requireNonNull(getActivity()))){
            binding.setJobRunning(true);
            binding.setJobStatus("Running");
        }else{
            binding.setJobRunning(false);
            binding.setJobStatus("Turned Off");
        }
        return binding.getRoot();
    }


    public static boolean isJobServiceOn(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService( Context.JOB_SCHEDULER_SERVICE ) ;
        boolean hasBeenScheduled = false ;

        for ( JobInfo jobInfo : scheduler.getAllPendingJobs() ) {
            if ( jobInfo.getId() == IntentKeys.JOB_UPDATE_LIGHTS ) {
                hasBeenScheduled = true ;
                break ;
            }
        }
        return hasBeenScheduled;
    }

    public void onButtonPressed(boolean turnedOff) {
        this.dismiss();
        if (turnOffServiceListener != null) {
            turnOffServiceListener.onTurnOffInteraction(turnedOff);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTurnOffServiceListener) {
            turnOffServiceListener = (OnTurnOffServiceListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTurnOffServiceListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        turnOffServiceListener = null;
    }

    public interface OnTurnOffServiceListener {
        void onTurnOffInteraction(boolean turnedOff);
    }
}
