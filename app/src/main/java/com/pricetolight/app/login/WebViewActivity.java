package com.pricetolight.app.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.UrlQuerySanitizer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pricetolight.R;
import com.pricetolight.api.modal.Home;
import com.pricetolight.api.service.Authenticator;
import com.pricetolight.app.MainActivity;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.app.base.UserManager;
import com.pricetolight.app.util.IntentKeys;
import com.pricetolight.databinding.ActivityWebViewBinding;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class WebViewActivity extends BaseActivity implements Authenticator{

    private ActivityWebViewBinding binding;
    UserManager userManager;

    CookieManager cookieManager = CookieManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);
        userManager = new UserManager(getAppPreferences());

        cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean value) {

            }
        });

        final String startUrl = getIntent().getExtras().getString(IntentKeys.URL);

        binding.webView.setWebChromeClient(new WebChromeClient());
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                Log.d("WebView", "your current url when webpage loading.." + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WebView", "your current url when webpage loading.. finish" + url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String[] parts = url.split("access_token=");
                try {
                    if (parts != null && parts[1] != null) {
                        String token = parts[1];
                        token = StringUtils.substringBetween(token, "", "&token_type");
                        updateToken(token);
                        binding.setLoggedIn(true);
                        Observable.timer(2000, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::loggingIn);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            private void loggingIn(Long aLong) {
                startActivity(new Intent(WebViewActivity.this, MainActivity.class));
            }

        });

        binding.webView.loadUrl(startUrl);

    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            binding.webView.clearCache(true);
            binding.webView.clearHistory();
            super.onBackPressed();
        }
    }


    @Nullable
    @Override
    public String getToken() {
        return null;
    }

    @Override
    public void logout() {

    }

    @Override
    public void updateToken(String token) {

        userManager.updateToken(token);
        getAppPreferences().setAuthenticationToken(token);
    }

    @Override
    public void updateCustomer(Home home) {

    }
}
