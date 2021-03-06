package com.pricetolight.app;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.pricetolight.R;
import com.pricetolight.api.modal.CurrentPrice;
import com.pricetolight.api.modal.Home;
import com.pricetolight.api.modal.HomeType;
import com.pricetolight.api.modal.Homes;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.app.hue.ConfigureHueActivity;
import com.pricetolight.app.login.LoginActivity;
import com.pricetolight.app.login.WebViewActivity;
import com.pricetolight.app.main.ConnectHueActivity;
import com.pricetolight.app.main.HelpActivity;
import com.pricetolight.app.main.LicencesActivity;
import com.pricetolight.app.main.fragment.TurnOffServiceDialog;
import com.pricetolight.app.main.fragment.TurnOnWifiDialog;
import com.pricetolight.app.util.IntentKeys;
import com.pricetolight.app.util.TextUtil;
import com.pricetolight.app.util.Util;
import com.pricetolight.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;


public class MainActivity extends BaseActivity implements TurnOffServiceDialog.OnTurnOffServiceListener, PHSDKListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    List<String> homeNickNames = new ArrayList<String>();
    public static final int CONFIGURE_LIGHT = 2001;
    public static final int CONNECT_LIFX = 3001;

    private ActivityMainBinding binding;
    BottomSheetBehavior bottomSheetBehavior;
    private Homes homes;
    private Home home;
    private TurnOffServiceDialog turnOffServiceDialog;
    private TurnOnWifiDialog turnOnWifiDialog;
    PHHueSDK phHueSDK;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PriceToLightsApplication) getApplication()).setupPHSDK();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLoading(true);

        binding.bar.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
        bottomSheetBehavior.setPeekHeight(0);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    binding.dimBackground.setVisibility(View.GONE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                binding.dimBackground.setVisibility(View.VISIBLE);
                binding.dimBackground.setAlpha(slideOffset);
            }
        });

        binding.dimBackground.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        if (getAppPreferences() != null && getAppPreferences().getPreferredTotalPrice() != null) {
            binding.priceSwitch.setChecked(getAppPreferences().getPreferredTotalPrice().get());
        }

        binding.bottomSheet.findViewById(R.id.addDeviceLayout).setOnClickListener(v -> {
            if (getWifiManager() != null && !getWifiManager().isWifiEnabled()) {
                turnOnWifiDialog = TurnOnWifiDialog.newInstance();
                turnOnWifiDialog.show(getSupportFragmentManager(), TurnOnWifiDialog.TAG);
            } else {
                MainActivity.this.startActivityForResult(new Intent(MainActivity.this, ConnectHueActivity.class), 2);
            }
        });
        binding.bottomSheet.findViewById(R.id.logout).setOnClickListener(v -> {
            getUserManager().logout();
            getUserManager().clearCache(this);
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
        binding.bottomSheet.findViewById(R.id.licencesLayout).setOnClickListener(v -> startActivityForResult(new Intent(MainActivity.this, LicencesActivity.class), 1));
        binding.bottomSheet.findViewById(R.id.helpMoreLayout).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HelpActivity.class)));
        binding.bottomSheet.findViewById(R.id.lifxLayout).setOnClickListener(v ->
                startActivityForResult(new Intent(MainActivity.this, WebViewActivity.class)
                        .putExtra(IntentKeys.SHOW_WEBVIEW_HEADER, false)
                        .putExtra(IntentKeys.URL,getResources().getString(R.string.lifx_login_url)), CONNECT_LIFX));

        if (getAppPreferences() != null && getAppPreferences().getNotFirstTime()!=null && getAppPreferences().getNotFirstTime().get()) {
            binding.bottomSheet.findViewById(R.id.firstTimeUser).setVisibility(View.VISIBLE);
            getAppPreferences().setNotFirstTime(false);
        }

        binding.bar.setOnMenuItemClickListener(menuItem -> {
            Toast.makeText(MainActivity.this, "this" + menuItem.toString(), Toast.LENGTH_SHORT).show();
            return false;
        });
        binding.turnOff.setOnClickListener(v -> {
            turnOffServiceDialog = TurnOffServiceDialog.newInstance();
            turnOffServiceDialog.show(getSupportFragmentManager(), TurnOffServiceDialog.TAG);
        });
        binding.fab.setOnClickListener(v -> startActivityForResult(new Intent(MainActivity.this, ConfigureHueActivity.class), CONFIGURE_LIGHT));

//        runGuide();
    }

    private void runGuide() {
        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.bar), "Bar here"),
                        TapTarget.forView(findViewById(R.id.fab), "Pick light here", "Light")
                                .dimColor(android.R.color.holo_blue_dark)
                                .outerCircleColor(R.color.gray400)
                                .targetCircleColor(R.color.blue700)
                                .textColor(android.R.color.white))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        // Yay
                        Log.d(TAG, "onSequenceFinish: ");
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        Log.d(TAG, "onSequenceFinish: ");

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                        Log.d(TAG, "onSequenceFinish: ");

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchData();

        binding.bar.setHideOnScroll(true);

        binding.setNoActiveSubscription(false);
        phHueSDK = PHHueSDK.getInstance();
        try {
            phHueSDK.getNotificationManager().registerSDKListener(this);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable(e.getMessage()));
        }

        if (phHueSDK.getAllBridges() != null && phHueSDK.getAllBridges().size() > 0) {
            PHBridge bridge = phHueSDK.getAllBridges().get(0);
            if (bridge != null && bridge.getResourceCache() != null) {
                binding.setHasHuePaired(true);
            } else {
                binding.setHasHuePaired(false);
            }
        } else {
            binding.setHasHuePaired(false);
        }
    }

    private void fetchData() {
        //Add other calls to fetch from different endpoints
        fetchMe();
        fetchPriceHistory();
    }

    private void fetchPriceHistory() {
        getApi().getPriceApiService()
                .getPriceHistory(getAppPreferences().getActiveHomeId().get())
                .takeUntil(getLifecycleEvents(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPriceHistory, this::handleError);
    }

    private int graphColor1;

    private void onPriceHistory(Home home) {
        //Price history for the day
    }

    private void fetchMe() {
        if (getUserManager().getToken() == null) {
            getUserManager().logout();
            PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
            this.finish();
        }

        getApi().getPriceApiService()
                .getMe()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMe, this::handleError);
    }

    private void onMe(Homes homes) {
        this.homes = homes;

        getUserManager().updateCustomer(homes.getHomes().get(0));

        //Set an active home by default
        getAppPreferences().setActiveHomeId(homes.getHomes().get(0).getId());

        if (!this.homes.getHomes().isEmpty()) {
            for (int i = 0; i < homes.getHomes().size(); i++) {
                homeNickNames.add(homes.getHomes().get(i).getAppNickname());
            }
        }
        //Fetch price only after we have a valid homeID
        fetchPrice();

        //Set Dropdown list of homes
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, R.layout.row_category_spinner, homeNickNames);
        binding.dropDownList.setAdapter(categoryAdapter);
        binding.dropDownList.setBackgroundColor(ContextCompat.getColor(this, R.color.white50));
        if (homes.getHomes().get(0).getType() != null) {
            setAvatar(homes.getHomes().get(0).getType());
        } else {
            setAvatar(HomeType.NOHOUSE);
        }
        binding.dropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getAppPreferences().setActiveHomeId(homes.getHomes().get(position).getId());
                onChangeHome(position);
                if (homes.getHomes().get(position).getType() != null) {
                    setAvatar(homes.getHomes().get(position).getType());
                } else {
                    setAvatar(HomeType.NOHOUSE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
        super.onBackPressed();
    }

    private void setAvatar(HomeType type) {
        switch (type) {
            case CASTLE:
                binding.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_castle, null));
                break;
            case COTTAGE:
                binding.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_cottage, null));
                break;
            case ROWHOUSE:
                binding.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_rowhouse, null));
                break;
            case APARTMENT:
                binding.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_apartment, null));
                break;
            case FLOORHOUSE1:
                binding.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_floorhouse1, null));
                break;
            case FLOORHOUSE2:
                binding.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_floorhouse2, null));
                break;
            case FLOORHOUSE3:
                binding.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_floorhouse3, null));
                break;
            default:
                binding.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_default, null));
        }
    }

    @Nullable
    public WifiManager getWifiManager() {
        return (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    private void onChangeHome(int pos) {
        getAppPreferences().setActiveHomeId(homes.getHomes().get(pos).getId());
        fetchPrice();
    }

    private void fetchPrice() {
        getApi().getPriceApiService()
                .getPrice(getAppPreferences().getActiveHomeId().get())
                .takeUntil(getLifecycleEvents(ActivityEvent.DESTROY))
                .subscribe(this::onPriceRating,
                        this::handleError);
    }

    private void onPriceRating(Home home) {
        binding.setLoading(false);
        this.home = home;

        if (home.getCurrentSubscription() != null && home.getCurrentSubscription().getPriceInfo() != null && home.getCurrentSubscription().getPriceInfo().getCurrent() != null) {
            TransitionManager.beginDelayedTransition(binding.parent);
            binding.setNoActiveSubscription(false);
            CurrentPrice currentPrice = home.getCurrentSubscription().getPriceInfo().getCurrent();

            binding.priceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                getAppPreferences().setPreferredTotalPrice(isChecked);
                setPrice(currentPrice);
            });

            binding.progressView.setBackgroundColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.gray100));
            binding.progressView.setProgress(Util.getCircleProgress(currentPrice.getLevel().getLevelProgress(this)), (int) (Math.random() * (580 - 400)) + 400, 380);
            binding.progressView.setProgressColor(currentPrice.getLevel().getLevelColor(this));

            binding.glowEffect.setColorFilter(currentPrice.getLevel().getLevelColor(this), android.graphics.PorterDuff.Mode.MULTIPLY);
            binding.unit.setText(getResources().getString(R.string.price_unit));

            binding.timeFrame.setText(Util.getformattedTimeframe());
            binding.togglePriceLayout.setVisibility(currentPrice.getTax() == 0 ? View.GONE : View.VISIBLE);

            setPrice(currentPrice);
        } else {
            TransitionManager.beginDelayedTransition(binding.parent);
            binding.setNoActiveSubscription(true);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || requestCode == 2) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if (requestCode == CONFIGURE_LIGHT && resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            Gson gson = new GsonBuilder().create();
            PHLight phLight = gson.fromJson(data.getStringExtra("myjson"), PHLight.class);
            if (phLight != null) {
                PHLight light = new PHLight(phLight);
                setLightColor(light);
            }
            runGuide();
        }
    }

    private void setLightColor(PHLight light) {
        PHHueSDK phHueSDK = PHHueSDK.getInstance();

        getAppPreferences().setLightData(new Gson().toJson(light));

        int priceColor = home.getCurrentSubscription().getPriceInfo().getCurrent().getLevel().getLevelColor(this);
        float xy[] = PHUtilities.calculateXYFromRGB(Color.red(priceColor), Color.green(priceColor), Color.blue(priceColor), light.getModelNumber());
        PHLightState lightState = new PHLightState();

        if (!light.getLastKnownLightState().isOn()) {
            lightState.setOn(true);
        }
        lightState.setX(xy[0]);
        lightState.setY(xy[1]);
        phHueSDK.getSelectedBridge().updateLightState(light, lightState);
        scheduleJob();
    }

    public void scheduleJob() {
        ComponentName componentName = new ComponentName(this, UpdateLightJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(IntentKeys.JOB_UPDATE_LIGHTS, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(60 * 60 * 1000) //Update every 1 hour
                .build();
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            int resultCode = jobScheduler.schedule(jobInfo);
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d(TAG, "Job successfully done mate: ");
            } else {
                Log.d(TAG, "Job failed mate");
            }
        }
    }

    public void cancelJob() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.cancel(IntentKeys.JOB_UPDATE_LIGHTS);
        }
    }

    private void setPrice(CurrentPrice currentPrice) {
        if (getAppPreferences() != null && getAppPreferences().getPreferredTotalPrice() != null && getAppPreferences().getPreferredTotalPrice().get() != null) {
            binding.value.setText(String.valueOf(getAppPreferences().getPreferredTotalPrice().get() ?
                    TextUtil.formatCentesimal(currentPrice.getTotal()) :
                    TextUtil.formatCentesimal(currentPrice.getEnergy())));
        }
    }

    @Override
    public void onTurnOffInteraction(boolean turnedOff) {
        if (turnedOff) {
            if (isJobServiceOn(this)) {
                cancelJob();
                Toast.makeText(this, getResources().getString(R.string.background_service_stopped), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.background_service_started), Toast.LENGTH_SHORT).show();
                scheduleJob();
            }
        }
    }

    @Override
    public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {
        Log.d(TAG, "onCacheUpdated: ");
    }

    @Override
    public void onBridgeConnected(PHBridge phBridge, String s) {
        binding.setHasHuePaired(true);
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {
        Log.d(TAG, "onAuthenticationRequired: ");
        showSnackBar(new Throwable(getResources().getString(R.string.throwable_bridge_auth_required)));
    }

    @Override
    public void onAccessPointsFound(List<PHAccessPoint> list) {
        Log.d(TAG, "onAccessPointsFound: ");
    }

    @Override
    public void onError(int i, String s) {
        Log.d(TAG, "onError: ");
        showSnackBar(new Throwable(getResources().getString(R.string.throwable_bridge_not_found)));
        phHueSDK.getNotificationManager().unregisterSDKListener(this);
    }

    @Override
    public void onConnectionResumed(PHBridge phBridge) {
        Log.d(TAG, "onConnectionResumed: ");
    }

    @Override
    public void onConnectionLost(PHAccessPoint phAccessPoint) {
        Log.d(TAG, "onConnectionLost: ");
        showSnackBar(new Throwable(getResources().getString(R.string.throwable_bridge_connection_lost)));
        phHueSDK.getNotificationManager().unregisterSDKListener(this);
    }


    @Override
    public void onParsingErrors(List<PHHueParsingError> list) {
        Log.d(TAG, "onParsingErrors: ");
    }
}

