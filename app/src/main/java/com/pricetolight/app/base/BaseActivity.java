package com.pricetolight.app.base;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.pricetolight.R;
import com.pricetolight.api.Api;
import com.pricetolight.app.PriceToLightsApplication;
import com.pricetolight.app.util.IntentKeys;
import com.pricetolight.app.util.UI;

import java.util.Objects;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    public enum ActivityEvent {
        CREATE,
        DESTROY,
        START,
        STOP,
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public Api getApi() {
        return ((PriceToLightsApplication) getApplication()).getApi();
    }

    public AppPreferences getAppPreferences() {
        return ((PriceToLightsApplication) getApplication()).getAppPreferences();
    }

    public UserManager getUserManager() {
        return ((PriceToLightsApplication) getApplication()).getUserManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }

    @Keep
    public void handleError(Throwable throwable) {
        Log.w(TAG, "An error occured and handled by genering error message", throwable);
        if (UI.byId(this, R.id.coordinator) == null) {
            Log.e(TAG, "No CoordinatorLayout attached with ID = R.id.coordinator => we aren't able to show error message");
            return;
        }
        showSnackBar(throwable);
        //TODO Show Error message here in SnackBar
    }

    public Observable<ActivityEvent> getLifecycleEvents(ActivityEvent event) {
        return lifecycleSubject.filter((ActivityEvent activityEvent) -> activityEvent == event);
    }

    public static boolean isJobServiceOn(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE );
        boolean hasBeenScheduled = false ;

        for ( JobInfo jobInfo : Objects.requireNonNull(scheduler).getAllPendingJobs() ) {
            if ( jobInfo.getId() == IntentKeys.JOB_UPDATE_LIGHTS ) {
                hasBeenScheduled = true ;
                break ;
            }
        }
        return hasBeenScheduled;
    }

    public void showSnackBar(Throwable throwable){
        View contextView = findViewById(android.R.id.content);
        final Snackbar snackBar = Snackbar.make(contextView, throwable.getMessage(), Snackbar.LENGTH_INDEFINITE);

        snackBar.setAction(this.getResources().getString(R.string.dismiss), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        snackBar.setActionTextColor(ContextCompat.getColor(this, R.color.red300));
        snackBar.show();

    }

}
