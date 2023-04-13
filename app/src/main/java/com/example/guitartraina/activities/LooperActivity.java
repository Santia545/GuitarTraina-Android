package com.example.guitartraina.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.guitartraina.R;

public class LooperActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looper);

        webView = findViewById(R.id.webview);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/index.html");
/*
        String videoId = "Sb5aq5HcS1A";
        String videoUrl = "https://www.youtube.com/embed/" + videoId + "?enablejsapi=1";

        // Set the WebView layout params to match the video player size
        int videoWidth = 640;
        int videoHeight = 390;
        float videoAspectRatio = (float) videoWidth / videoHeight;
        int webViewWidth = getResources().getDisplayMetrics().widthPixels;
        int webViewHeight = Math.round(webViewWidth / videoAspectRatio);

        ViewGroup.LayoutParams layoutParams = webView.getLayoutParams();
        layoutParams.width = webViewWidth;
        layoutParams.height = webViewHeight;
        webView.setLayoutParams(layoutParams);
        playVideo(videoId);*/
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
