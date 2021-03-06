package com.pricetolight.app.login;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;

import com.pricetolight.R;
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

    //To who ever reading this! Good job!! You actually either dug into the core or sneaking around the commits!
    //Here is a pat on the back!
    
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
        finishAndRemoveTask();
        super.onBackPressed();

    }


    private void loginWithEmail() {
        setState(State.LOADING);
        startActivityForResult(new Intent(
                this, WebViewActivity.class)
                .putExtra(IntentKeys.SHOW_WEBVIEW_HEADER, true)
                .putExtra(IntentKeys.URL,getResources().getString(R.string.tibber_login_url)),LOGIN_REQUEST);
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
