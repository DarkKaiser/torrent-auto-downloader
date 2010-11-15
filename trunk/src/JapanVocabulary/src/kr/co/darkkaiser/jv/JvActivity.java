package kr.co.darkkaiser.jv;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

import kr.co.darkkaiser.jv.detail.JvDetailActivity;
import kr.co.darkkaiser.jv.list.JvListActivity;
import kr.co.darkkaiser.jv.option.OptionActivity;

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
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class JvActivity extends Activity implements OnTouchListener, ViewFactory {

	private static final String TAG = "JvActivity";

	private static final int MSG_TOAST_SHOW = 1;
	private static final int MSG_PROGRESS_DIALOG_REFRESH = 2;
	private static final int MSG_VOCABULARY_MEMORIZE_START = 3;
	private static final int MSG_NETWORK_DISCONNECTED_DIALOG_SHOW = 4;
	private static final int MSG_VOCABULARY_DATA_DOWNLOAD_QUESTION = 5;
	private static final int MSG_VOCABULARY_DATA_DOWNLOAD_START = 6;
	private static final int MSG_VOCABULARY_DATA_DOWNLOAD_END = 7;
	private static final int MSG_VOCABULARY_DATA_DOWNLOADING = 8;

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

	// 일본식 한자 단어를 암기대상으로 출력할지의 여부
	// 이 값이 false 이면 히라가나/가타카나를 암기대상으로 출력한다.
	private boolean mIsJapanVocabularyOutputMode = true;

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

        TextSwitcher vocabularyTextSwitcher = (TextSwitcher)findViewById(R.id.vocabulary);
        vocabularyTextSwitcher.setFactory(this);  
        vocabularyTextSwitcher.setOnTouchListener(this);

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
					String newVocabularyDbVersion = checkNewVocabularyDb();
					if (newVocabularyDbVersion != null && TextUtils.isEmpty(newVocabularyDbVersion) == false) {
						// 현재 연결된 네트워크가 3G 연결인지 확인한다.
						ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
						NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
						if (mobileNetworkInfo != null && mobileNetworkInfo.isConnectedOrConnecting() == true) {
							Bundle bundle = new Bundle();
							bundle.putString("NEW_VOCABULARY_DB_VERSION", newVocabularyDbVersion);
							
							Message msg = Message.obtain();
							msg.what = MSG_VOCABULARY_DATA_DOWNLOAD_QUESTION;
			                msg.setData(bundle);

							mVocabularyDataLoadHandler.sendMessage(msg);
						} else {
							// 새로운 단어 DB로 갱신합니다.
							updateVocabularyDb(newVocabularyDbVersion);

							// 단어 데이터를 초기화한 후, 암기를 시작합니다.
							initVocabularyDataAndStartMemorize(mIsNowNetworkConnected);
						}
					} else {
						// 단어 데이터를 초기화한 후, 암기를 시작합니다.
						initVocabularyDataAndStartMemorize(mIsNowNetworkConnected);
					}					
				} else {
					// 단어 데이터를 초기화한 후, 암기를 시작합니다.
					initVocabularyDataAndStartMemorize(mIsNowNetworkConnected);					
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
					JvManager.getInstance().rememorizeAllMemorizeTarget();
					
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
		if (mPreferences.getBoolean(JvDefines.JV_SPN_FADE_EFFECT_NEXT_VOCABULARY, true) == true) {
			vocabularyTextSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
			vocabularyTextSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));						
		} else {
			vocabularyTextSwitcher.setInAnimation(null);
			vocabularyTextSwitcher.setOutAnimation(null);			
		}

		String memorizeTargetItem = mPreferences.getString(JvDefines.JV_SPN_MEMORIZE_TARGET_ITEM, "0");
		if (mIsJapanVocabularyOutputMode != (TextUtils.equals(memorizeTargetItem, "0"))) {
			if (TextUtils.equals(memorizeTargetItem, "0") == true) {
				mIsJapanVocabularyOutputMode = true;
			} else {
				mIsJapanVocabularyOutputMode = false;
			}

			// 출력될 단어의 항목이 바뀌었을 경우에만 다음 글자를 보인다.
			if (showNextVocabulary == true)
				showNextVocabulary();
		}
	}

	private void showNextVocabulary() {
		TextSwitcher vocabularyTextSwitcher = (TextSwitcher)findViewById(R.id.vocabulary);

		if (mJvList.isEmpty() == true) {
			mJvCurrentIndex = -1;
			vocabularyTextSwitcher.setText("");

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

			// 화면에 다음 단어를 출력한다.
			if (mIsJapanVocabularyOutputMode == true)
				vocabularyTextSwitcher.setText(mJvList.get(mJvCurrentIndex).getVocabulary());
			else
				vocabularyTextSwitcher.setText(mJvList.get(mJvCurrentIndex).getVocabularyGana());
		}
	}

	private void updateJvMemorizeInfo() {
		TextView jvInfo = (TextView)findViewById(R.id.jv_info);
		jvInfo.setText(String.format("암기완료 %d개 / 암기대상 %d개", mMemorizeTargetJvCount - mJvList.size(), mMemorizeTargetJvCount));
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
    	if (v.getId() == R.id.vocabulary && mJvCurrentIndex != -1) {
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

	        	new AlertDialog.Builder(JvActivity.this)
    				.setTitle("알림")
    				.setMessage("3G 네트워크로 접속되었습니다. 데이터 통화료가 부과될 수 있습니다. 단어 DB를 업데이트하시겠습니까?")
    				.setPositiveButton("사용함", new DialogInterface.OnClickListener() {
				
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						updateAndInitVocabularyDataOnMobileNetwork(newVocabularyDbVersion, true);
    					}
    				})
					.setNegativeButton("취소", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							updateAndInitVocabularyDataOnMobileNetwork(newVocabularyDbVersion, false);
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

	@Override
	public View makeView() {
		TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(Typeface.SERIF);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 70);
        tv.setLayoutParams(new TextSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        return tv;
	}
	
	private String checkNewVocabularyDb() {
		// 로컬 단어 DB의 버전정보를 구한다.
		SharedPreferences mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		String localDbVersion = mPreferences.getString(JvDefines.JV_SPN_DB_VERSION, "");

		// 단어 DB의 갱신 여부를 확인한다.
		String remoteDbVersion = null;

		try {
			URL url = new URL(JvDefines.JV_DB_VERSION_CHECK_URL);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
			osw.write("");
			osw.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "euc-kr"));

			String inputLine = null;
			StringBuilder sb = new StringBuilder();
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}

			br.close();
			remoteDbVersion = sb.toString().trim();

			if (remoteDbVersion != null && TextUtils.isEmpty(remoteDbVersion) == false && remoteDbVersion.equals(localDbVersion) == false) {
				return remoteDbVersion;
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

		return "sldfl";//@@@@@ 임시코드
//		return "";
	}

	private void updateAndInitVocabularyDataOnMobileNetwork(String newVocabularyDbVersion, boolean updateVocabularyDb) {
		if (updateVocabularyDb == true) {
			mProgressDialog = ProgressDialog.show(JvActivity.this, null, "단어 DB를 업데이트하고 있습니다.", true, false);			
		} else {
			mProgressDialog = ProgressDialog.show(JvActivity.this, null, "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.", true, false);			
		}

		new Thread() {

			// 단어 DB를 업데이트 할지에 대한 플래그
			private boolean mUpdateVocabularyDb = false;
			
			// 새로 업데이트 할 단어 DB의 버전
			private String mNewVocabularyDbVersion = null;

   			public Thread setValues(String newVocabularyDbVersion, boolean updateVocabularyDb) {
   				assert TextUtils.isEmpty(newVocabularyDbVersion) == false;
   				mNewVocabularyDbVersion = newVocabularyDbVersion;
   				mUpdateVocabularyDb = updateVocabularyDb;
   				return this;
   			}

			@Override
   			public void run() {
				if (mUpdateVocabularyDb == true) {
					// 새로운 단어 DB로 갱신합니다.
					updateVocabularyDb(mNewVocabularyDbVersion);					
				}

				// 단어 데이터를 초기화한 후, 암기를 시작합니다.
				initVocabularyDataAndStartMemorize(true);
   			};
   		}
   		.setValues(newVocabularyDbVersion, updateVocabularyDb)
   		.start();
	}

	private void updateVocabularyDb(String newVocabularyDbVersion) {
		assert TextUtils.isEmpty(newVocabularyDbVersion) == false;

		Message msg = null;
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

			bis.close();

			if (contentLength > 0 && contentLength != baf.length()) {
				msg = Message.obtain();
				msg.what = MSG_TOAST_SHOW;
				msg.obj = "새로운 단어 DB의 업데이트가 실패하였습니다.";
				mVocabularyDataLoadHandler.sendMessage(msg);
			} else {
				f = new File(jvDbPath);
				f.delete();

				FileOutputStream fos = new FileOutputStream(f);
				fos.write(baf.toByteArray());
				fos.close();

				// @@@@@ sha1 체크

				SharedPreferences mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
				mPreferences.edit().putString(JvDefines.JV_SPN_DB_VERSION, newVocabularyDbVersion).commit();
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
	}

	private void initVocabularyDataAndStartMemorize(boolean nowNetworkConnected) {
		Message msg = Message.obtain();
		msg.what = MSG_PROGRESS_DIALOG_REFRESH;
		msg.obj = "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.";
		mVocabularyDataLoadHandler.sendMessage(msg);

		// DB에서 단어 데이터를 읽어들인다.
		if (JvManager.getInstance().initDataFromDB() == false) {
			msg = Message.obtain();
			msg.what = MSG_TOAST_SHOW;
			msg.obj = "단어 DB에서 데이터의 로딩이 실패하였습니다.";
			mVocabularyDataLoadHandler.sendMessage(msg);
		}

		// 단어 데이터를 로드합니다.
        loadMemorizeTargetVocabularyData();

        // 단어 암기를 시작합니다.
		msg = Message.obtain();
		msg.what = MSG_VOCABULARY_MEMORIZE_START;
		mVocabularyDataLoadHandler.sendMessage(msg);

		if (nowNetworkConnected == false) {
			msg = Message.obtain();
			msg.what = MSG_NETWORK_DISCONNECTED_DIALOG_SHOW;
			mVocabularyDataLoadHandler.sendMessage(msg);
		}
	}

	private void loadMemorizeTargetVocabularyData() {
		Message msg = Message.obtain();
		msg.what = MSG_PROGRESS_DIALOG_REFRESH;
		msg.obj = "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.";
		mVocabularyDataLoadHandler.sendMessage(msg);

		// 암기 대상 단어들만을 필터링한다.
		mJvList.clear();
		mMemorizeTargetJvCount = JvManager.getInstance().getMemorizeTargetJvList(mJvList);			
	}

}
