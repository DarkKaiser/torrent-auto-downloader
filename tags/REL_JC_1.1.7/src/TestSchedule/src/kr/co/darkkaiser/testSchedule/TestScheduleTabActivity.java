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
          .setIndicator("시험검색") 
          .setContent(R.id.tabview1)); 
        mTabHost.addTab(mTabHost.newTabSpec("tab_test2") 
          .setIndicator("영역별") 
          .setContent(R.id.tabview2)); 
        mTabHost.addTab(mTabHost.newTabSpec("tab_test3") 
          .setIndicator("시행처별") 
          .setContent(R.id.tabview3)); 
        mTabHost.addTab(mTabHost.newTabSpec("tab_test4") 
                .setIndicator("나의 시험") 
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
//			Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		SharedPreferences preferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
//		mJvMemorizeList.saveVocabularyPosition(preferences);
//
//		super.onBackPressed();
//	}

}
