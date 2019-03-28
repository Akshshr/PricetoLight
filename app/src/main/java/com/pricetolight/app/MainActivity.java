package com.pricetolight.app;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.pricetolight.R;
import com.pricetolight.api.modal.CurrentPrice;
import com.pricetolight.api.modal.CurrentSubscription;
import com.pricetolight.api.modal.Home;
import com.pricetolight.api.modal.Homes;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.app.hue.ConfigureHueActivity;
import com.pricetolight.app.main.ConnectHueActivity;
import com.pricetolight.app.main.LicencesActivity;
import com.pricetolight.app.util.TextUtil;
import com.pricetolight.app.util.Util;
import com.pricetolight.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends BaseActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    List<String> homeNickNames = new ArrayList<String>();

    private ActivityMainBinding binding;
    BottomSheetBehavior bottomSheetBehavior;
    private Homes homes;
    private CurrentSubscription currentSubscription;
    private Home home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

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


        //Dim background setup..
        binding.dimBackground.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        binding.priceSwitch.setChecked(getAppPreferences().getPreferredTotalPrice().get());

        binding.bottomSheet.findViewById(R.id.addDeviceLayout).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ConnectHueActivity.class)));
        binding.bottomSheet.findViewById(R.id.licencesLayout).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LicencesActivity.class)));

        binding.bar.setOnMenuItemClickListener(menuItem -> {
            Toast.makeText(MainActivity.this, "this" + menuItem.toString(), Toast.LENGTH_SHORT).show();
            return false;
        });


        PHHueSDK phHueSDK=PHHueSDK.getInstance();
        if(phHueSDK.getAllBridges()!=null && phHueSDK.getAllBridges().size() > 0) {
            PHBridge bridge = phHueSDK.getAllBridges().get(0);
            if (bridge != null && bridge.getResourceCache() != null) {
                binding.setHasHuePaired(true);
                binding.fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ConfigureHueActivity.class)));
            } else {
                binding.setHasHuePaired(false);
            }
        }else {
            binding.setHasHuePaired(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.bar.setHideOnScroll(true);

        fetchData();
    }

    private void fetchData() {
        fetchMe();
        fetchPrice();
    }

    private void fetchMe() {
        getApi().getPriceApiService()
                .getMe()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMe, this::handleError);
    }

    private void onMe(Homes homes) {
        this.homes = homes;
        if (!this.homes.getHomes().isEmpty()) {
            for (int i = 0; i < homes.getHomes().size(); i++) {
                homeNickNames.add(homes.getHomes().get(i).getAppNickname());
            }
        }

        //Set Dropdown list of homes
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, R.layout.row_category_spinner, homeNickNames);
        binding.dropDownList.setAdapter(categoryAdapter);

        setAvatar(0);

        binding.dropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getAppPreferences().setActiveHomeId(homes.getHomes().get(position).getId());
                onChangeHome(position);
                setAvatar(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setAvatar(int position) {
        switch (homes.getHomes().get(position).getType()) {
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

            setPrice(currentPrice);
        } else {
            TransitionManager.beginDelayedTransition(binding.parent);
            binding.setNoActiveSubscription(true);
        }
    }

    private void setPrice(CurrentPrice currentPrice) {

        if (getAppPreferences() != null && getAppPreferences().getPreferredTotalPrice() != null && getAppPreferences().getPreferredTotalPrice().get() != null) {
            binding.value.setText(String.valueOf(getAppPreferences().getPreferredTotalPrice().get() ?
                    TextUtil.formatCentesimal(currentPrice.getTotal()) :
                    TextUtil.formatCentesimal(currentPrice.getEnergy())));
        }
    }


}

