package com.example.guitartraina.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.BugReportActivity;
import com.example.guitartraina.databinding.FragmentDashboardBinding;
import com.google.android.material.navigation.NavigationView;

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
            } else if (itemId == R.id.navigation_sesion_grupal) {
            } else if (itemId == R.id.navigation_entrenador_de_oido) {
            } else if (itemId == R.id.navigation_detective_de_ritmo) {
            } else if (itemId == R.id.navigation_rythm_looper) {
            } else if (itemId == R.id.navigation_chord_library) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}