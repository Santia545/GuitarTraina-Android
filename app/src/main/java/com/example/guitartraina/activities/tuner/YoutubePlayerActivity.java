package com.example.guitartraina.activities.tuner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import com.example.guitartraina.databinding.ActivityYoutubePlayerBinding;

public class YoutubePlayerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //to use:
        /*startActivity(new Intent(this, YoutubePlayerActivity.class)
                .putExtra("video", "\"https://www.youtube.com/embed/xyb74jO1QkA\"")
                .putExtra("titulo", R.string.resource)
                .putExtra("cuerpo", R.string.resource));*/

        super.onCreate(savedInstanceState);
        ActivityYoutubePlayerBinding binding = ActivityYoutubePlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();

        binding.tutorialTitle.setText(intent.getIntExtra("titulo", 0));
        binding.tutorialBody.setText(intent.getIntExtra("cuerpo",0));

        String html = "<body style=\"margin: 0; padding: 0\">" +
                "<iframe " +
                "width=\"100%\" " +
                "height= \"100%\" " +
                "src=" + intent.getStringExtra("video") + " "+
                "title=\"YouTube video player\" " +
                "frameborder=\"0\" " +
                "allow=\"accelerometer; " +
                "autoplay; " +
                "clipboard-write; " +
                "encrypted-media; " +
                "gyroscope;" +
                " picture-in-picture; " +
                "web-share\" " +
                "allowfullscreen>" +
                "</iframe>";
        binding.webView.loadData(html, "text/html", "utf-8");
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebChromeClient(new WebChromeClient());


    }
}