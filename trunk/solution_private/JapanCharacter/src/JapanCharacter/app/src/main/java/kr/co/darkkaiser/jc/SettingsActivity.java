package kr.co.darkkaiser.jc;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;

import com.lb.material_preferences_library.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_Jc_Settings);
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("jc_setup");



        CheckBoxPreference chkHiragana = (CheckBoxPreference)findPreference("chk_hiragana");
        CheckBoxPreference chkGatakana = (CheckBoxPreference)findPreference("chk_gatakana");
        chkHiragana.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean flag = (boolean) newValue;
                CheckBoxPreference chkYoum = (CheckBoxPreference)findPreference("chk_youm");
                CheckBoxPreference chkGatakana = (CheckBoxPreference)findPreference("chk_gatakana");

                if (flag || chkGatakana.isChecked()) {
                    chkYoum.setEnabled(true);
                } else {
                    chkYoum.setEnabled(false);
                }

                return true;
            }
        });
        chkGatakana.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean flag = (boolean) newValue;

                CheckBoxPreference chkYoum = (CheckBoxPreference)findPreference("chk_youm");
                CheckBoxPreference chkHiragana = (CheckBoxPreference)findPreference("chk_hiragana");

                if (chkHiragana.isChecked() || flag) {
                    chkYoum.setEnabled(true);
                } else {
                    chkYoum.setEnabled(false);
                }

                return true;
            }
        });

        CheckBoxPreference chkYoum = (CheckBoxPreference)findPreference("chk_youm");
        if (getPreferenceScreen().getSharedPreferences().getBoolean("chk_hiragana", true) || getPreferenceScreen().getSharedPreferences().getBoolean("chk_gatakana", true)) {
            chkYoum.setEnabled(true);
        } else {
            chkYoum.setEnabled(false);
        }

        // 어플리케이션의 버전 정보를 구하여 화면에 출력한다.
        String versionName = getString(R.string.unknown_program_version);
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        PreferenceScreen prefProgramInfo = (PreferenceScreen)findPreference("jc_program_info");
        prefProgramInfo.setSummary(getString(R.string.app_name) + " " + getString(R.string.program_version) + " " + versionName);
    }

    @Override
    protected int getPreferencesXmlId() {
        return R.xml.pref_settings_activity;
    }

}
