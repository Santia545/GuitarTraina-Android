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
        playVideo(videoId);
    }
    private void playVideo(String videoId) {
        String iframe = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/oH7IHDVzE7Q?enablejsapi=1&origin=https%3A%2F%2Fwww.looper.tube&widgetid=1\" frameborder=\"0\" allowfullscreen=\"1\"></iframe>";
        webView.loadData(iframe, "text/html", "utf-8");
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
