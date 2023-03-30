package com.example.guitartraina.ui.configuration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.FormatException;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;

import android.widget.Toast;

import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.account.LogInActivity;
import com.example.guitartraina.activities.tuner.Tuning;
import com.example.guitartraina.services.PostureNotificationService;
import com.example.guitartraina.services.PracticeNotificationService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.List;


public class ConfigurationFragment extends PreferenceFragmentCompat {
    private SharedPreferences archivo;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.config, rootKey);
        getEncryptedSharedPreferences();
        SwitchPreferenceCompat practiceNotifications = findPreference("practice_notifications");
        if(practiceNotifications!=null) {
            practiceNotifications.setOnPreferenceChangeListener((preference, newValue) -> {
                if((Boolean) newValue){
                    Intent intent = new Intent(requireContext(), PracticeNotificationService.class);
                    requireActivity().startService(intent);
                }else{
                    requireContext().stopService(new Intent(requireContext(), PracticeNotificationService.class));
                }
                return true;
            });
        }
        SwitchPreferenceCompat postureNotifications = findPreference("posture_notifications");
        if(postureNotifications!=null) {
            postureNotifications.setOnPreferenceChangeListener((preference, newValue) -> {
                if((Boolean) newValue){
                    Intent intent = new Intent(requireContext(), PostureNotificationService.class);
                    requireActivity().startService(intent);
                }else{
                    requireContext().stopService(new Intent(requireContext(), PostureNotificationService.class));
                }
                return true;
            });
        }
        EditTextPreference practiceTime = findPreference("practice_notifications_time");
        if (practiceTime != null) {
            practiceTime.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
                editText.setHint("HH:MM:SS");
                editText.setText(R.string.notification_time_5min);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
            });
            practiceTime.setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    validateDate(newValue.toString());
                    validateTotalTime(newValue.toString(), 300, 7200);
                    return true;
                } catch (FormatException ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    return false;
                }
            });
        }
        DropDownPreference tuning = findPreference("tuner_default");
        setDropDownPreferenceData(tuning);
        SeekBarPreference microphoneSensibility = findPreference("tuner_sensibility");
        if (microphoneSensibility != null) {
            microphoneSensibility.setOnPreferenceChangeListener((preference, newValue) -> {
                String value = newValue.toString();
                if(!value.equals("0")){
                    value="-"+value;
                }else{
                    Toast.makeText(requireContext(), R.string.mic_sens_info,Toast.LENGTH_SHORT).show();
                }
                value=value+"dB";
                preference.setSummary(value);
                return true;
            });
        }
        SeekBarPreference microphoneGain = findPreference("microphone_gain");
        if (microphoneGain != null) {
            microphoneGain.setOnPreferenceChangeListener((preference, newValue) -> {
                double value = Integer.parseInt(newValue.toString())/10.;
                if(value<1.d){
                    return false;
                }
                preference.setSummary("X"+value);
                return true;
            });
        }
        Preference language = findPreference("change_language");
        if (language != null) {
            language.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intent);
                return true;
            });
        }
        EditTextPreference postureTime = findPreference("posture_notifications_time");
        if (postureTime != null) {
            postureTime.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
                editText.setHint("HH:MM:SS");
                editText.setText(R.string.notification_time_2min);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
            });
            postureTime.setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    validateDate(newValue.toString());
                    validateTotalTime(newValue.toString(), 120, 3600);
                    return true;
                } catch (FormatException ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    return false;
                }
            });
        }
        Preference logIn = findPreference("sign_in");
        if (logIn != null) {

            if (archivo.contains("idUsuario")) {
                if (!archivo.getString("idUsuario", null).equals("0")) {
                    logIn.setEnabled(false);
                }
            }
            logIn.setOnPreferenceClickListener(preference -> {
                onClickLogIn();
                return true;
            });

        }
        Preference logOut = findPreference("sign_out");
        if (logOut != null) {
            if (archivo.contains("idUsuario")) {
                if (archivo.getString("idUsuario", null).equals("0")) {
                    logOut.setEnabled(false);

                }
            }
            logOut.setOnPreferenceClickListener(preference -> {
                onClickLogOut();

                return true;
            });

        }
    }

    private void setDropDownPreferenceData(DropDownPreference tuning) {
        List<Tuning> tunings;
        String jsonTunings = getResources().getString(R.string.default_tunings);
        Type type = new TypeToken<List<Tuning>>() {
        }.getType();
        tunings = new Gson().fromJson(jsonTunings, type);
        String jsonArrayLocalTunings = archivo.getString("custom_tunings", null);
        if (jsonArrayLocalTunings != null) {
            tunings.addAll(new Gson().fromJson(jsonArrayLocalTunings, type));
        }
        CharSequence[] id = new CharSequence[tunings.size()];
        CharSequence[] title = new CharSequence[tunings.size()];
        for (int i = 0; i < tunings.size(); i++) {
            id[i] = "" + tunings.get(i).getId();
            title[i] = tunings.get(i).getTitle();
        }
        tuning.setDefaultValue(id);
        tuning.setEntries(title);
        tuning.setEntryValues(id);

    }

    private void onClickLogIn() {
        SharedPreferences.Editor editor = archivo.edit();
        editor.clear();
        editor.apply();
        Intent aux = new Intent(requireContext(), LogInActivity.class);
        startActivity(aux);
        requireActivity().finish();
    }

    private void onClickLogOut() {
        SharedPreferences.Editor editor = archivo.edit();
        editor.clear();
        editor.apply();
        Intent aux = new Intent(requireContext(), LogInActivity.class);
        startActivity(aux);
        requireActivity().finish();
    }

    private void getEncryptedSharedPreferences() {
        String masterKeyAlias;
        archivo = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            archivo = EncryptedSharedPreferences.create("archivo", masterKeyAlias, requireContext(), EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private void validateTotalTime(String Date, int minSeconds, int maxSeconds) throws FormatException {
        int seconds = 0;
        String[] date = Date.split(":");
        int[] intDate = stringArrayToIntArray(date);
        seconds += intDate[2];
        seconds += intDate[1] * 60;
        seconds += intDate[0] * 3600;
        if (seconds > maxSeconds) {
            throw new FormatException(getString(R.string.time_cant_be_more_than) + String.format("%.2f", (double) maxSeconds / 60) + getString(R.string.minutos));
        }
        if (seconds < minSeconds) {
            throw new FormatException(getString(R.string.time_cant_be_less_than) + String.format("%.2f", (double) minSeconds / 60) + getString(R.string.minutos));
        }


    }

    private void validateDate(String date) throws FormatException {
        if (date.length() != 8) {
            throw new FormatException(getString(R.string.time_format_lenght_error));
        }
        String[] splittedDate = date.split(":");
        if (splittedDate.length != 3) {
            throw new FormatException(getString(R.string.time_format_HHMMSS_error));
        }
        int[] numbers = stringArrayToIntArray(splittedDate);
        if (numbers[0] > 24) {
            throw new FormatException(getString(R.string.time_format_hh_error));
        }
        if (numbers[1] > 60) {
            throw new FormatException(getString(R.string.time_format_mm_error));
        }
        if (numbers[2] > 60) {
            throw new FormatException(getString(R.string.time_format_ss_error));
        }
    }

    private int[] stringArrayToIntArray(String[] splittedDate) {
        int[] numbers = new int[splittedDate.length];
        for (int i = 0; i < splittedDate.length; i++) {
            numbers[i] = Integer.parseInt(splittedDate[i]);
        }
        return numbers;
    }


}