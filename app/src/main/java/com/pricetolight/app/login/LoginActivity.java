package com.pricetolight.app.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.ColorInt;
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
import com.pricetolight.app.util.Util;
import com.pricetolight.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    private static final String TAG = LoginActivity.class.getSimpleName();

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
        TransitionManager.beginDelayedTransition(binding.content);
        TransitionManager.beginDelayedTransition(binding.loginView);
        binding.setState(state);
        if(state==State.EMAIL){
            binding.titleAndCaption.animate().translationY(-120).setDuration(500);
        }else{
            binding.titleAndCaption.animate().translationY(0).setDuration(500);
        }
        if(state == State.BEGIN) {
            Util.hideKeyboardFromView(getCurrentFocus());
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

    private void loginWithEmail(String email, String password) {
        setState(State.LOADING);
        startActivity(new Intent(this, MainActivity.class));
//        getApi().getUserApiService()
//                .signinUserWithEmail(email, password)
//                .takeUntil(getLifecycleEvents(ActivityEvent.STOP))
//                .subscribe(this::onSuccess, throwable -> {
//                    setState(State.EMAIL);
//                    Log.e(TAG, "An error occured while signing up customer, " + throwable.getMessage(), throwable);
//                });
    }

    private void onSuccess(Homes homes) {
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

            if(validEmailAndPassword(binding.textEmail.getText().toString(),binding.textPassword.getText().toString())) {
                loginWithEmail(binding.textEmail.getText().toString(), binding.textPassword.getText().toString());
            }
        }


    }

    private boolean validEmailAndPassword(String email, String password) {
        boolean invalidEmail = !email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        invalidEmail = !invalidEmail;
        boolean invalidPassword = false;

        if(TextUtils.isEmpty(password) || password.length() < 8){
            invalidPassword = true;
        }
        if(invalidEmail) {
//            binding.setEmailError(getString(R.string.message_error_email_malformed));
        } else if(invalidPassword) {
//            binding.setEmailError(getString(R.string.message_error_password));
        } else {
//            binding.setEmailError(null);
        }
        return !invalidEmail && !invalidPassword;
    }

}
