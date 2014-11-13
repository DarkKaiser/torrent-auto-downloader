package kr.co.darkkaiser.jv.view.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.TextUtils;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.common.Constants;
import kr.co.darkkaiser.jv.vocabulary.data.JvPathManager;

// TODO 액션바의 홈버튼 누를때 리프레쉬 됨
// TODO 프로요에서 액션바가 표시디지 않음
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	private String appVersion = null;
	private String installedDbVersion = null;
	private PreferenceScreen prefDbVersion = null;
	private DoConfirmVocabularyDbAsyncTask doConfirmVocabularyDbAsyncTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getPreferenceManager().setSharedPreferencesName(Constants.SHARED_PREFERENCE_NAME);
		addPreferencesFromResource(R.xml.pref_settings_activity);

		try {
			appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		installedDbVersion = preferences.getString(Constants.SP_DB_VERSION, getString(R.string.unknown_vocabulary_db_version));

        prefDbVersion = (PreferenceScreen)findPreference("jv_program_info");
		prefDbVersion.setSummary(getString(R.string.app_name) + " 버전 " + (appVersion == null ? getString(R.string.unknown_app_version) : appVersion) + "\n최신 단어DB 버전 : 버전 확인중..." + "\n설치된 단어DB 버전 : " + (installedDbVersion == null ? getString(R.string.unknown_vocabulary_db_version) : installedDbVersion));

		// 최신 단어DB의 버전 정보를 확인합니다.
		doConfirmVocabularyDbAsyncTask = new DoConfirmVocabularyDbAsyncTask();
		doConfirmVocabularyDbAsyncTask.execute();
	}

	@Override
	protected void onDestroy() {
		if (doConfirmVocabularyDbAsyncTask != null)
			doConfirmVocabularyDbAsyncTask.cancel(true);

		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

	}
	
	private class DoConfirmVocabularyDbAsyncTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				String latestVocabularyDbVersion = JvPathManager.getLatestVocabularyDbVersion();

				if (TextUtils.isEmpty(latestVocabularyDbVersion) == false)
                    return latestVocabularyDbVersion;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return getString(R.string.unknown_vocabulary_db_version);
		}

		@Override
        protected void onPostExecute(String result) {
			if (prefDbVersion != null) {
                prefDbVersion = (PreferenceScreen)findPreference("jv_program_info");
				prefDbVersion.setSummary(getString(R.string.app_name) + " 버전 " + (appVersion == null ? getString(R.string.unknown_app_version) : appVersion) + "\n최신 단어DB 버전 : " + result + "\n설치된 단어DB 버전 : " + (installedDbVersion == null ? getString(R.string.unknown_vocabulary_db_version) : installedDbVersion));
			}
        }

	}

}
