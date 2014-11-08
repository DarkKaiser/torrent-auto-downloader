package kr.co.darkkaiser.jv.view.option;

import java.util.ArrayList;

import kr.co.darkkaiser.jv.JvDefines;
import kr.co.darkkaiser.jv.JvHelper;
import kr.co.darkkaiser.jv.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.Window;

//@@@@@
public class OptionActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	private String appVersion = null;
	private String installedDbVersion = null;
	private PreferenceScreen prefJvDbVersion = null;
	private DoConfirmVocabularyDbJob doConfirmVocabularyDbJob = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		// Ÿ��Ʋ�� �����Ѵ�.
		setTitle(String.format("%s - ȯ�漳��", getResources().getString(R.string.app_name)));

		getPreferenceManager().setSharedPreferencesName(JvDefines.JV_SHARED_PREFERENCE_NAME);
		addPreferencesFromResource(R.xml.preference);

		try {
			appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		SharedPreferences preferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		installedDbVersion = preferences.getString(JvDefines.JV_SPN_DB_VERSION, "Unknown");

		StringBuilder sb = new StringBuilder();
		sb.append("App. Version ").append(appVersion == null ? "�� �� ����" : appVersion)
		  .append("\n�ֽ� �ܾ� DB ���� : ").append("���� Ȯ����...")
		  .append("\n��ġ�� �ܾ� DB ���� : ").append(installedDbVersion == null ? "" : installedDbVersion);

		prefJvDbVersion = (PreferenceScreen)findPreference("jv_program_info");
		prefJvDbVersion.setSummary(sb.toString());

		// �ֽ� �ܾ�DB�� ���� ������ Ȯ���մϴ�.
		doConfirmVocabularyDbJob = new DoConfirmVocabularyDbJob();
		doConfirmVocabularyDbJob.execute();
	}

	@Override
	protected void onDestroy() {
		if (doConfirmVocabularyDbJob != null)
			doConfirmVocabularyDbJob.cancel(true);

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
	
	private class DoConfirmVocabularyDbJob extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				ArrayList<String> vocaDbInfo = JvHelper.getLatestVocabularyDbInfo();
				
				if (vocaDbInfo.size() >= 1) {
					return vocaDbInfo.get(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return "�� �� ����";
		}

		@Override
        protected void onPostExecute(String result) {
			if (prefJvDbVersion != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("App. Version ").append(appVersion == null ? "�� �� ����" : appVersion)
				  .append("\n�ֽ� �ܾ� DB ���� : ").append(result)
				  .append("\n��ġ�� �ܾ� DB ���� : ").append(installedDbVersion == null ? "" : installedDbVersion);

				prefJvDbVersion = (PreferenceScreen)findPreference("jv_program_info");
				prefJvDbVersion.setSummary(sb.toString());
			}
        }

	}

}
