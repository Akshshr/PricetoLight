package com.pricetolight.app.login;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pricetolight.R;
import com.pricetolight.api.modal.Home;
import com.pricetolight.api.service.Authenticator;
import com.pricetolight.app.MainActivity;
import com.pricetolight.app.base.AppCache;
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
    AppCache appCache;
    String tokenRecieved;
    boolean showHeader;

    public static final String TIBBER = "tibber";
    public static final String LIFX = "lifx";

    CookieManager cookieManager = CookieManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);
        userManager = new UserManager(getAppPreferences() , appCache);

        if(getIntent().getExtras()!=null) {
            showHeader = getIntent().getExtras().getBoolean(IntentKeys.SHOW_WEBVIEW_HEADER, false);
        }
        if(!showHeader){
            binding.topHeader.setVisibility(View.GONE);
        }
        cookieManager.removeAllCookies(value -> {

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

                if(url.contains(TIBBER)) {
                    try {
                        if (parts != null && parts[1] != null) {
                            String token = parts[1];
                            token = StringUtils.substringBetween(token, "", "&token_type");
                            tokenRecieved = token;
                            updateToken(token);
                            binding.setLoggedIn(true);
                            Observable.timer(2000, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(this::loggingIn);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(url.contains(LIFX)) {
//                    Do magic here
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            private void loggingIn(Long aLong) {
                Intent mainIntent = new Intent(WebViewActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                WebViewActivity.this.finish();
            }

        });
        binding.webView.loadUrl(startUrl);
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
