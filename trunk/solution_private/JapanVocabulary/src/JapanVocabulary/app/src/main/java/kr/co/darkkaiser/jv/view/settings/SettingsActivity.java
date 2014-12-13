package kr.co.darkkaiser.jv.view.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.common.Constants;
import kr.co.darkkaiser.jv.vocabulary.db.VocabularyDbHelper;

// TODO 프로요에서 액션바가 표시디지 않음
@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private static final String TAG = "SettingsActivity";

    private String mAppVersion = null;
	private String mInstalledVocabularyDbVersion = null;
	private VocabularyDbVersionCheckAsyncTask mVocabularyDbVersionCheckAsyncTask = null;
    private PreferenceScreen mPrefDbVersion = null;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getPreferenceManager().setSharedPreferencesName(Constants.SHARED_PREFERENCES_NAME);
		addPreferencesFromResource(R.xml.pref_settings_activity);

		try {
			mAppVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            Log.d(TAG, String.format("App Version : %s", mAppVersion));
		} catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
		}

		SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
		mInstalledVocabularyDbVersion = sharedPreferences.getString(Constants.SPKEY_INSTALLED_DB_VERSION, getString(R.string.unknown_vocabulary_db_version));
        Log.d(TAG, String.format("Installed Vocabulary Db Version : %s", mInstalledVocabularyDbVersion));

        mPrefDbVersion = (PreferenceScreen)findPreference(getString(R.string.as_program_info_key));
		mPrefDbVersion.setSummary(getString(R.string.app_name) + " 버전 " + (mAppVersion == null ? getString(R.string.unknown_app_version) : mAppVersion) + "\n최신 단어DB 버전 : 버전 확인중..." + "\n설치된 단어DB 버전 : " + (mInstalledVocabularyDbVersion == null ? getString(R.string.unknown_vocabulary_db_version) : mInstalledVocabularyDbVersion));

		// 최신 단어DB의 버전 정보를 확인합니다.
		mVocabularyDbVersionCheckAsyncTask = new VocabularyDbVersionCheckAsyncTask();
		mVocabularyDbVersionCheckAsyncTask.execute();
	}

	@Override
	protected void onDestroy() {
		if (mVocabularyDbVersionCheckAsyncTask != null)
			mVocabularyDbVersionCheckAsyncTask.cancel(true);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
			if (mPrefDbVersion != null)
				mPrefDbVersion.setSummary(getString(R.string.app_name) + " 버전 " + (mAppVersion == null ? getString(R.string.unknown_app_version) : mAppVersion) + "\n최신 단어DB 버전 : " + result + "\n설치된 단어DB 버전 : " + (mInstalledVocabularyDbVersion == null ? getString(R.string.unknown_vocabulary_db_version) : mInstalledVocabularyDbVersion));
        }
	}

}
