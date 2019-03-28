package com.pricetolight.app.base;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pricetolight.R;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    private static final String TAG = BaseFragment.class.getSimpleName();

    public BaseFragment() {
        // Required empty public constructor
    }

    protected @Nullable AppPreferences getAppPreferences() {
        return getActivity() != null ? ((BaseActivity) getActivity()).getAppPreferences() : null;
    }

    public enum FragmentEvent {
        ATTACH, DETACH,
        START, STOP,
    }

    public enum BehaviourEvent {
        ENABLED,
        OPENED,
        MODIFIED,
        DISABLED,
        REGISTERED,

    }

    @Keep
    public void handleError(Throwable throwable) {
        Log.w(TAG, "handleError called", throwable);
        if(getView() != null && getActivity() != null) {
            //TODO: Implement error handle
        }
    }

    private BehaviorSubject<FragmentEvent> lifecycleSubject;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        lifecycleSubject = BehaviorSubject.create();
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        lifecycleSubject = BehaviorSubject.create();
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        lifecycleSubject.onCompleted();
        lifecycleSubject = null;
        super.onDetach();
    }

    public Observable<FragmentEvent> getLifecycleEvents(FragmentEvent event) {
        return lifecycleSubject.filter((FragmentEvent fe) -> fe == event);
    }


}
