package com.pricetolight.app.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.pricetolight.R;
import com.pricetolight.app.MainActivity;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.databinding.ActivityMagicLinkBinding;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MagicLinkActivity extends BaseActivity {

    private ActivityMagicLinkBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_magic_link);

        Observable.timer(1200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFinishing);
    }

    private void onFinishing(Long aLong) {
        if(getUserManager()!=null && getUserManager().isLoggedIn()){
            this.finish();
            startActivity(new Intent(this, MainActivity.class).setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }else{
            startActivity(new Intent(this, LoginActivity.class).setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            this.finish();
        }
    }

}
