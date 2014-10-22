package kr.co.darkkaiser.jc;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class JapanCharacterActivity extends Activity {

	private boolean mShowYoum = false;
	private boolean mShowHiragana = false;
	private boolean mShowGatakana = false;
	private boolean mVibrateNextCharacter = true;

	private int mCurrentShowIndex = -1;
	private boolean mIsCurrentShowHiragana = false;

	private Random mRandom = new Random();
	private List<String> mKorea = null;
	private List<String> mJapanHiragana = null;
	private List<String> mJapanGatagana = null;
	
	private SharedPreferences mPreferences = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jc_main);

        mPreferences = getSharedPreferences("jc_setup", MODE_PRIVATE);

        RelativeLayout characterContainer = (RelativeLayout)findViewById(R.id.character_container);
        characterContainer.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
			    case MotionEvent.ACTION_DOWN:
			        break;
			    case MotionEvent.ACTION_UP:
					if (v.getId() == R.id.character_container) {
						if (mCurrentShowIndex != -1) {
							Dialog dlg = new Dialog(JapanCharacterActivity.this, R.style.NoTitleDialog);
							dlg.setContentView(R.layout.jc_description);
							dlg.setCanceledOnTouchOutside(true);
							
							TextView descriptionTv = (TextView)dlg.findViewById(R.id.description);
							if (mIsCurrentShowHiragana == true)
								descriptionTv.setText(String.format("발음 : %s\n가타카나 : %s", mKorea.get(mCurrentShowIndex), mJapanGatagana.get(mCurrentShowIndex)));
							else
								descriptionTv.setText(String.format("발음 : %s\n히라가나 : %s", mKorea.get(mCurrentShowIndex), mJapanHiragana.get(mCurrentShowIndex)));

							dlg.show();
						}
					}
					
			        v.performClick();
			        break;
			    default:
			        break;
			    }

				return true;
			}
		});

        Button nextButton = (Button)findViewById(R.id.nextCharacter);
        nextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showNextCharactor();
				
				if (mVibrateNextCharacter == true) {
					// 진동을 발생시킨다.
					Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(10);
				}
			}
		});
        
        // 데이터를 초기화한다.
        mKorea = Arrays.asList(getResources().getStringArray(R.array.character_korea));
        mJapanHiragana = Arrays.asList(getResources().getStringArray(R.array.character_japan_hiragana));
        mJapanGatagana = Arrays.asList(getResources().getStringArray(R.array.character_japan_gatagana));
        
        // 프로그램이 처음 시작될 때 한자를 보이도록 한다.
        init();
        showNextCharactor();
    }
    
    private void init() {
    	assert mPreferences != null;

    	// 프로그램을 초기화합니다.
    	mShowYoum = mPreferences.getBoolean("chk_youm", true);
    	mShowHiragana = mPreferences.getBoolean("chk_hiragana", true);
    	mShowGatakana = mPreferences.getBoolean("chk_gatakana", true);
    	mVibrateNextCharacter = mPreferences.getBoolean("vibrate_next_character", true);

    	TextView characterMean = (TextView)findViewById(R.id.character_mean);
    	if (mPreferences.getBoolean("show_character_mean", false) == true) {
        	characterMean.setVisibility(View.VISIBLE);
    	} else {
        	characterMean.setVisibility(View.GONE);
    	}
    }

    private void showNextCharactor() {
    	if (mShowHiragana == false && mShowGatakana == false) {
    		Toast.makeText(this, "암기 대상 문자가 선택되지 않았습니다. 환경설정 페이지에서 선택하여 주세요!", Toast.LENGTH_LONG).show();
    		return;
    	}

		if (mShowYoum == true) {
			mCurrentShowIndex = mRandom.nextInt(104/* 요음을 포함한 일본어 글자 개수 */);
		} else {
			mCurrentShowIndex = mRandom.nextInt(71/* 요음을 제외한 일본어 글자 개수 */);
		}

    	TextView character = (TextView)findViewById(R.id.character);
    	TextView characterMean = (TextView)findViewById(R.id.character_mean);

    	if (mShowHiragana == true && mShowGatakana == true) {
    		if (mRandom.nextInt(2) == 0) {
    			mIsCurrentShowHiragana = true;
    			character.setText(mJapanHiragana.get(mCurrentShowIndex));
    		} else {
    			mIsCurrentShowHiragana = false;
    			character.setText(mJapanGatagana.get(mCurrentShowIndex));
    		}
    	} else if (mShowHiragana == true) {
			mIsCurrentShowHiragana = true;
			character.setText(mJapanHiragana.get(mCurrentShowIndex));
    	} else if (mShowGatakana == true) {
			mIsCurrentShowHiragana = false;
			character.setText(mJapanGatagana.get(mCurrentShowIndex));
    	}

    	characterMean.setText(mKorea.get(mCurrentShowIndex));
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem item = menu.add(0, 1, 0, getString(R.string.environment_setting));
		item.setIcon(android.R.drawable.ic_menu_preferences);
		return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			// 설정 페이지를 띄운다.
			startActivityForResult(new Intent(this, SetupActivity.class), 0);

			return true;
		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 0) {
			init();
			showNextCharactor();
		}
	}

}
