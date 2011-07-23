package kr.co.darkkaiser.testSchedule;

import kr.co.darkkaiser.testSchedule.view.option.OptionActivity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

public class TestScheduleTabActivity extends TabActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// @@@@@
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TabHost mTabHost = getTabHost();
        
        mTabHost.addTab(mTabHost.newTabSpec("tab_test1") 
          .setIndicator("����˻�") 
          .setContent(R.id.tabview1)); 
        mTabHost.addTab(mTabHost.newTabSpec("tab_test2") 
          .setIndicator("������") 
          .setContent(R.id.tabview2)); 
        mTabHost.addTab(mTabHost.newTabSpec("tab_test3") 
          .setIndicator("����ó��") 
          .setContent(R.id.tabview3)); 
        mTabHost.addTab(mTabHost.newTabSpec("tab_test4") 
                .setIndicator("���� ����") 
                .setContent(new Intent(this, TestActivity.class))); 
        
        mTabHost.setCurrentTab(0); 
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ts_main_menu, menu);
		return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.tsm_preferences:
			startActivityForResult(new Intent(this, OptionActivity.class), R.id.tsm_preferences);
			return true;
		}

		return false;
	}

//	@Override
//	public void onBackPressed() {
//		if (mCustomEventHandler.hasMessages(MSG_CUSTOM_EVT_APP_FINISH_STANDBY) == false) {
//			mCustomEventHandler.sendEmptyMessageAtTime(MSG_CUSTOM_EVT_APP_FINISH_STANDBY, SystemClock.uptimeMillis() + 2000);
//			Toast.makeText(this, "'�ڷ�' ��ư�� �ѹ� �� �����ø� ����˴ϴ�.", Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		SharedPreferences preferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
//		mJvMemorizeList.saveVocabularyPosition(preferences);
//
//		super.onBackPressed();
//	}

}
