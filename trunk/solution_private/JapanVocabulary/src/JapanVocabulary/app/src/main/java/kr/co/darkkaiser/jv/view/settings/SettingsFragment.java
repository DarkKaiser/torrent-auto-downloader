package kr.co.darkkaiser.jv.view.settings;

import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;

import com.github.machinarius.preferencefragment.PreferenceFragment;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.common.Constants;
import kr.co.darkkaiser.jv.vocabulary.db.VocabularyDbHelper;

public class SettingsFragment extends PreferenceFragment {

    private static final String TAG = "SettingsFragment";

    private String appVersion = null;
	private String installedVocabularyDbVersion = null;
    private PreferenceScreen prefDbVersion = null;

    private VocabularyDbVersionCheckAsyncTask vocabularyDbVersionCheckAsyncTask = null;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getPreferenceManager().setSharedPreferencesName(Constants.SHARED_PREFERENCES_NAME);
		addPreferencesFromResource(R.xml.pref_settings_activity);

		try {
			this.appVersion = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            Log.d(TAG, String.format("App Version : %s", this.appVersion));
		} catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
		}

		SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
		this.installedVocabularyDbVersion = sharedPreferences.getString(Constants.SPKEY_INSTALLED_DB_VERSION, getString(R.string.unknown_vocabulary_db_version));
        Log.d(TAG, String.format("Installed Vocabulary Db Version : %s", this.installedVocabularyDbVersion));

        this.prefDbVersion = (PreferenceScreen) findPreference(getString(R.string.as_app_info_key));
        this.prefDbVersion.setSummary(getString(R.string.app_name) + " 버전 " + (this.appVersion == null ? getString(R.string.unknown_app_version) : this.appVersion) + "\n최신 단어DB 버전 : 버전 확인중..." + "\n설치된 단어DB 버전 : " + (this.installedVocabularyDbVersion == null ? getString(R.string.unknown_vocabulary_db_version) : this.installedVocabularyDbVersion));

		// 최신 단어DB의 버전 정보를 확인합니다.
		this.vocabularyDbVersionCheckAsyncTask = new VocabularyDbVersionCheckAsyncTask();
        this.vocabularyDbVersionCheckAsyncTask.execute();
	}

	@Override
	public void onDestroy() {
		if (this.vocabularyDbVersionCheckAsyncTask != null)
            this.vocabularyDbVersionCheckAsyncTask.cancel(true);

		super.onDestroy();
	}

    private class VocabularyDbVersionCheckAsyncTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				String latestVocabularyDbVersion = VocabularyDbHelper.getInstance().getLatestVocabularyDbVersion();

				if (TextUtils.isEmpty(latestVocabularyDbVersion) == false) {
                    Log.d(TAG, String.format("Latest Vocabulary Db Version : %s", latestVocabularyDbVersion));
                    return latestVocabularyDbVersion;
                }
			} catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
			}

			return getString(R.string.unknown_vocabulary_db_version);
		}

		@Override
        protected void onPostExecute(String result) {
			if (prefDbVersion != null) {
				prefDbVersion.setSummary(getString(R.string.app_name) + " 버전 " + (appVersion == null ? getString(R.string.unknown_app_version) : appVersion) + "\n최신 단어DB 버전 : " + result + "\n설치된 단어DB 버전 : " + (installedVocabularyDbVersion == null ? getString(R.string.unknown_vocabulary_db_version) : installedVocabularyDbVersion));
			}
        }

	}

}
