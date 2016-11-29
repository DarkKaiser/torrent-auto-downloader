package kr.co.darkkaiser.jc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity {

    private static final int MSG_CUSTOM_EVT_APP_FINISH_STANDBY = 1;

    private boolean mShowYoum = false;
    private boolean mShowHiragana = false;
    private boolean mShowGatakana = false;
    private boolean mVibrateNextCharacter = true;

    private int mCurrentShowIndex = -1;

    private Random mRandom = new Random();
    private List<String> mKorea = null;
    private List<String> mJapanHiragana = null;
    private List<String> mJapanGatagana = null;

    private SharedPreferences mPreferences = null;
    private Handler mCustomEventHandler = new CustomEventHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_japan_character);

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
                                TextView contentView = new TextView(MainActivity.this);
                                contentView.setPadding(0, 0, 0, 0);
                                contentView.setGravity(Gravity.CENTER);
                                contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                contentView.setTypeface(Typeface.SERIF);
                                contentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                                contentView.setText(String.format("%s / %s\n%s", mJapanHiragana.get(mCurrentShowIndex), mJapanGatagana.get(mCurrentShowIndex), mKorea.get(mCurrentShowIndex)));
                                contentView.setTextColor(ContextCompat.getColor(v.getContext(), R.color.am_description_text));

                                MaterialDialog dialog = new MaterialDialog(MainActivity.this)
                                        .setCanceledOnTouchOutside(true)
                                        .setContentView(contentView);

                                dialog.show();
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

        Button nextButton = (Button)findViewById(R.id.next_character);
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showNextCharacter();

                if (mVibrateNextCharacter) {
                    // 진동을 발생시킨다.
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(10);
                }
            }
        });
        nextButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.am_next_button_background), PorterDuff.Mode.MULTIPLY);

        // 데이터를 초기화한다.
        mKorea = Arrays.asList(getResources().getStringArray(R.array.character_korea));
        mJapanHiragana = Arrays.asList(getResources().getStringArray(R.array.character_japan_hiragana));
        mJapanGatagana = Arrays.asList(getResources().getStringArray(R.array.character_japan_gatagana));

        // 프로그램이 처음 시작될 때 한자를 보이도록 한다.
        init();
        showNextCharacter();
    }

    private void init() {
        assert mPreferences != null;

        // 프로그램을 초기화합니다.
        mShowYoum = mPreferences.getBoolean("chk_youm", getResources().getBoolean(R.bool.chk_youm_default_value));
        mShowHiragana = mPreferences.getBoolean("chk_hiragana", getResources().getBoolean(R.bool.chk_hiragana_default_value));
        mShowGatakana = mPreferences.getBoolean("chk_gatakana", getResources().getBoolean(R.bool.chk_gatakana_default_value));
        mVibrateNextCharacter = mPreferences.getBoolean("vibrate_next_character", getResources().getBoolean(R.bool.vibrate_next_character_default_value));

        TextView characterMean = (TextView)findViewById(R.id.character_mean);
        if (mPreferences.getBoolean("show_character_mean", getResources().getBoolean(R.bool.show_character_mean_default_value))) {
            characterMean.setVisibility(View.VISIBLE);
        } else {
            characterMean.setVisibility(View.GONE);
        }
    }

    private void showNextCharacter() {
        if (!mShowHiragana && !mShowGatakana) {
            Toast.makeText(this, "암기 대상 문자가 선택되지 않았습니다. 환경설정 페이지에서 선택하여 주세요!", Toast.LENGTH_LONG).show();
            return;
        }

        if (mShowYoum) {
            mCurrentShowIndex = mRandom.nextInt(104/* 요음을 포함한 일본어 글자 개수 */);
        } else {
            mCurrentShowIndex = mRandom.nextInt(71/* 요음을 제외한 일본어 글자 개수 */);
        }

        TextView character = (TextView)findViewById(R.id.character);
        TextView characterMean = (TextView)findViewById(R.id.character_mean);

        if (mShowHiragana && mShowGatakana) {
            if (mRandom.nextInt(2) == 0) {
                character.setText(mJapanHiragana.get(mCurrentShowIndex));
            } else {
                character.setText(mJapanGatagana.get(mCurrentShowIndex));
            }
        } else if (mShowHiragana) {
            character.setText(mJapanHiragana.get(mCurrentShowIndex));
        } else if (mShowGatakana) {
            character.setText(mJapanGatagana.get(mCurrentShowIndex));
        }

        characterMean.setText(mKorea.get(mCurrentShowIndex));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(0, 1, 0, getString(R.string.activity_environment_setting_label));
        item.setIcon(android.R.drawable.ic_menu_preferences);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
   		if (item.getItemId() == 1) {
			// 설정 페이지를 띄운다.
			startActivityForResult(new Intent(this, SettingsActivity.class), 0);

			return true;
		}

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 0) {
            init();
            showNextCharacter();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mCustomEventHandler.hasMessages(MSG_CUSTOM_EVT_APP_FINISH_STANDBY)) {
            mCustomEventHandler.sendEmptyMessageAtTime(MSG_CUSTOM_EVT_APP_FINISH_STANDBY, SystemClock.uptimeMillis() + 2000);
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        super.onBackPressed();
    }

    private static class CustomEventHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_CUSTOM_EVT_APP_FINISH_STANDBY:
                    // 수행하는 작업 없음
                    break;

                default:
                    throw new RuntimeException("Unknown message " + msg);
            }

            super.handleMessage(msg);
        }
    }

}
