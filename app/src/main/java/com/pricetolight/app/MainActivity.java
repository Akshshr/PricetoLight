package com.pricetolight.app;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.pricetolight.R;
import com.pricetolight.api.modal.Home;
import com.pricetolight.api.modal.Homes;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.app.base.BaseFragment;
import com.pricetolight.app.main.ConnectHueActivity;
import com.pricetolight.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends BaseActivity {

    public static final String TAG = MainActivity.class.getSimpleName();


    List<String> homeNickNames = new ArrayList<String>();

    private ActivityMainBinding binding;
    BottomSheetBehavior bottomSheetBehavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.circleProgressBar.setProgress(75, true);
        }else{
            binding.circleProgressBar.setProgress(75);
        }

    }

    private void fetchData() {
        fetchMe();
//        fetchPrice();
    }

    private void fetchMe() {
        getApi().getPriceApiService()
                .getMe()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMe, this::handleError);
    }

    private void onMe(Homes homes) {

        for(int i = 0; i< homes.getHomes().size(); i++) {
            homeNickNames.add(homes.getHomes().get(i).getAppNickname());
        }
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, R.layout.row_category_spinner,homeNickNames);
        binding.dropDownList.setAdapter(categoryAdapter);
        int currentSelection = binding.dropDownList.getSelectedItemPosition();
        binding.houseType.setText(homes.getHomes().get(0).getType());

        binding.dropDownList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentSelection == position){
                    //Do nothing
                }else{
                    getAppPreferences().setActiveHomeId(homes.getHomes().get(position).getId());
                    onChangeHome(position);
                }
            }
        });

    }

    private void onChangeHome(int pos) {
        fetchPrice();
    }

    private void fetchPrice() {
        getApi().getPriceApiService()
                .getPrice(getAppPreferences().getActiveHomeId().get())
                .takeUntil(getLifecycleEvents(ActivityEvent.DESTROY))
                .subscribe(this::onPriceRating,
                        this::handleError);

    }

    private void onPriceRating(Homes homes) {
        Log.d(TAG, "onPriceRating: " + homes);
    }



}

