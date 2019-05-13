package com.pricetolight.app.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;

import com.pricetolight.R;
import com.pricetolight.api.modal.Homes;
import com.pricetolight.app.MainActivity;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.app.base.UserManager;
import com.pricetolight.app.util.IntentKeys;
import com.pricetolight.app.util.Util;
import com.pricetolight.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final int LOGIN_REQUEST = 1001;
    UserManager userManager;

    public enum State {
        BEGIN, EMAIL, LOADING
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setDelegate(new Delegate());

        setState(State.BEGIN);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.blue700,null));
    }

    public void setState(State state) {
        if(userManager!=null && userManager.isLoggedIn()){
            startActivity(new Intent(this, MainActivity.class));
        }

        TransitionManager.beginDelayedTransition(binding.content);
        TransitionManager.beginDelayedTransition(binding.loginView);
        binding.setState(state);
        if(state==State.EMAIL){
            binding.titleAndCaption.animate().translationY(-120).setDuration(400);
        }else{
            binding.titleAndCaption.animate().translationY(0).setDuration(400);
        }
        if(state == State.BEGIN) {
            binding.groundView.animate().translationY(0).setDuration(300);
            Util.hideKeyboardFromView(getCurrentFocus());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOGIN_REQUEST){
            setState(State.BEGIN);
        }
    }

    @Override
    public void onBackPressed() {
        binding.groundView.animate().translationY(0).setDuration(500);
        if(!interceptBack()) {
            super.onBackPressed();
        }
    }

    private boolean interceptBack() {
        if(binding.getState() == State.BEGIN) {
            return false;
        }
        setState(State.BEGIN);
        return true;
    }

    private void loginWithEmail() {
        setState(State.LOADING);
        startActivityForResult(new Intent(this, WebViewActivity.class).putExtra(IntentKeys.URL,getResources().getString(R.string.tibber_login_url)),LOGIN_REQUEST);
    }

    public class Delegate {

        Delegate() {
        }

        public void onTibber() {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(getResources().getString(R.string.login_tibber_url)));
            startActivity(browserIntent);
        }

        public void onLogin() {
            setState(State.EMAIL);
            binding.groundView.animate().translationY(-320).setDuration(400);

            loginWithEmail();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setState(State.BEGIN);
    }


}
