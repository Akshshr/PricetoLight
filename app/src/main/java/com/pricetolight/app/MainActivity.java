package com.pricetolight.app;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.pricetolight.R;
import com.pricetolight.api.modal.CurrentPrice;
import com.pricetolight.api.modal.CurrentSubscription;
import com.pricetolight.api.modal.Home;
import com.pricetolight.api.modal.Homes;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.app.main.ConnectHueActivity;
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

        binding.bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
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
        binding.dimBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });


        binding.bottomSheet.findViewById(R.id.addDeviceLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ConnectHueActivity.class));
            }
        });

        binding.bar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Toast.makeText(MainActivity.this, "this" + menuItem.toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

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
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, R.layout.row_category_spinner,homeNickNames);
        binding.dropDownList.setAdapter(categoryAdapter);

        int currentSelection = binding.dropDownList.getSelectedItemPosition();
        binding.houseType.setText(homes.getHomes().get(0).getType());

        binding.dropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(currentSelection == position){
//                    Do nothing
//                }else{
                    getAppPreferences().setActiveHomeId(homes.getHomes().get(position).getId());
                    onChangeHome(position);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

        CurrentPrice currentPrice = home.getCurrentSubscription().getPriceInfo().getCurrent();

        binding.priceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getAppPreferences().setPreferredTotalPrice(isChecked);
                setPrice(currentPrice);
            }
        });

        binding.progressView.setBackgroundColor(ContextCompat.getColor(binding.getRoot().getContext(),R.color.gray100));
        binding.progressView.setProgress(Util.getCircleProgress(currentPrice.getLevel().getLevelProgress(this)), (int) (Math.random() * (580 - 400)) + 400, 380);
        binding.progressView.setProgressColor(currentPrice.getLevel().getLevelColor(this));

        binding.glowEffect.setColorFilter(currentPrice.getLevel().getLevelColor(this), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.unit.setText(getResources().getString(R.string.price_unit));

        binding.timeFrame.setText(Util.getformattedTimeframe());

//        aed581
        setPrice(currentPrice);
    }

    private void setPrice(CurrentPrice currentPrice) {

        if(getAppPreferences()!=null && getAppPreferences().getPreferredTotalPrice()!=null && getAppPreferences().getPreferredTotalPrice().get()!=null) {
            binding.value.setText(String.valueOf(getAppPreferences().getPreferredTotalPrice().get() ?
                    TextUtil.formatCentesimal(currentPrice.getTotal())       :
                    TextUtil.formatCentesimal(currentPrice.getEnergy())));
        }
    }


}
