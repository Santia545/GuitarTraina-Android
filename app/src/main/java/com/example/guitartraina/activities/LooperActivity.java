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

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Enable JavaScript interface to access video player state and current time
        webView.addJavascriptInterface(new JavaScriptInterface(), "Android");

        String videoId = "Sb5aq5HcS1A";
        String videoUrl = "https://www.youtube.com/embed/" + videoId + "?enablejsapi=1";

        // Set the WebView layout params to match the video player size
        int videoWidth = 640;
        int videoHeight = 360;
        float videoAspectRatio = (float) videoWidth / videoHeight;
        int webViewWidth = getResources().getDisplayMetrics().widthPixels;
        int webViewHeight = Math.round(webViewWidth / videoAspectRatio);

        ViewGroup.LayoutParams layoutParams = webView.getLayoutParams();
        layoutParams.width = webViewWidth;
        layoutParams.height = webViewHeight;
        webView.setLayoutParams(layoutParams);

        webView.loadUrl(videoUrl);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // JavaScript interface to access video player state and current time
    private class JavaScriptInterface {

        @JavascriptInterface
        public void onPlayerStateChange(int state) {
            // Handle video player state change
            switch (state) {
                case 0: // Video ended
                    Toast.makeText(LooperActivity.this, "Video ended", Toast.LENGTH_SHORT).show();
                    break;
                case 1: // Video playing
                    Toast.makeText(LooperActivity.this, "Video playing", Toast.LENGTH_SHORT).show();
                    break;
                case 2: // Video paused
                    Toast.makeText(LooperActivity.this, "Video paused", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @JavascriptInterface
        public void onCurrentTimeChange(float currentTime) {
            // Handle current time change
            Toast.makeText(LooperActivity.this, "Current time: " + currentTime, Toast.LENGTH_SHORT).show();
        }
    }
}
