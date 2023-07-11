package com.example.guitartraina.ui.dashboard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.BugReportActivity;
import com.example.guitartraina.activities.ChordLibraryActivity;
import com.example.guitartraina.activities.LooperActivity;
import com.example.guitartraina.activities.group_session.PickTypeActivity;
import com.example.guitartraina.activities.group_session.share_audio.AudioClientActivity;
import com.example.guitartraina.activities.metronome.MetronomeActivity;
import com.example.guitartraina.databinding.FragmentDashboardBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.Map;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavigationView dashnavView;
        dashnavView = requireView().findViewById(R.id.dash_nav_view);
        dashnavView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_ejercicios) {
            } else if (itemId == R.id.navigation_entrenador_de_bends) {
            } else if (itemId == R.id.navigation_metronomo) {
                Intent metronome = new Intent(requireContext(), MetronomeActivity.class);
                startActivity(metronome);
            } else if (itemId == R.id.navigation_sesion_grupal) {
                requestStoragePermission();
            } else if (itemId == R.id.navigation_entrenador_de_oido) {
            } else if (itemId == R.id.navigation_detective_de_ritmo) {
            } else if (itemId == R.id.navigation_rythm_looper) {
                Intent rythmLooper = new Intent(requireContext(), LooperActivity.class);
                startActivity(rythmLooper);
            } else if (itemId == R.id.navigation_chord_library) {
                Intent chordLibrary = new Intent(requireContext(), ChordLibraryActivity.class);
                startActivity(chordLibrary);
            } else if (itemId == R.id.navigation_progreso) {
            } else if (itemId == R.id.navigation_ranking) {
            } else if (itemId == R.id.navigation_unranked) {
            } else if (itemId == R.id.navigation_reportar_errores) {
                Intent bugReport = new Intent(requireContext(), BugReportActivity.class);
                startActivity(bugReport);
            }
            return true;
        });
    }

    private void requestStoragePermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            requestPermissionLauncher.launch(permissions);
        }else{
            Intent group= new Intent(requireContext(), PickTypeActivity.class);
            startActivity(group);
        }
    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean allPermissionsGranted = true;
                for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                    String permission = entry.getKey();
                    boolean isGranted = entry.getValue();
                    if (!isGranted) {
                        allPermissionsGranted = false;
                        // Handle the denied permission accordingly
                        // You can show an error message or take appropriate action
                        Toast.makeText(requireContext(), "Este modulo no puede funcionar sin el permiso: " + permission, Toast.LENGTH_SHORT).show();
                    }
                }
                if (allPermissionsGranted) {
                    // All permissions granted
                    Toast.makeText(requireContext(), "All permissions granted", Toast.LENGTH_SHORT).show();
                    Intent group= new Intent(requireContext(), PickTypeActivity.class);
                    startActivity(group);
                }
            }
    );

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}