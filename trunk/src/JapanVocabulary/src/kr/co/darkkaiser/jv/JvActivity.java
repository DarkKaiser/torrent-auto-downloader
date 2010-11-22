package kr.co.darkkaiser.jv;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import kr.co.darkkaiser.jv.data.JapanVocabulary;
import kr.co.darkkaiser.jv.data.JapanVocabularyComparator;
import kr.co.darkkaiser.jv.data.JapanVocabularyManager;
import kr.co.darkkaiser.jv.data.JapanVocabularyMemorizeTargetItem;
import kr.co.darkkaiser.jv.helper.ByteUtils;
import kr.co.darkkaiser.jv.view.detail.JvDetailActivity;
import kr.co.darkkaiser.jv.view.list.JvListActivity;
import kr.co.darkkaiser.jv.view.option.OptionActivity;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class JvActivity extends Activity implements OnTouchListener {

	private static final String TAG = "JvActivity";

	private static final int MSG_TOAST_SHOW = 1;
	private static final int MSG_PROGRESS_DIALOG_REFRESH = 2;
	private static final int MSG_VOCABULARY_MEMORIZE_START = 3;
	private static final int MSG_NETWORK_DISCONNECTED_DIALOG_SHOW = 4;
	private static final int MSG_VOCABULARY_DATA_DOWNLOAD_QUESTION = 5;
	private static final int MSG_VOCABULARY_DATA_DOWNLOAD_START = 6;
	private static final int MSG_VOCABULARY_DATA_DOWNLOAD_END = 7;
	private static final int MSG_VOCABULARY_DATA_DOWNLOADING = 8;
	private static final int MSG_VOCABULARY_DATA_UPDATE_INFO_DIALOG_SHOW = 9;

    private static final int MSG_CUSTOM_EVT_TAP = 1;
    private static final int MSG_CUSTOM_EVT_LONG_PRESS = 2;
    private static final int MSG_CUSTOM_EVT_APP_FINISH_STANDBY = 3;

	// Long Press 를 판단하는 시간 값
	private static final int LONG_PRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();

	private Random mRandom = new Random();
	private ProgressDialog mProgressDialog = null;

	// 현재 화면에 보여지고 있는 단어의 인덱스
	private int mJvCurrentIndex = -1;

	// 암기 대상 단어 전체 갯수
	private int mMemorizeTargetJvCount = 0;

	// 암기 대상 항목
	private JapanVocabularyMemorizeTargetItem mJvMemorizeTargetItem = JapanVocabularyMemorizeTargetItem.VOCABULARY;

	// 암기 대상 일본어 단어 리스트
	private ArrayList<JapanVocabulary> mJvList = new ArrayList<JapanVocabulary>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // SD 카드의 상태를 확인한다.
        String sdStatus = Environment.getExternalStorageState();
        if (sdStatus.equals(Environment.MEDIA_UNMOUNTED) == true) {
        	new AlertDialog.Builder(this)
        		.setTitle("SD 카드 오류")
        		.setMessage("SD 카드가 마운트 해제되어 있습니다. 단어 데이터를 로드할 수 없습니다.")
        		.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					
        			@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
        		.show();

        	return;
        }
        
        RelativeLayout vocabularyContainer = (RelativeLayout)findViewById(R.id.vocabulary_container);
        vocabularyContainer.setOnTouchListener(this);

        TextSwitcher vocabularyTextSwitcher = (TextSwitcher)findViewById(R.id.vocabulary);
        vocabularyTextSwitcher.setOnTouchListener(this);
        vocabularyTextSwitcher.setFactory(new ViewFactory() {
			
			@Override
			public View makeView() {
				TextView tv = new TextView(JvActivity.this);
		        tv.setGravity(Gravity.CENTER);
		        tv.setTypeface(Typeface.SERIF);
		        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 70);
		        tv.setLayoutParams(new TextSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		        return tv;
			}
		});
        
        TextSwitcher vocabularyTranslationTextSwitcher = (TextSwitcher)findViewById(R.id.vocabulary_translation);
        vocabularyTranslationTextSwitcher.setOnTouchListener(this);
        vocabularyTranslationTextSwitcher.setFactory(new ViewFactory() {

			@Override
			public View makeView() {
				TextView tv = new TextView(JvActivity.this);
		        tv.setGravity(Gravity.CENTER);
		        tv.setTypeface(Typeface.SERIF);
		        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
		        tv.setLayoutParams(new TextSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		        return tv;
			}
		});

        // 환경설정 값을 로드한다.
        initSharedPreference(false);

        Button nextVocabulary = (Button)findViewById(R.id.next_vocabulary);
        nextVocabulary.setOnClickListener(new View.OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				// 진동을 발생시킨다.
				Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(30);

				showNextVocabulary();
			}
		});

        // 현재 인터넷에 연결되어 있는지의 여부를 확인한 후, 단어 데이터를 업데이트한다.
        boolean isNowNetworkConnected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting() == true) {
			isNowNetworkConnected = true;

			// 프로그레스 대화상자를 보인다.
			mProgressDialog = ProgressDialog.show(this, null, "단어 DB의 업데이트 여부를 확인하는 중 입니다.", true, false);
		} else {
			// 프로그레스 대화상자를 보인다.
			mProgressDialog = ProgressDialog.show(this, null, "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.", true, false);			
		}

   		new Thread() {

   			// 현재 네트워크(3G 혹은 와이파이)에 연결되어 있는지의 여부를 나타낸다.
   			private boolean mIsNowNetworkConnected = false;

   			public Thread setValues(boolean isNowNetworkConnected) {
   				mIsNowNetworkConnected = isNowNetworkConnected;
   				return this;
   			}

			@Override
   			public void run() {
				if (mIsNowNetworkConnected == true) {
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
							initVocabularyDataAndStartMemorize(mIsNowNetworkConnected, updateSucceeded);
						}
					} else {
						// 단어 데이터를 초기화한 후, 암기를 시작합니다.
						initVocabularyDataAndStartMemorize(mIsNowNetworkConnected, false);
					}					
				} else {
					// 단어 데이터를 초기화한 후, 암기를 시작합니다.
					initVocabularyDataAndStartMemorize(mIsNowNetworkConnected, false);
				}
   			};
   		}
   		.setValues(isNowNetworkConnected)
   		.start();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.jv_main_menu, menu);
		return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.jvm_show_all_vocabulary:
			Intent intent = new Intent(this, JvListActivity.class);
			startActivityForResult(intent, R.id.jvm_show_all_vocabulary);
			return true;

		case R.id.jvm_all_rememorize:
			assert mProgressDialog == null;

			// 데이터를 처리가 끝날 때가지 프로그레스 대화상자를 보인다.
			mProgressDialog = ProgressDialog.show(this, null, "요청하신 작업을 처리 중입니다.", true, false);

	   		new Thread() {
				@Override
	   			public void run() {
					// 암기 대상 단어들을 모두 암기미완료로 리셋한다.
					JapanVocabularyManager.getInstance().rememorizeAllMemorizeTarget();
					
			        // 단어 데이터를 로드합니다.
			        loadMemorizeTargetVocabularyData();

					Message msg = Message.obtain();
					msg.what = MSG_VOCABULARY_MEMORIZE_START;
					mVocabularyDataLoadHandler.sendMessage(msg);
	   			};
	   		}
	   		.start();

			return true;

		case R.id.jvm_preferences:
			startActivityForResult(new Intent(this, OptionActivity.class), R.id.jvm_preferences);
			return true;
		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// @@@@@ 단어 암기 순서가 변경된 경우는???

		if (requestCode == R.id.jvm_show_all_vocabulary) {
			assert mProgressDialog == null;

			// 환경설정 값이 바뀌었는지 확인한다.
			if ((resultCode & JvListActivity.ACTIVITY_RESULT_PREFERENCE_CHANGED) == JvListActivity.ACTIVITY_RESULT_PREFERENCE_CHANGED) {
				if ((resultCode & JvListActivity.ACTIVITY_RESULT_DATA_CHANGED) == JvListActivity.ACTIVITY_RESULT_DATA_CHANGED) {
					initSharedPreference(false);
				} else {
					initSharedPreference(true);
					return;
				}
			}

			// 변경된 내역이 없으면 다시 로드할 필요가 없으므로 바로 반환한다.
			if ((resultCode & JvListActivity.ACTIVITY_RESULT_DATA_CHANGED) != JvListActivity.ACTIVITY_RESULT_DATA_CHANGED)
				return;

			// 데이터를 로드하는 중임을 나타내는 프로그레스 대화상자를 보인다.
			mProgressDialog = ProgressDialog.show(this, null, "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.", true, false);

	   		new Thread() {
				@Override
	   			public void run() {
			        // 단어 데이터를 로드합니다.
			        loadMemorizeTargetVocabularyData();

					Message msg = Message.obtain();
					msg.what = MSG_VOCABULARY_MEMORIZE_START;
					mVocabularyDataLoadHandler.sendMessage(msg);
	   			};
	   		}
	   		.start();
		} else if (requestCode == R.id.jvm_preferences) {
			initSharedPreference(true);
		}
	}

	private void initSharedPreference(boolean showNextVocabulary) {
		SharedPreferences mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);

		TextSwitcher vocabularyTextSwitcher = (TextSwitcher)findViewById(R.id.vocabulary);
		TextSwitcher vocabularyTranslationTextSwitcher = (TextSwitcher)findViewById(R.id.vocabulary_translation);
		if (mPreferences.getBoolean(JvDefines.JV_SPN_FADE_EFFECT_NEXT_VOCABULARY, true) == true) {
			vocabularyTextSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
			vocabularyTextSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));						
			vocabularyTranslationTextSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
			vocabularyTranslationTextSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));						
		} else {
			vocabularyTextSwitcher.setInAnimation(null);
			vocabularyTextSwitcher.setOutAnimation(null);			
			vocabularyTranslationTextSwitcher.setInAnimation(null);
			vocabularyTranslationTextSwitcher.setOutAnimation(null);			
		}
		
		if (mPreferences.getBoolean(JvDefines.JV_SPN_SHOW_VOCABULARY_TRANSLATION, false) == false) {
			vocabularyTranslationTextSwitcher.setVisibility(View.GONE);
		} else {
			vocabularyTranslationTextSwitcher.setVisibility(View.VISIBLE);
		}

		String memorizeTargetItem = mPreferences.getString(JvDefines.JV_SPN_MEMORIZE_TARGET_ITEM, "0");
		if (mIsJapanVocabularyOutputMode != (TextUtils.equals(memorizeTargetItem, "0"))) {
			if (TextUtils.equals(memorizeTargetItem, "0") == true) {
				mJvMemorizeTargetItem = JapanVocabularyMemorizeTargetItem.VOCABULARY;
			} else {
				mJvMemorizeTargetItem = JapanVocabularyMemorizeTargetItem.VOCABULARY_GANA;
			}

			// 출력될 단어의 항목이 바뀌었을 경우에만 다음 글자를 보인다.
			if (showNextVocabulary == true)
				showNextVocabulary();
		}
	}

	private void showNextVocabulary() {
		TextSwitcher vocabularyTextSwitcher = (TextSwitcher)findViewById(R.id.vocabulary);
		TextSwitcher vocabularyTranslationTextSwitcher = (TextSwitcher)findViewById(R.id.vocabulary_translation);

		if (mJvList.isEmpty() == true) {
			mJvCurrentIndex = -1;
			vocabularyTextSwitcher.setText("");
			vocabularyTranslationTextSwitcher.setText("");

			Toast.makeText(this, "암기 할 단어가 없습니다.", Toast.LENGTH_SHORT).show();
		} else {
			if (mJvList.size() == 1) {
				mJvCurrentIndex = 0;
			} else {
				int index = mJvCurrentIndex;

				do
				{
					mJvCurrentIndex = mRandom.nextInt(mJvList.size());									
				} while (mJvCurrentIndex == index);
			}

			// 글자가 길어서 컨트롤의 크기가 커질 경우 한 템포씩 늦게 컨트롤의 크기가 줄어들므로
			// 먼저 컨트롤의 크기를 줄이고 나서 값을 넣는다.
			vocabularyTextSwitcher.setText("");
			vocabularyTranslationTextSwitcher.setText("");
			
			JapanVocabulary jpVocabulary = mJvList.get(mJvCurrentIndex);
			if (jpVocabulary != null) {
				// 화면에 다음 단어를 출력한다.
				if (mJvMemorizeTargetItem == JapanVocabularyMemorizeTargetItem.VOCABULARY) {
					vocabularyTextSwitcher.setText(jpVocabulary.getVocabulary());
				} else {
					vocabularyTextSwitcher.setText(jpVocabulary.getVocabularyGana());
				}
				
				vocabularyTranslationTextSwitcher.setText(jpVocabulary.getVocabularyTranslation());				
			}
		}
	}

	private void updateJvMemorizeInfo() {
		// @@@@@
		TextView jvInfo = (TextView)findViewById(R.id.jv_info);
		jvInfo.setText(String.format("암기완료 %d개 / 암기대상 %d개", mMemorizeTargetJvCount - mJvList.size(), mMemorizeTargetJvCount));
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
    	if ((v.getId() == R.id.vocabulary_container || v.getId() == R.id.vocabulary || v.getId() == R.id.vocabulary_translation) &&
    			mJvCurrentIndex != -1) {

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

	@Override
	public void onBackPressed() {
		if (mCustomEventHandler.hasMessages(MSG_CUSTOM_EVT_APP_FINISH_STANDBY) == false) {
			mCustomEventHandler.sendEmptyMessageAtTime(MSG_CUSTOM_EVT_APP_FINISH_STANDBY, SystemClock.uptimeMillis() + 2000);
			Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		super.onBackPressed();
	}

	private Handler mVocabularyDataLoadHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_PROGRESS_DIALOG_REFRESH) {
				if (mProgressDialog != null)
					mProgressDialog.setMessage((String)msg.obj);
			} else if (msg.what == MSG_VOCABULARY_MEMORIZE_START) {
		    	updateJvMemorizeInfo();
	        	showNextVocabulary();

				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				mProgressDialog = null;
			} else if (msg.what == MSG_TOAST_SHOW) {
				Toast.makeText(JvActivity.this, (String)msg.obj, Toast.LENGTH_LONG).show();
			} else if (msg.what == MSG_NETWORK_DISCONNECTED_DIALOG_SHOW) {
	        	new AlertDialog.Builder(JvActivity.this)
        			.setTitle("알림")
        			.setMessage("Wi-Fi/3G등의 데이터 네트워크 상태가 불안정하여 단어 DB의 업데이트 여부를 확인할 수 없습니다.")
        			.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					
        				@Override
        				public void onClick(DialogInterface dialog, int which) {
        				}
        			})
        			.show();
			} else if (msg.what == MSG_VOCABULARY_DATA_UPDATE_INFO_DIALOG_SHOW) {
				LayoutInflater inflater = getLayoutInflater();
				View v = inflater.inflate(R.layout.jv_update_info_view, null);
				
				if (v != null) {
					TextView jpVocabularyUpdateInfo = (TextView)v.findViewById(R.id.jv_update_info);
					jpVocabularyUpdateInfo.setText(msg.getData().getString("JV_UPDATE_INFO"));

		        	new AlertDialog.Builder(JvActivity.this)
		        		.setTitle("단어 업데이트 정보")
		        		.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
		        			@Override
		        			public void onClick(DialogInterface dialog, int which) {
		        			}
		        		})
		        		.setView(v)
		        		.show();
				}
			} else if (msg.what == MSG_VOCABULARY_DATA_DOWNLOAD_START) {
				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				int totalVocabularyDbSize = msg.getData().getInt("TOTAL_VOCABULARY_DB_SIZE");

				mProgressDialog = new ProgressDialog(JvActivity.this);
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				mProgressDialog.setMessage("단어 DB를 업데이트하고 있습니다.");
				mProgressDialog.setMax(totalVocabularyDbSize);
				mProgressDialog.setCancelable(false);
				mProgressDialog.setProgress(0);
				mProgressDialog.show();
			} else if (msg.what == MSG_VOCABULARY_DATA_DOWNLOADING) {
				if (mProgressDialog != null) {
					int recvVocabularyDbSize = msg.getData().getInt("RECV_VOCABULARY_DB_SIZE");
					mProgressDialog.setProgress(recvVocabularyDbSize);	
				}
			} else if (msg.what == MSG_VOCABULARY_DATA_DOWNLOAD_END) {
				if (mProgressDialog != null)
					mProgressDialog.dismiss();
				
				mProgressDialog = ProgressDialog.show(JvActivity.this, null, "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.", true, false);
			} else if (msg.what == MSG_VOCABULARY_DATA_DOWNLOAD_QUESTION) {
				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				final String newVocabularyDbVersion = msg.getData().getString("NEW_VOCABULARY_DB_VERSION");
				final String newVocabularyDbFileHash = msg.getData().getString("NEW_VOCABULARY_DB_FILE_HASH");

	        	new AlertDialog.Builder(JvActivity.this)
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
			}
		};
	};

	private Handler mCustomEventHandler = new Handler() {

		@Override
    	public void handleMessage(Message msg){
    		switch(msg.what) {
	    		case MSG_CUSTOM_EVT_LONG_PRESS:
	    			if (mJvCurrentIndex != -1) {
						// 진동을 발생시킨다.
						Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
						vibrator.vibrate(30);

	    				new AlertDialog.Builder(JvActivity.this)
	    					.setTitle("암기완료")
	    					.setMessage("단어 암기를 완료하셨나요?")
	    					.setPositiveButton("예", new DialogInterface.OnClickListener() {
	    						
	    						@Override
	    						public void onClick(DialogInterface dialog, int which) {
	    							// 진동을 발생시킨다.
	    							Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
	    							vibrator.vibrate(30);

		    						// @@@@@
	    							mJvList.get(mJvCurrentIndex).setMemorizeCompleted(true, true, true);
	    							mJvList.remove(mJvCurrentIndex);
	    							updateJvMemorizeInfo();
	    							showNextVocabulary();

	    							dialog.dismiss();
	    						}
	    					})
	    					.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
	    						
	    						@Override
	    						public void onClick(DialogInterface dialog, int which) {
	    						}
	    					})
	    					.show();
					}
	    			break;
			
	    		case MSG_CUSTOM_EVT_TAP:
	    			if (mJvCurrentIndex != -1) {
	    				Intent intent = new Intent(JvActivity.this, JvDetailActivity.class);
	    				intent.putExtra("idx", mJvList.get(mJvCurrentIndex).getIdx());
	    				startActivity(intent);	    					
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

	private ArrayList<String> checkNewVocabularyDb() {
		// 로컬 단어 DB의 버전정보를 구한다.
		SharedPreferences mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		String localDbVersion = mPreferences.getString(JvDefines.JV_SPN_DB_VERSION, "");

		try {
			URL url = new URL(JvDefines.JV_DB_VERSION_CHECK_URL);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
			osw.write("");
			osw.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "euc-kr"));

			String inputLine = null;
			ArrayList<String> readData = new ArrayList<String>(); 
			while ((inputLine = br.readLine()) != null) {
				readData.add(inputLine);
			}

			br.close();

			// 단어 DB의 갱신 여부를 확인한다.
			String newVocabularyDbVersion = "", newVocabularyDbFileHash = "";

			if (readData.size() >= 1) {
				newVocabularyDbVersion = readData.get(0).trim();
			}
			
			if (newVocabularyDbVersion != null && TextUtils.isEmpty(newVocabularyDbVersion) == false && newVocabularyDbVersion.equals(localDbVersion) == false) {
				if (readData.size() >= 2) {
					newVocabularyDbFileHash = readData.get(1).trim();
				}

				ArrayList<String> result = new ArrayList<String>();
				result.add(newVocabularyDbVersion);
				result.add(newVocabularyDbFileHash);
				return result;
			}
		} catch (FileNotFoundException e) {
			Log.d(TAG, e.getMessage());

			Message msg = Message.obtain();
			msg.what = MSG_TOAST_SHOW;
			msg.obj = "단어 DB의 업데이트  여부를 확인할 수 없습니다.";
			mVocabularyDataLoadHandler.sendMessage(msg);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());

			Message msg = Message.obtain();
			msg.what = MSG_TOAST_SHOW;
			msg.obj = "단어 DB의 업데이트  여부를 확인할 수 없습니다.";
			mVocabularyDataLoadHandler.sendMessage(msg);
		}

		return null;
	}

	private void updateAndInitVocabularyDataOnMobileNetwork(String newVocabularyDbVersion, String newVocabularyDbFileHash, boolean isUpdateVocabularyDb) {
		if (isUpdateVocabularyDb == true)
			mProgressDialog = ProgressDialog.show(JvActivity.this, null, "단어 DB를 업데이트하고 있습니다.", true, false);			
		else
			mProgressDialog = ProgressDialog.show(JvActivity.this, null, "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.", true, false);			

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
				initVocabularyDataAndStartMemorize(true, updateSucceeded);
   			};
   		}
   		.setValues(newVocabularyDbVersion, newVocabularyDbFileHash, isUpdateVocabularyDb)
   		.start();
	}

	private boolean updateVocabularyDb(String newVocabularyDbVersion, String newVocabularyDbFileHash) {
		assert TextUtils.isEmpty(newVocabularyDbVersion) == false;

		Message msg = null;
		boolean updateSucceeded = false;

		String jvDbPath = String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), JvDefines.JV_MAIN_FOLDER_NAME);
		File f = new File(jvDbPath);
		if (f.exists() == false) {
			f.mkdir();
		}

		jvDbPath += JvDefines.JV_VOCABULARY_DB;

		// 단어 DB 파일을 내려받는다.
		try {
			URL url = new URL(JvDefines.JV_DB_DOWNLOAD_URL);

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
					f = new File(jvDbPath);
					f.delete();

					FileOutputStream fos = new FileOutputStream(f);
					fos.write(baf.toByteArray());
					fos.close();

					SharedPreferences mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
					mPreferences.edit().putString(JvDefines.JV_SPN_DB_VERSION, newVocabularyDbVersion).commit();
				} else {
					f = new File(String.format("%s.tmp", jvDbPath));
					f.delete();

					FileOutputStream fos = new FileOutputStream(f);
					fos.write(baf.toByteArray());
					fos.close();

					// 다운로드 받은 파일의 해쉬값을 구하여 올바른 파일인지 비교한다.
					boolean isValidationFile = true;
					byte[] fileHashBytes = getFileHash(f);
					if (fileHashBytes != null) {
						if (newVocabularyDbFileHash.equalsIgnoreCase(ByteUtils.toHexString(fileHashBytes)) == false)
							isValidationFile = false;
					}

					if (isValidationFile == true) {
						File dstFile = new File(jvDbPath);
						dstFile.delete();
						f.renameTo(dstFile);

						updateSucceeded = true;
						SharedPreferences mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
						mPreferences.edit().putString(JvDefines.JV_SPN_DB_VERSION, newVocabularyDbVersion).commit();
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

	private void initVocabularyDataAndStartMemorize(boolean nowNetworkConnected, boolean updateSucceeded) {
		Message msg = Message.obtain();
		msg.what = MSG_PROGRESS_DIALOG_REFRESH;
		msg.obj = "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.";
		mVocabularyDataLoadHandler.sendMessage(msg);

		// DB에서 단어 데이터를 읽어들인다.
		if (JapanVocabularyManager.getInstance().initDataFromDB() == false) {
			msg = Message.obtain();
			msg.what = MSG_TOAST_SHOW;
			msg.obj = "단어 DB에서 데이터의 로딩이 실패하였습니다.";
			mVocabularyDataLoadHandler.sendMessage(msg);
		}

		// 단어 데이터를 로드합니다.
        loadMemorizeTargetVocabularyData();

		if (nowNetworkConnected == false) {
			msg = Message.obtain();
			msg.what = MSG_NETWORK_DISCONNECTED_DIALOG_SHOW;
			mVocabularyDataLoadHandler.sendMessage(msg);
		} else if (updateSucceeded == true) {
			SharedPreferences mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
			long prevMaxIdx = mPreferences.getLong(JvDefines.JV_SPN_LAST_UPDATED_MAX_IDX, -1);
			
			StringBuilder sb = new StringBuilder();
			long newMaxIdx = JapanVocabularyManager.getInstance().getUpdatedJapanVocabularyInfo(prevMaxIdx, sb);

			if (newMaxIdx != -1) {
				mPreferences.edit().putLong(JvDefines.JV_SPN_LAST_UPDATED_MAX_IDX, newMaxIdx).commit();
				
				// 이전에 한번이상 업데이트 된 경우에 한에서 단어 업데이트 정보를 보인다.
				if (prevMaxIdx != -1) {
					Bundle bundle = new Bundle();
					bundle.putString("JV_UPDATE_INFO", sb.toString());

					msg = Message.obtain();
					msg.what = MSG_VOCABULARY_DATA_UPDATE_INFO_DIALOG_SHOW;
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

	private void loadMemorizeTargetVocabularyData() {
		Message msg = Message.obtain();
		msg.what = MSG_PROGRESS_DIALOG_REFRESH;
		msg.obj = "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.";
		mVocabularyDataLoadHandler.sendMessage(msg);

		// 암기 대상 단어들만을 필터링한다.
		mJvList.clear();
		mMemorizeTargetJvCount = JapanVocabularyManager.getInstance().getMemorizeTargetJvList(mJvList);			

		// 단어 암기 순서에 따라 정렬한다.
		SharedPreferences mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		int memorizeOrderMethod = Integer.parseInt(mPreferences.getString(JvDefines.JV_SPN_MEMORIZE_ORDER_METHOD, "0"));
		
		switch (memorizeOrderMethod) {
		case 1:
			Collections.sort(mJvList, JapanVocabularyComparator.mJvVocabularyComparator);
			break;
		case 3:
			Collections.sort(mJvList, JapanVocabularyComparator.mJvVocabularyGanaComparator);
			break;
		case 2:
			Collections.sort(mJvList, JapanVocabularyComparator.mJvVocabularyTranslationComparator);
			break;
		case 4:
			Collections.sort(mJvList, JapanVocabularyComparator.mJvRegistrationDateUpComparator);
			break;
		case 5:
			Collections.sort(mJvList, JapanVocabularyComparator.mJvRegistrationDateDownComparator);
			break;
		}
	}

	protected byte[] getFileHash(File file) throws IOException, NoSuchAlgorithmException {
		assert file != null;
		assert file.exists() == true;

	    BufferedInputStream bis = null;

	    try {
	        MessageDigest md = MessageDigest.getInstance("SHA1");
	        bis = new BufferedInputStream(new FileInputStream(file));
	        
	        int readBytes = -1;
	        byte[] buffer = new byte[1024];
	        while ((readBytes = bis.read(buffer)) != -1) {
	            md.update(buffer, 0, readBytes);
	        }

	        return md.digest();
	    } finally {
	        if (bis != null) {
	        	try {
	        		bis.close();
	        	} catch (IOException e) {
	        	}
	        }
	    }
	}

}
