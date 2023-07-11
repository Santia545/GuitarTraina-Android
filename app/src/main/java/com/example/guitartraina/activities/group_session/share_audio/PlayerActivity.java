package com.example.guitartraina.activities.group_session.share_audio;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.group_session.Host;
import com.example.guitartraina.activities.group_session.share_audio.sync_utilities.SyncServer;
import com.example.guitartraina.activities.group_session.share_audio.sync_utilities.SyncClient;
import com.example.guitartraina.databinding.ActivityPlayerBinding;

import java.net.InetAddress;
import java.net.UnknownHostException;
public class PlayerActivity extends AppCompatActivity {

    private ActivityPlayerBinding binding;
    String TAG = "PlayerActivity";


    private SyncServer syncServer;
    private SyncClient syncClient;

    private long playbackPosition;
    private Uri uri;
    private int userType;
    MediaPlayer mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uri = getIntent().getParcelableExtra("uri");
        userType = getIntent().getIntExtra("user", 0);
        if(userType == 0){
            syncServer = new SyncServer(this);
            try {
                InetAddress addr = InetAddress.getByName("127.0.0.1");
                syncClient = new SyncClient(this, addr);
            } catch (UnknownHostException e) {
                Log.e(TAG, e.toString());
            }
            binding.btnPlayPause.setOnClickListener(v -> syncServer.togglePlayState());
        }else{
            Host host = getIntent().getParcelableExtra("host");
            syncClient = new SyncClient(this, host.getHostAddress());
        }




        initPlayer();



    }

    private void initPlayer() {
        mPlayer = MediaPlayer.create(this, uri);
        mPlayer.start();
        if(userType == 0){
            binding.sbSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    long duration = mPlayer.getDuration();
                    playbackPosition = i * duration / 100;

                    if (b) {
                        syncServer.sync();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        Thread seekBarSyncThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.e("PLAYER", e.toString());
            }
            while (syncClient.isRunning()) {
                binding.sbSeekbar.setProgress((int) mPlayer.getCurrentPosition() * 100 / (int) mPlayer.getDuration());
                runOnUiThread(() -> {
                    long duration = mPlayer.getDuration();
                    long pbp = getExactPlaybackPosition();
                    String totalTime = String.format("%02d:%02d:%02d", (duration / 3600000) % 24, (duration / 60000) % 60, (duration / 1000) % 60);
                    String curTime = String.format("%02d:%02d:%02d", (pbp / 3600000) % 24, (pbp / 60000) % 60, (pbp / 1000) % 60);
                    binding.tvTotalTime.setText(totalTime);
                    binding.tvCurTime.setText(curTime);
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Log.e("PLAYER", e.toString());
                }
            }
        });
        seekBarSyncThread.start();

    }

    public long getPlaybackPosition() {
        return playbackPosition;
    }

    public long getExactPlaybackPosition() {
        return mPlayer.getCurrentPosition();
    }

    public void seekTo(long l) {
        mPlayer.seekTo((int) l);
    }

    public void setPlay(boolean b) {
        if(b){
            mPlayer.start();
            binding.btnPlayPause.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.baseline_pause_24));
        }else{
            mPlayer.pause();
            binding.btnPlayPause.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.baseline_play_arrow_24));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "Destroy", Toast.LENGTH_SHORT).show();
        Log.d("PLAYER_ACTIVITY", "Destroy");

        if (syncClient != null) syncClient.close();
        if (syncServer != null) syncServer.close();
        mPlayer.release();
    }
}