package com.example.guitartraina.activities.bendtrainer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guitartraina.R;


public class BendTrainerActivity extends AppCompatActivity {
    private BendTrainer bendTrainer;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bend_trainer);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        } else {
            bendTrainer = new BendTrainer(BendTrainerActivity.this);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(BendTrainerActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            bendTrainer = null;
            ViewGroup parentView = findViewById(android.R.id.content);
            LinearLayout linearLayout = createInfoLayout();
            parentView.removeAllViews();
            parentView.addView(linearLayout);
            return;
        }
        bendTrainer.run();

    }
    @Override
    public void onPause() {
        super.onPause();
        Toast.makeText(BendTrainerActivity.this, "On Pause", Toast.LENGTH_SHORT).show();
        if (bendTrainer != null) {
            Toast.makeText(BendTrainerActivity.this, "Interrumpiendo el hilo", Toast.LENGTH_SHORT).show();
            bendTrainer.stop();
        }

    }


    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    Toast.makeText(BendTrainerActivity.this, "Concedido", Toast.LENGTH_SHORT).show();
                    bendTrainer = new BendTrainer(BendTrainerActivity.this);
                    BendTrainerActivity.this.recreate();
                } else {
                    Toast.makeText(BendTrainerActivity.this, "Este modulo no puede funcionar sin el uso del microfono", Toast.LENGTH_SHORT).show();
                }
            }
    );


    private LinearLayout createInfoLayout() {
        LinearLayout layout = new LinearLayout(BendTrainerActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        float dpi = this.getResources().getDisplayMetrics().density;
        layout.setPaddingRelative((int) (19 * dpi), 0, (int) (19 * dpi), 0);
        TextView tvInfo = new TextView(BendTrainerActivity.this);
        tvInfo.setText(R.string.info_mic_permiso);
        tvInfo.setTextSize(20.0f);
        tvInfo.setGravity(Gravity.CENTER);
        tvInfo.setPaddingRelative(0, 0, 0, (int) (19 * dpi));
        //create a button
        Button btnPermiss = new Button(BendTrainerActivity.this);
        btnPermiss.setText(R.string.dar_permiso);
        btnPermiss.setOnClickListener((View view) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", BendTrainerActivity.this.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            BendTrainerActivity.this.finishAfterTransition();
        });
        layout.addView(tvInfo);
        layout.addView(btnPermiss);
        return layout;
    }
}