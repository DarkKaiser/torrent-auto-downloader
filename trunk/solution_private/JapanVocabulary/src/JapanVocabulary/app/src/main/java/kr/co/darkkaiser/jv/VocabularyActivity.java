package kr.co.darkkaiser.jv;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.androidquery.AQuery;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import kr.co.darkkaiser.jv.common.Constants;
import kr.co.darkkaiser.jv.util.ByteUtils;
import kr.co.darkkaiser.jv.util.FileHash;
import kr.co.darkkaiser.jv.view.detail.DetailActivity;
import kr.co.darkkaiser.jv.view.list.SearchListActivity;
import kr.co.darkkaiser.jv.view.settings.SettingsActivity;
import kr.co.darkkaiser.jv.vocabulary.MemorizeOrder;
import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;
import kr.co.darkkaiser.jv.vocabulary.data.VocabularyDbManager;
import kr.co.darkkaiser.jv.vocabulary.data.VocabularyManager;
import kr.co.darkkaiser.jv.vocabulary.list.internal.MemorizeTargetVocabularyList;

public class VocabularyActivity extends ActionBarActivity implements OnTouchListener {

    private static final String TAG = "VocabularyActivity";

	private static final int MSG_TOAST_SHOW = 1;
	private static final int MSG_PROGRESS_DIALOG_REFRESH = 2;
	private static final int MSG_VOCABULARY_MEMORIZE_START = 3;
	private static final int MSG_NETWORK_DISCONNECTED_DIALOG_SHOW = 4;
	private static final int MSG_VOCABULARY_DATA_DOWNLOAD_QUESTION = 5;
	private static final int MSG_VOCABULARY_DATA_DOWNLOAD_START = 6;
	private static final int MSG_VOCABULARY_DATA_DOWNLOAD_END = 7;
	private static final int MSG_VOCABULARY_DATA_DOWNLOADING = 8;
	private static final int MSG_VOCABULARY_DATA_SHOW_VOCABULARY_UPDATE_INFO_DIALOG = 9;
	private static final int MSG_VOCABULARY_SEEKBAR_VISIBILITY = 10;
	private static final int MSG_VOCABULARY_MAINBAR_VISIBILITY = 11;

    private static final int MSG_CUSTOM_EVT_TAP = 1;
    private static final int MSG_CUSTOM_EVT_LONG_PRESS = 2;
    private static final int MSG_CUSTOM_EVT_APP_FINISH_STANDBY = 3;

    private static final int REQ_CODE_SEARCH_MEMORIZE_VOCABULARY = 1;
    private static final int REQ_CODE_VOCABULARY_DETAIL_INFO = 2;
    private static final int REQ_CODE_OPEN_SETTINGS_ACTIVITY = 3;

    private static final String EXTRA_VOCABULARY_UPDATE_INFO = "VOCABULARY_UPDATE_INFO";

    // 롱 터치를 판단하는 시간 값
	private static final int LONG_PRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();

    // 긴 작업동안 화면에 작업중임을 보여 줄 대화상자
	private ProgressDialog mProgressDialog = null;

	// 암기 단어 관련 정보 객체
	private MemorizeTargetVocabularyList mMemorizeTargetVocabularyList = new MemorizeTargetVocabularyList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        AQuery aq = new AQuery(this);

        SeekBar vocabularySeekBar = (SeekBar)findViewById(R.id.av_vocabulary_seekbar);
        vocabularySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true)
                    showMemorizeVocabulary(mMemorizeTargetVocabularyList.movePosition(progress));
            }
        });

        RelativeLayout vocabularyContainer = (RelativeLayout)findViewById(R.id.av_vocabulary_container);
        vocabularyContainer.setOnTouchListener(this);

        TextSwitcher tswVocabulary = (TextSwitcher)findViewById(R.id.av_vocabulary);
        tswVocabulary.setOnTouchListener(this);
        tswVocabulary.setFactory(new ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(VocabularyActivity.this);
                tv.setGravity(Gravity.CENTER);
                tv.setTypeface(Typeface.SERIF);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 70);
                tv.setLayoutParams(new TextSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                return tv;
            }
        });

        TextSwitcher tswVocabularyTranslation = (TextSwitcher)findViewById(R.id.av_vocabulary_translation);
        tswVocabularyTranslation.setOnTouchListener(this);
        tswVocabularyTranslation.setFactory(new ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(VocabularyActivity.this);
                tv.setGravity(Gravity.CENTER);
                tv.setTypeface(Typeface.SERIF);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                tv.setLayoutParams(new TextSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                return tv;
            }
        });

        aq.id(R.id.av_prev_vocabulary).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
                if (preferences.getBoolean(getString(R.string.as_vibrate_next_vocabulary_key), getResources().getBoolean(R.bool.vibrate_next_vocabulary_default_value)) == true) {
                    // 진동을 발생시킨다.
                    Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(30);
                }

                showPrevMemorizeVocabulary();
            }
        });

        aq.id(R.id.av_next_vocabulary).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
                if (preferences.getBoolean(getString(R.string.as_vibrate_next_vocabulary_key), getResources().getBoolean(R.bool.vibrate_next_vocabulary_default_value)) == true) {
                    // 진동을 발생시킨다.
                    Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(30);
                }

                showNextMemorizeVocabulary();
            }
        });

        // 단어DB 관리자 객체를 초기화한다.
        if (VocabularyDbManager.getInstance().init(this) == false) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.cannot_access_db_storage))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();

            return;
        }

        // 환경설정 정보를 읽어들인다.
        resetSettings();

        // 단어DB에서 단어를 읽어들입니다.
        loadVocabularyDb();
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_vocabulary, menu);
		return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.av_search_memorize_vocabulary:
			Intent intent = new Intent(this, SearchListActivity.class);
			startActivityForResult(intent, REQ_CODE_SEARCH_MEMORIZE_VOCABULARY);
			return true;

		case R.id.av_rememorize_all:
			assert mProgressDialog == null;

			// 데이터를 처리가 끝날 때가지 프로그레스 대화상자를 보인다.
			mProgressDialog = ProgressDialog.show(this, null, "요청하신 작업을 처리 중입니다.", true, false);

	   		new Thread() {
				@Override
	   			public void run() {
                    // @@@@@
					// 암기 대상 단어들을 모두 암기미완료로 리셋한다.
					VocabularyManager.getInstance().rememorizeAllMemorizeTarget();

			        // 암기할 단어 데이터를 로드합니다.
			        loadMemorizeTargetVocabularyData(false);

					Message msg = Message.obtain();
					msg.what = MSG_VOCABULARY_MEMORIZE_START;
					mVocabularyDataLoadHandler.sendMessage(msg);
	   			}
	   		}
	   		.start();

			return true;

		case R.id.av_open_settings_activity:
			startActivityForResult(new Intent(this, SettingsActivity.class), REQ_CODE_OPEN_SETTINGS_ACTIVITY);
			return true;
		}

		return false;
	}

	@Override
    // @@@@@
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQ_CODE_SEARCH_MEMORIZE_VOCABULARY) {
			assert mProgressDialog == null;

			// 환경설정 값이 바뀌었는지 확인한다.
			if (resultCode == 0)
				return;

			boolean mustReloadJvData = false;

			// 환경설정의 값이 변경된 경우는 해당 값을 다시 읽어들인다.
			if ((resultCode & SearchListActivity.ACTIVITY_RESULT_PREFERENCE_CHANGED) == SearchListActivity.ACTIVITY_RESULT_PREFERENCE_CHANGED) {
                 resetSettings();
                if (mMemorizeTargetVocabularyList.getCount() == 0)
                    mustReloadJvData = true;
            }
			if ((resultCode & SearchListActivity.ACTIVITY_RESULT_DATA_CHANGED) == SearchListActivity.ACTIVITY_RESULT_DATA_CHANGED)
				mustReloadJvData = true;

			if (mustReloadJvData == true) {
				// 데이터를 로드하는 중임을 나타내는 프로그레스 대화상자를 보인다.
				mProgressDialog = ProgressDialog.show(this, null, "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.", true, false);

		   		new Thread() {
					@Override
		   			public void run() {
				        // 암기할 단어 데이터를 로드합니다.
				        loadMemorizeTargetVocabularyData(false);

						Message msg = Message.obtain();
						msg.what = MSG_VOCABULARY_MEMORIZE_START;
						mVocabularyDataLoadHandler.sendMessage(msg);
		   			};
		   		}
		   		.start();
			} else {
				// '암기 대상 항목'등의 값이 변경되었을 수도 있으므로 현재 보여지고 있는 단어를 리프레쉬한다.
                showCurrentMemorizeVocabulary();
			}
		} else if (requestCode == REQ_CODE_OPEN_SETTINGS_ACTIVITY) {
            resetSettings();
			if (mMemorizeTargetVocabularyList.getCount() == 0) {
				// 데이터를 로드하는 중임을 나타내는 프로그레스 대화상자를 보인다.
				mProgressDialog = ProgressDialog.show(this, null, "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.", true, false);

		   		new Thread() {
					@Override
		   			public void run() {
				        // 암기할 단어 데이터를 로드합니다.
				        loadMemorizeTargetVocabularyData(false);

						Message msg = Message.obtain();
						msg.what = MSG_VOCABULARY_MEMORIZE_START;
						mVocabularyDataLoadHandler.sendMessage(msg);
		   			};
		   		}
		   		.start();
		   	} else {
				// '암기 대상 항목'등의 값이 변경되었을 수도 있으므로 현재 보여지고 있는 단어를 리프레쉬한다.
                showCurrentMemorizeVocabulary();
			}
		} else if (requestCode == REQ_CODE_VOCABULARY_DETAIL_INFO) {
			DetailActivity.setSeekVocabularyList(null);
			if (resultCode == DetailActivity.ACTIVITY_RESULT_POSITION_CHANGED)
				showCurrentMemorizeVocabulary();
		}
	}

    private void resetSettings() {
		SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);

		TextSwitcher tswVocabulary = (TextSwitcher)findViewById(R.id.av_vocabulary);
		TextSwitcher tswVocabularyTranslation = (TextSwitcher)findViewById(R.id.av_vocabulary_translation);
		if (preferences.getBoolean(getString(R.string.as_fade_effect_next_vocabulary_key), getResources().getBoolean(R.bool.fade_effect_next_vocabulary_default_value)) == true) {
			tswVocabulary.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
			tswVocabulary.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
			tswVocabularyTranslation.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
			tswVocabularyTranslation.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
		} else {
			tswVocabulary.setInAnimation(null);
			tswVocabulary.setOutAnimation(null);
			tswVocabularyTranslation.setInAnimation(null);
			tswVocabularyTranslation.setOutAnimation(null);
		}

		if (preferences.getBoolean(getString(R.string.as_show_vocabulary_translation_key), getResources().getBoolean(R.bool.show_vocabulary_translation_default_value)) == false)
            tswVocabularyTranslation.setVisibility(View.GONE);
        else
            tswVocabularyTranslation.setVisibility(View.VISIBLE);

        mMemorizeTargetVocabularyList.resetMemorizeSettings(this, preferences);
	}

    private void showCurrentMemorizeVocabulary() {
		Vocabulary vocabulary = mMemorizeTargetVocabularyList.getVocabulary();

		if (vocabulary != null)
			showMemorizeVocabulary(vocabulary);

		// 암기 단어의 위치를 가리키는 SeekBar의 위치를 조정한다.
		SeekBar vocabularySeekBar = (SeekBar)findViewById(R.id.av_vocabulary_seekbar);
		vocabularySeekBar.setProgress(mMemorizeTargetVocabularyList.getPosition());
	}

    private void showPrevMemorizeVocabulary() {
		StringBuilder sbErrMessage = new StringBuilder();
		Vocabulary vocabulary = mMemorizeTargetVocabularyList.previousVocabulary(sbErrMessage);

		if (vocabulary == null) {
			if (sbErrMessage.length() > 0)
				Toast.makeText(this, sbErrMessage.toString(), Toast.LENGTH_SHORT).show();
		} else {
			showMemorizeVocabulary(vocabulary);
		}

		// 암기 단어의 위치를 가리키는 SeekBar의 위치를 조정한다.
		SeekBar vocabularySeekBar = (SeekBar)findViewById(R.id.av_vocabulary_seekbar);
		vocabularySeekBar.setProgress(mMemorizeTargetVocabularyList.getPosition());
	}

	private void showNextMemorizeVocabulary() {
		StringBuilder sbErrMessage = new StringBuilder();
		Vocabulary vocabulary = mMemorizeTargetVocabularyList.nextVocabulary(sbErrMessage);

		if (vocabulary == null && sbErrMessage.length() > 0)
			Toast.makeText(this, sbErrMessage.toString(), Toast.LENGTH_SHORT).show();

		showMemorizeVocabulary(vocabulary);

		// 암기 단어의 위치를 가리키는 SeekBar의 위치를 조정한다.
		SeekBar vocabularySeekBar = (SeekBar)findViewById(R.id.av_vocabulary_seekbar);
		vocabularySeekBar.setProgress(mMemorizeTargetVocabularyList.getPosition());
	}

    private void showMemorizeVocabulary(Vocabulary vocabulary) {
        AQuery aq = new AQuery(this);

		TextSwitcher tswVocabulary = (TextSwitcher)findViewById(R.id.av_vocabulary);
		TextSwitcher tswVocabularyTranslation = (TextSwitcher)findViewById(R.id.av_vocabulary_translation);

		// 글자가 길어서 컨트롤의 크기가 커질 경우 한 템포씩 늦게 컨트롤의 크기가 줄어들므로 먼저 컨트롤의 크기를 줄이고 나서 값을 넣는다.
		tswVocabulary.setText("");
		tswVocabularyTranslation.setText("");

		if (vocabulary != null) {
			if (vocabulary.isMemorizeCompleted() == true)
                aq.id(R.id.av_memorize_completed_info).text(String.format("총 %d회 암기완료", vocabulary.getMemorizeCompletedCount())).textColor(getResources().getColor(R.color.av_memorize_completed_count)).visible();
			else
                aq.id(R.id.av_memorize_completed_info).invisible();

			switch (mMemorizeTargetVocabularyList.getMemorizeTarget()) {
			case VOCABULARY:
				tswVocabulary.setText(vocabulary.getVocabulary());
				break;

			case VOCABULARY_GANA:
				tswVocabulary.setText(vocabulary.getVocabularyGana());
				break;
				
			default:
				assert false;
				break;
			}

			tswVocabularyTranslation.setText(vocabulary.getVocabularyTranslation());
		} else {
            aq.id(R.id.av_memorize_completed_info).invisible();
		}
	}

    private void updateMemorizeVocabularyInfo() {
        AQuery aq = new AQuery(this);

		String memorizeVocabularyInfo = mMemorizeTargetVocabularyList.getMemorizeVocabularyInfo();
		if (TextUtils.isEmpty(memorizeVocabularyInfo) == false)
            aq.id(R.id.av_memorize_vocabulary_info).text(memorizeVocabularyInfo);
		else
            aq.id(R.id.av_memorize_vocabulary_info).text("");
	}

	@Override
    public void onBackPressed() {
		if (mCustomEventHandler.hasMessages(MSG_CUSTOM_EVT_APP_FINISH_STANDBY) == false) {
			mCustomEventHandler.sendEmptyMessageAtTime(MSG_CUSTOM_EVT_APP_FINISH_STANDBY, SystemClock.uptimeMillis() + 2000);
			Toast.makeText(this, getString(R.string.av_app_terminate_when_back_press), Toast.LENGTH_SHORT).show();
			return;
		}

        // 화면에 현재 출력중인 암기단어의 위치를 저장하여 다음 실행시에 바로 보여지도록 한다.
		SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		mMemorizeTargetVocabularyList.writePosition(preferences);

		super.onBackPressed();
	}

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mMemorizeTargetVocabularyList.isValidPosition() == true &&
                (v.getId() == R.id.av_vocabulary_container || v.getId() == R.id.av_vocabulary || v.getId() == R.id.av_vocabulary_translation)) {

            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCustomEventHandler.removeMessages(MSG_CUSTOM_EVT_LONG_PRESS);
                mCustomEventHandler.sendEmptyMessageAtTime(MSG_CUSTOM_EVT_LONG_PRESS, event.getDownTime() + LONG_PRESS_TIMEOUT);
                break;

            case MotionEvent.ACTION_MOVE:
                // 약간의 움직임만으로 메시지가 제거되므로 주석처리한다.
                // mHandler.removeMessages(LONG_PRESS);
                break;

            case MotionEvent.ACTION_UP:
                // MSG_TOUCHEVT_LONG_PRESS 메시지가 처리전이라면 MSG_TOUCHEVT_TAP으로 인식되어 처리된다.
                if (mCustomEventHandler.hasMessages(MSG_CUSTOM_EVT_LONG_PRESS) == true) {
                    mCustomEventHandler.removeMessages(MSG_CUSTOM_EVT_LONG_PRESS);
                    mCustomEventHandler.sendEmptyMessage(MSG_CUSTOM_EVT_TAP);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                mCustomEventHandler.removeMessages(MSG_CUSTOM_EVT_LONG_PRESS);
                break;
            }
        } else {
            mCustomEventHandler.removeMessages(MSG_CUSTOM_EVT_LONG_PRESS);
        }

        return true;
    }

    private Handler mCustomEventHandler = new Handler() {
		@Override
    	public void handleMessage(Message msg){
    		switch (msg.what) {
            case MSG_CUSTOM_EVT_LONG_PRESS:
                if (mMemorizeTargetVocabularyList.isValidPosition() == true) {
                    // 진동을 발생시킨다.
                    Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(30);

                    new AlertDialog.Builder(VocabularyActivity.this)
                        .setTitle(getString(R.string.av_memorize_completed_ad_title))
                        .setMessage(getString(R.string.av_memorize_completed_ad_message))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 진동을 발생시킨다.
                                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(30);

                                mMemorizeTargetVocabularyList.setMemorizeCompleted();
                                VocabularyManager.getInstance().writeUserVocabularyInfo();

                                updateMemorizeVocabularyInfo();
                                showNextMemorizeVocabulary();

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                }
                break;

            case MSG_CUSTOM_EVT_TAP:
                long idx = mMemorizeTargetVocabularyList.getVocabularyIdx();
                if (idx != -1) {
                    if (findViewById(R.id.av_vocabulary_seekbar).getVisibility() == View.VISIBLE)
                        DetailActivity.setSeekVocabularyList(mMemorizeTargetVocabularyList);
                    else
                        DetailActivity.setSeekVocabularyList(null);

                    // 상세정보 페이지를 연다.
                    Intent intent = new Intent(VocabularyActivity.this, DetailActivity.class);
                    intent.putExtra("idx", idx);
                    startActivityForResult(intent, REQ_CODE_VOCABULARY_DETAIL_INFO);
                }
                break;

            case MSG_CUSTOM_EVT_APP_FINISH_STANDBY:
                // 수행하는 작업 없음
                break;

            default:
                 throw new RuntimeException("Unknown message " + msg);
    		}
    	}
    };

	private void adjustVocabularySeekBar(SharedPreferences preferences) {
		assert preferences != null;

        Message msg = Message.obtain();
        msg.what = MSG_VOCABULARY_SEEKBAR_VISIBILITY;

        int memorizeVocabularyCount = mMemorizeTargetVocabularyList.getCount();

        // '랜덤' 모드이거나 암기 단어가 하나도 없는 경우에는 암기 단어의 위치를 가리키는 SeekBar를 화면에 보이지 않도록 한다.
		if (memorizeVocabularyCount == 0 ||
                Integer.parseInt(preferences.getString(getString(R.string.as_memorize_order_key), String.format("%d", getResources().getInteger(R.integer.memorize_order_default_value)))) == MemorizeOrder.RANDOM.ordinal()) {
			msg.arg1 = View.INVISIBLE;
		} else {
			msg.arg1 = View.VISIBLE;

			SeekBar vocabularySeekBar = (SeekBar)findViewById(R.id.av_vocabulary_seekbar);
			vocabularySeekBar.setProgress(0);
			vocabularySeekBar.setMax(memorizeVocabularyCount - 1);
			vocabularySeekBar.incrementProgressBy(1);
		}

		mVocabularyDataLoadHandler.sendMessage(msg);
	}

    // @@@@@
    private void loadVocabularyDb() {
        // 시작시 단어DB를 업데이트할지의 여부를 확인한 후, 단어DB를 업데이트한다.
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        boolean isVocabularyUpdateOnStarted = preferences.getBoolean(getString(R.string.as_vocabulary_update_on_started_key), getResources().getBoolean(R.bool.vocabulary_update_on_started_default_value));

        // 현재 인터넷에 연결되어 있는지의 여부를 확인한 후, 단어DB를 업데이트한다.
        boolean isNowConnectedNetwork = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting() == true) isNowConnectedNetwork = true;

        // 프로그레스 대화상자를 보인다.
        if (isNowConnectedNetwork == true && isVocabularyUpdateOnStarted == true)
            mProgressDialog = ProgressDialog.show(this, null, "단어 DB의 업데이트 여부를 확인하는 중 입니다.", true, false);
        else
            mProgressDialog = ProgressDialog.show(this, null, "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.", true, false);

        new Thread() {
            // 현재 네트워크(3G 혹은 와이파이)에 연결되어 있는지의 여부를 나타낸다.
            private boolean mIsNowConnectedNetwork = false;

            private boolean mIsVocabularyUpdateOnStarted = true;

            public Thread setValues(boolean isNowNetworkConnected, boolean isVocabularyUpdateOnStarted) {
                mIsNowConnectedNetwork = isNowNetworkConnected;
                mIsVocabularyUpdateOnStarted = isVocabularyUpdateOnStarted;
                return this;
            }

            // @@@@@
            @Override
            public void run() {
                if (mIsNowConnectedNetwork == true && mIsVocabularyUpdateOnStarted == true) {
                    ArrayList<String> newVocaInfo = checkNewVocabularyDb();
                    String newVocabularyDbVersion = "", newVocabularyDbFileHash = "";

                    if (newVocaInfo != null) {
                        if (newVocaInfo.size() >= 1)
                            newVocabularyDbVersion = newVocaInfo.get(0);

                        if (newVocaInfo.size() >= 2)
                            newVocabularyDbFileHash = newVocaInfo.get(1);
                    }

                    if (newVocabularyDbVersion != null && TextUtils.isEmpty(newVocabularyDbVersion) == false) {
                        // 현재 연결된 네트워크가 3G 연결인지 확인한다.
                        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                        if (mobileNetworkInfo != null && mobileNetworkInfo.isConnectedOrConnecting() == true) {
                            Bundle bundle = new Bundle();
                            bundle.putString("NEW_VOCABULARY_DB_VERSION", newVocabularyDbVersion);
                            bundle.putString("NEW_VOCABULARY_DB_FILE_HASH", newVocabularyDbFileHash);

                            Message msg = Message.obtain();
                            msg.what = MSG_VOCABULARY_DATA_DOWNLOAD_QUESTION;
                            msg.setData(bundle);

                            mVocabularyDataLoadHandler.sendMessage(msg);
                        } else {
                            // 새로운 단어 DB로 갱신합니다.
                            boolean updateSucceeded = updateVocabularyDb(newVocabularyDbVersion, newVocabularyDbFileHash);

                            // 단어 데이터를 초기화한 후, 암기를 시작합니다.
                            initVocabularyDataAndStartMemorize(true, true, updateSucceeded);
                        }
                    } else {
                        // 단어 데이터를 초기화한 후, 암기를 시작합니다.
                        initVocabularyDataAndStartMemorize(true, true, false);
                    }
                } else {
                    // 단어 데이터를 초기화한 후, 암기를 시작합니다.
                    initVocabularyDataAndStartMemorize(mIsNowConnectedNetwork, mIsVocabularyUpdateOnStarted, false);
                }
            }
        }
        .setValues(isNowConnectedNetwork, isVocabularyUpdateOnStarted)
        .start();
    }

    // @@@@@
    private ArrayList<String> checkNewVocabularyDb() {
        // 로컬 단어DB의 버전정보를 구한다.
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        String localDbVersion = preferences.getString(Constants.SP_DB_VERSION, "");

        try {
            ArrayList<String> vocaDbInfo = VocabularyDbManager.getInstance().getLatestVocabularyDbInfoList();
            assert vocaDbInfo.size() == 2;

            String newVocabularyDbVersion = "";
            if (vocaDbInfo.size() >= 1)
                newVocabularyDbVersion = vocaDbInfo.get(0);

			// 단어 DB의 갱신 여부를 확인한다.
			if (newVocabularyDbVersion != null
                    && TextUtils.isEmpty(newVocabularyDbVersion) == false
                    && newVocabularyDbVersion.equals(localDbVersion) == false) {
				return vocaDbInfo;
			}
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());

            Message msg = Message.obtain();
            msg.what = MSG_TOAST_SHOW;
            msg.obj = "단어DB의 업데이트  여부를 확인할 수 없습니다.";
            mVocabularyDataLoadHandler.sendMessage(msg);
        }

        return null;
    }

    // @@@@@
    private void updateAndInitVocabularyDataOnMobileNetwork(String newVocabularyDbVersion, String newVocabularyDbFileHash, boolean isUpdateVocabularyDb) {
        if (isUpdateVocabularyDb)
            mProgressDialog = ProgressDialog.show(VocabularyActivity.this, null, "단어 DB를 업데이트하고 있습니다.", true, false);
        else
            mProgressDialog = ProgressDialog.show(VocabularyActivity.this, null, "암기 할 단어를 불러오고 있습니다.\n잠시만 기다려주세요.", true, false);

        new Thread() {
            // 단어 DB를 업데이트 할지에 대한 플래그
            private boolean mIsUpdateVocabularyDb = false;

            // 새로 업데이트 할 단어 DB의 버전 및 파일 해쉬값
            private String mNewVocabularyDbVersion = null;
            private String mNewVocabularyDbFileHash = null;

            public Thread setValues(String newVocabularyDbVersion, String newVocabularyDbFileHash, boolean isUpdateVocabularyDb) {
                assert TextUtils.isEmpty(newVocabularyDbVersion) == false;

                mIsUpdateVocabularyDb = isUpdateVocabularyDb;
                mNewVocabularyDbVersion = newVocabularyDbVersion;
                mNewVocabularyDbFileHash = newVocabularyDbFileHash;

                return this;
            }

            @Override
            public void run() {
                boolean updateSucceeded = false;
                if (mIsUpdateVocabularyDb == true) {
                    // 새로운 단어 DB로 갱신합니다.
                    updateSucceeded = updateVocabularyDb(mNewVocabularyDbVersion, mNewVocabularyDbFileHash);
                }

                // 단어 데이터를 초기화한 후, 암기를 시작합니다.
                initVocabularyDataAndStartMemorize(true, true, updateSucceeded);
            };
        }
        .setValues(newVocabularyDbVersion, newVocabularyDbFileHash, isUpdateVocabularyDb)
        .start();
    }

    // @@@@@
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean updateVocabularyDb(String newVocabularyDbVersion, String newVocabularyDbFileHash) {
        assert TextUtils.isEmpty(newVocabularyDbVersion) == false;

        Message msg = null;
        boolean updateSucceeded = false;
        String jvDbPath = VocabularyDbManager.getInstance().getVocabularyDbFilePath();

        // 단어 DB 파일을 내려받는다.
        try {
            URL url = new URL(Constants.VOCABULARY_DB_DOWNLOAD_URL);

            URLConnection con = url.openConnection();
            int contentLength = con.getContentLength();
            BufferedInputStream bis = new BufferedInputStream(con.getInputStream());

            // 다운로드 받을 단어 DB의 크기에 대한 정보를 프로그레스바에 출력한다.
            Bundle bundle = new Bundle();
            bundle.putInt("TOTAL_VOCABULARY_DB_SIZE", contentLength);

            msg = Message.obtain();
            msg.what = MSG_VOCABULARY_DATA_DOWNLOAD_START;
            msg.setData(bundle);
            mVocabularyDataLoadHandler.sendMessage(msg);

            // 해당 메시지의 처리가 완료될 때까지 대기한다.
            while (mVocabularyDataLoadHandler.hasMessages(MSG_VOCABULARY_DATA_DOWNLOAD_START) == true) {
                Thread.sleep(10);
            }

            int readBytes = 0;
            byte[] bytesIn = new byte[1024];
            ByteArrayBuffer baf = new ByteArrayBuffer(1024);

            while ((readBytes = bis.read(bytesIn)) >= 0) {
                baf.append(bytesIn, 0, readBytes);

                // 현재까지 받은 단어 DB의 크기 정보를 프로그레스바에 출력한다.
                bundle = new Bundle();
                bundle.putInt("RECV_VOCABULARY_DB_SIZE", baf.length());

                msg = Message.obtain();
                msg.what = MSG_VOCABULARY_DATA_DOWNLOADING;
                msg.setData(bundle);
                mVocabularyDataLoadHandler.sendMessage(msg);
            }

            // 해당 메시지의 처리가 완료될 때까지 대기한다.
            while (mVocabularyDataLoadHandler.hasMessages(MSG_VOCABULARY_DATA_DOWNLOADING) == true) {
                Thread.sleep(10);
            }

            bis.close();

            if (contentLength > 0 && contentLength != baf.length()) {
                msg = Message.obtain();
                msg.what = MSG_TOAST_SHOW;
                msg.obj = "새로운 단어 DB의 업데이트가 실패하였습니다.";
                mVocabularyDataLoadHandler.sendMessage(msg);
            } else {
                if (TextUtils.isEmpty(newVocabularyDbFileHash) == true) {
                    File f = new File(jvDbPath);
                    f.delete();

                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(baf.toByteArray());
                    fos.close();

                    SharedPreferences mPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
                    mPreferences.edit().putString(Constants.SP_DB_VERSION, newVocabularyDbVersion).commit();
                } else {
                    File f = new File(String.format("%s.tmp", jvDbPath));
                    f.delete();

                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(baf.toByteArray());
                    fos.close();

                    // 다운로드 받은 파일의 해쉬값을 구하여 올바른 파일인지 비교한다.
                    boolean isValidationFile = true;
                    byte[] fileHashBytes = FileHash.getHash(f);
                    if (fileHashBytes != null) {
                        if (newVocabularyDbFileHash.equalsIgnoreCase(ByteUtils.toHexString(fileHashBytes)) == false)
                            isValidationFile = false;
                    }

                    if (isValidationFile == true) {
                        File dstFile = new File(jvDbPath);
                        dstFile.delete();
                        f.renameTo(dstFile);

                        updateSucceeded = true;
                        SharedPreferences mPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
                        mPreferences.edit().putString(Constants.SP_DB_VERSION, newVocabularyDbVersion).commit();
                    } else {
                        f.delete();

                        msg = Message.obtain();
                        msg.what = MSG_TOAST_SHOW;
                        msg.obj = "새로운 단어 DB의 업데이트가 실패하였습니다(에러 : 유효하지 않은 단어 DB 파일).";
                        mVocabularyDataLoadHandler.sendMessage(msg);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());

            msg = Message.obtain();
            msg.what = MSG_TOAST_SHOW;
            msg.obj = "새로운 단어 DB의 업데이트가 실패하였습니다.";
            mVocabularyDataLoadHandler.sendMessage(msg);
        }

        msg = Message.obtain();
        msg.what = MSG_VOCABULARY_DATA_DOWNLOAD_END;
        mVocabularyDataLoadHandler.sendMessage(msg);

        return updateSucceeded;
    }

    // @@@@@
    private void initVocabularyDataAndStartMemorize(boolean nowNetworkConnected, boolean isVocabularyUpdateOnStarted, boolean updateSucceeded) {
        Message msg = Message.obtain();
        msg.what = MSG_PROGRESS_DIALOG_REFRESH;
        msg.obj = "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.";
        mVocabularyDataLoadHandler.sendMessage(msg);

        // DB에서 단어 데이터를 읽어들인다.
        if (VocabularyManager.getInstance().initDataFromDB(this) == false) {
            msg = Message.obtain();
            msg.what = MSG_TOAST_SHOW;
            msg.obj = "단어 DB에서 데이터의 로딩이 실패하였습니다.";
            mVocabularyDataLoadHandler.sendMessage(msg);
        }

        // 암기할 단어 데이터를 로드합니다.
        loadMemorizeTargetVocabularyData(true);

        if (nowNetworkConnected == false && isVocabularyUpdateOnStarted == true) {
            msg = Message.obtain();
            msg.what = MSG_NETWORK_DISCONNECTED_DIALOG_SHOW;
            mVocabularyDataLoadHandler.sendMessage(msg);
        } else if (updateSucceeded == true) {
            SharedPreferences mPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
            long prevMaxIdx = mPreferences.getLong(Constants.JV_SPN_LAST_UPDATED_MAX_IDX, -1);

            StringBuilder sb = new StringBuilder();
            long newMaxIdx = VocabularyManager.getInstance().getUpdatedVocabularyInfo(prevMaxIdx, sb);

            if (newMaxIdx != -1) {
                mPreferences.edit().putLong(Constants.JV_SPN_LAST_UPDATED_MAX_IDX, newMaxIdx).commit();

                // 이전에 한번이상 업데이트 된 경우에 한에서 단어 업데이트 정보를 보인다.
                if (prevMaxIdx != -1) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_VOCABULARY_UPDATE_INFO, sb.toString());

                    msg = Message.obtain();
                    msg.what = MSG_VOCABULARY_DATA_SHOW_VOCABULARY_UPDATE_INFO_DIALOG;
                    msg.setData(bundle);

                    mVocabularyDataLoadHandler.sendMessage(msg);
                }
            }
        }

        // 단어 암기를 시작합니다.
        msg = Message.obtain();
        msg.what = MSG_VOCABULARY_MEMORIZE_START;
        mVocabularyDataLoadHandler.sendMessage(msg);
    }

    // @@@@@
    private void loadMemorizeTargetVocabularyData(boolean firstLoadVocabularyData) {
        Message msg = Message.obtain();
        msg.what = MSG_PROGRESS_DIALOG_REFRESH;
        msg.obj = "암기 할 단어를 불러오고 있습니다.\n잠시만 기다려주세요.";
        mVocabularyDataLoadHandler.sendMessage(msg);

        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        mMemorizeTargetVocabularyList.loadVocabularyData(preferences, firstLoadVocabularyData);

        adjustVocabularySeekBar(preferences);
    }

    private Handler mVocabularyDataLoadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_PROGRESS_DIALOG_REFRESH) {
                // @@@@@
                if (mProgressDialog != null)
                    mProgressDialog.setMessage((String)msg.obj);
            } else if (msg.what == MSG_VOCABULARY_MEMORIZE_START) {
                // @@@@@
                updateMemorizeVocabularyInfo();
                showCurrentMemorizeVocabulary();

                if (mProgressDialog != null)
                    mProgressDialog.dismiss();

                mProgressDialog = null;

                // 단어 정보 헤더가 화면에 보이도록 메시지를 보낸다.
                Message msg2 = Message.obtain();
                msg2.what = MSG_VOCABULARY_MAINBAR_VISIBILITY;
                mVocabularyDataLoadHandler.sendMessage(msg2);
            } else if (msg.what == MSG_VOCABULARY_MAINBAR_VISIBILITY) {
                // @@@@@
                // 단어 정보 헤더를 화면에 보이도록 한다.
                RelativeLayout layout = (RelativeLayout)findViewById(R.id.av_memorize_info_header);
                // @@@@@ 애니메이션 효과 없애기??
                if (layout.getVisibility() != View.VISIBLE) {
                    layout.setVisibility(View.VISIBLE);
                }
            } else if (msg.what == MSG_TOAST_SHOW) {
                Toast.makeText(VocabularyActivity.this, (String)msg.obj, Toast.LENGTH_LONG).show();
            } else if (msg.what == MSG_NETWORK_DISCONNECTED_DIALOG_SHOW) {
                // @@@@@
                new AlertDialog.Builder(VocabularyActivity.this)
                        .setTitle("알림")
                        .setMessage("Wi-Fi/3G등의 데이터 네트워크 상태가 불안정하여 단어 DB의 업데이트 여부를 확인할 수 없습니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            } else if (msg.what == MSG_VOCABULARY_DATA_SHOW_VOCABULARY_UPDATE_INFO_DIALOG) {
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.view_vocabulary_update_info, new LinearLayout(VocabularyActivity.this), false);

                if (v != null) {
                    TextView tvVocabularyUpdateInfo = (TextView)v.findViewById(R.id.vvui_vocabulary_update_info);
                    tvVocabularyUpdateInfo.setText(msg.getData().getString(EXTRA_VOCABULARY_UPDATE_INFO));

                    new AlertDialog.Builder(VocabularyActivity.this)
                            .setTitle(getString(R.string.vvui_dialog_title))
                            .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setView(v)
                            .show();
                }
            } else if (msg.what == MSG_VOCABULARY_DATA_DOWNLOAD_START) {
                // @@@@@
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();

                int totalVocabularyDbSize = msg.getData().getInt("TOTAL_VOCABULARY_DB_SIZE");

                mProgressDialog = new ProgressDialog(VocabularyActivity.this);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setMessage("단어 DB를 업데이트하고 있습니다.");
                mProgressDialog.setMax(totalVocabularyDbSize);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setProgress(0);
                mProgressDialog.show();
            } else if (msg.what == MSG_VOCABULARY_DATA_DOWNLOADING) {
                // @@@@@
                if (mProgressDialog != null) {
                    int recvVocabularyDbSize = msg.getData().getInt("RECV_VOCABULARY_DB_SIZE");
                    mProgressDialog.setProgress(recvVocabularyDbSize);
                }
            } else if (msg.what == MSG_VOCABULARY_DATA_DOWNLOAD_END) {
                // @@@@@
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();

                mProgressDialog = ProgressDialog.show(VocabularyActivity.this, null, "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.", true, false);
            } else if (msg.what == MSG_VOCABULARY_DATA_DOWNLOAD_QUESTION) {
                // @@@@@
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();

                final String newVocabularyDbVersion = msg.getData().getString("NEW_VOCABULARY_DB_VERSION");
                final String newVocabularyDbFileHash = msg.getData().getString("NEW_VOCABULARY_DB_FILE_HASH");

                new AlertDialog.Builder(VocabularyActivity.this)
                        .setTitle("알림")
                        .setMessage("3G 네트워크로 접속되었습니다. 데이터 통화료가 부과될 수 있습니다. 단어 DB를 업데이트하시겠습니까?")
                        .setPositiveButton("사용함", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateAndInitVocabularyDataOnMobileNetwork(newVocabularyDbVersion, newVocabularyDbFileHash, true);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateAndInitVocabularyDataOnMobileNetwork(newVocabularyDbVersion, newVocabularyDbFileHash, false);
                            }
                        })
                        .show();
            } else if (msg.what == MSG_VOCABULARY_SEEKBAR_VISIBILITY) {
                SeekBar vocabularySeekBar = (SeekBar)findViewById(R.id.av_vocabulary_seekbar);

                if (msg.arg1 == View.VISIBLE) {
                    if (vocabularySeekBar.getVisibility() != View.VISIBLE) {
                        vocabularySeekBar.setVisibility(View.VISIBLE);
                        vocabularySeekBar.startAnimation(AnimationUtils.loadAnimation(VocabularyActivity.this, android.R.anim.fade_in));
                    }
                } else {
                    if (vocabularySeekBar.getVisibility() != View.GONE) {
                        vocabularySeekBar.startAnimation(AnimationUtils.loadAnimation(VocabularyActivity.this, android.R.anim.fade_out));
                        vocabularySeekBar.setVisibility(View.GONE);
                    }
                }
            }
        }
    };

}
