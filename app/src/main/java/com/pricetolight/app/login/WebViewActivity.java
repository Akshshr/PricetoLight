package com.pricetolight.app.login;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.UrlQuerySanitizer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pricetolight.R;
import com.pricetolight.api.modal.Home;
import com.pricetolight.api.service.Authenticator;
import com.pricetolight.app.base.BaseActivity;
import com.pricetolight.app.util.IntentKeys;
import com.pricetolight.databinding.ActivityWebViewBinding;

public class WebViewActivity extends BaseActivity implements Authenticator{

    private ActivityWebViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);


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
                UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);
                final String token = sanitizer.getValue("access_token");

                updateToken(token);
                System.out.println("when you click on any interlink on webview that time you got url :-" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        binding.webView.loadUrl(startUrl);

    }


    @Override
    protected void onStart() {
        super.onStart();


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

    }

    @Override
    public void updateCustomer(Home home) {

    }
}
