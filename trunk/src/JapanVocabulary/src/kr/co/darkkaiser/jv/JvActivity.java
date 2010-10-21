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
import kr.co.darkkaiser.jv.option.OptionActivity;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class JvActivity extends Activity implements OnTouchListener {

	private static final String TAG = "JvActivity";

	private static final int MSG_TOAST_SHOW = 1;
	private static final int MSG_PROGRESS_DIALOG_REFRESH = 2;
	private static final int MSG_VOCABULARY_MEMORIZE_START = 3;

    private static final int MSG_TOUCHEVT_TAP = 1;
    private static final int MSG_TOUCHEVT_LONG_PRESS = 2;

	// Long Press 를 판단하는 시간 값
	private static final int LONG_PRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();

	private Random mRandom = new Random();
	private ProgressDialog mProgressDialog = null;
	
	// 현재 화면에 보여지고 있는 일본어 단어의 인덱스
	private int mJvCurrentIndex = -1;

	// 암기 대상 일본어 단어 전체 갯수
	private int mMemorizeTargetJvCount = 0;

	// 일본식 한자 단어를 암기대상으로 출력할지의 여부
	// 이 값이 false 이면 히라가나/가타가나를 암기대상으로 출력한다.
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
        		.setMessage("SD 카드가 마운트 해제되어 있습니다. 데이터를 로드할 수 없습니다.")
        		.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
        		.show();

        	return;
        }
        
        // 환경설정 값을 로드한다.
        initPreference(false);

        TextView tvVocabulary = (TextView)findViewById(R.id.vocabulary);
        tvVocabulary.setOnTouchListener(this);

        Button btnNextVocabulary = (Button)findViewById(R.id.next_vocabulary);
        btnNextVocabulary.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showNextVocabulary();

				// 진동을 발생시킨다.
				Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(30);
			}
		});
        
		// 프로그레스 대화상자를 보인다.
		mProgressDialog = ProgressDialog.show(this, null, "단어 DB의 업데이트 여부를 확인하는 중 입니다.", true, false);

   		new Thread() {
			@Override
   			public void run() {
		        // 새로운 단어 데이터가 있을 경우, 단어 데이터를 업데이트합니다.
		        updateJvDB();

		        // 단어 데이터를 로드합니다.
		        readyMemorizeTargetVocabularyData();

				Message msg = Message.obtain();
				msg.what = MSG_VOCABULARY_MEMORIZE_START;
				mVocabularyDataLoadHandler.sendMessage(msg);
   			};
   		}.start();
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
			mProgressDialog = ProgressDialog.show(this, null, "요청하신 작업을 진행 중 입니다.", true, false);

	   		new Thread() {
				@Override
	   			public void run() {
					// 암기 대상 단어들을 모두 암기미완료로 리셋한다.
					JvManager.getInstance().rememorizeAllMemorizeTarget();
					
			        // 단어 데이터를 로드합니다.
			        readyMemorizeTargetVocabularyData();

					Message msg = Message.obtain();
					msg.what = MSG_VOCABULARY_MEMORIZE_START;
					mVocabularyDataLoadHandler.sendMessage(msg);
	   			};
	   		}.start();

			return true;
			
		case R.id.jvm_preferences:
			// 설정 페이지를 띄운다.
			startActivityForResult(new Intent(this, OptionActivity.class), R.id.jvm_preferences);

			return true;
		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// @@@@@ 변경된 내역이 없으면 다시 로드할 필요는 없다, resultCode를 이용
		if (requestCode == R.id.jvm_show_all_vocabulary) {
			assert mProgressDialog == null;

			// 데이터를 로드하는 중임을 나타내는 프로그레스 대화상자를 보인다.
			mProgressDialog = ProgressDialog.show(this, null, "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.", true, false);

	   		new Thread() {
				@Override
	   			public void run() {
			        // 단어 데이터를 로드합니다.
			        readyMemorizeTargetVocabularyData();

					Message msg = Message.obtain();
					msg.what = MSG_VOCABULARY_MEMORIZE_START;
					mVocabularyDataLoadHandler.sendMessage(msg);
	   			};
	   		}.start();
		} else if (requestCode == R.id.jvm_preferences) {
			initPreference(true);
		}
	}
	
	private void initPreference(boolean showNextVocabulary) {
		SharedPreferences mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		String memorizeTargetItem = mPreferences.getString(JvDefines.JV_SPN_MEMORIZE_TARGET_ITEM, "0");
		if (mIsJapanVocabularyOutputMode != (TextUtils.equals(memorizeTargetItem, "0"))) {
			if (TextUtils.equals(memorizeTargetItem, "0") == true) {
				mIsJapanVocabularyOutputMode = true;
			} else {
				mIsJapanVocabularyOutputMode = false;
			}

			if (showNextVocabulary == true)
				showNextVocabulary();
		}
	}

	private void updateJvDB() {
		// 로컬 단어 DB의 버전정보를 구한다.
		SharedPreferences mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		String localDbVersion = mPreferences.getString(JvDefines.JV_SPN_DB_VERSION, "");

		// 단어 DB가 갱신되었으면 네트워크를 통하여 데이터를 새로 내려받는다.
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

		if (remoteDbVersion != null && TextUtils.isEmpty(remoteDbVersion) == false && remoteDbVersion.equals(localDbVersion) == false) {
			Message msg = Message.obtain();
			msg.what = MSG_PROGRESS_DIALOG_REFRESH;
			msg.obj = "단어 DB를 업데이트 하고 있습니다.";
			mVocabularyDataLoadHandler.sendMessage(msg);
			
			String jvDbPath = String.format("%s/%s/", Environment.getExternalStorageDirectory().getAbsolutePath(), JvDefines.JV_MAIN_FOLDER_NAME);
			File f = new File(jvDbPath);
			if (f.exists() == false) {
				f.mkdir();
			}

			jvDbPath += JvDefines.JV_VOCABULARY_DB;

			// 단어 DB 파일을 내려받는다.
			try {
				URL url = new URL(JvDefines.JV_DB_DOWNLOAD_URL);
				BufferedInputStream bis = new BufferedInputStream(url.openConnection().getInputStream());

				int current = 0;
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				while ((current = bis.read()) != -1) {
					baf.append((byte)current);
				}

				f = new File(jvDbPath);
				f.delete();

				FileOutputStream fos = new FileOutputStream(f);
				fos.write(baf.toByteArray());
				fos.close();

				mPreferences.edit().putString(JvDefines.JV_SPN_DB_VERSION, remoteDbVersion).commit();
			} catch (Exception e) {
				Log.d(TAG, e.getMessage());
				
				msg = Message.obtain();
				msg.what = MSG_TOAST_SHOW;
				msg.obj = "새로운 단어 DB의 업데이트가 실패하였습니다.";
				mVocabularyDataLoadHandler.sendMessage(msg);
			}
		}
		
		Message msg = Message.obtain();
		msg.what = MSG_PROGRESS_DIALOG_REFRESH;
		msg.obj = "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.";
		mVocabularyDataLoadHandler.sendMessage(msg);

    	// DB에서 단어 데이터를 읽어들인다.
		if (JvManager.getInstance().initDataFromDB() == false) {
			msg = Message.obtain();
			msg.what = MSG_TOAST_SHOW;
			msg.obj = "암기 할 단어 데이터의 로딩이 실패하였습니다.";
			mVocabularyDataLoadHandler.sendMessage(msg);
		}
	}

	private void readyMemorizeTargetVocabularyData() {
		Message msg = Message.obtain();
		msg.what = MSG_PROGRESS_DIALOG_REFRESH;
		msg.obj = "암기 할 단어를 불러들이고 있습니다.\n잠시만 기다려주세요.";
		mVocabularyDataLoadHandler.sendMessage(msg);

		// 암기 대상 단어들만을 필터링한다.
		mJvList.clear();
		mMemorizeTargetJvCount = JvManager.getInstance().getMemorizeTargetJvList(mJvList);
	}

	private void showNextVocabulary() {
		TextView tvJapanVocabulary = (TextView)findViewById(R.id.vocabulary);

		if (mJvList.isEmpty() == true) {
			mJvCurrentIndex = -1;
			tvJapanVocabulary.setText("");
			
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
				tvJapanVocabulary.setText(mJvList.get(mJvCurrentIndex).getVocabulary());
			else
				tvJapanVocabulary.setText(mJvList.get(mJvCurrentIndex).getVocabularyGana());
		}
	}

	private void updateJapanVocabularyInfo() {
		TextView tvJapanVocabularyInfo = (TextView)findViewById(R.id.jv_info);
		tvJapanVocabularyInfo.setText(String.format("완료 %d개 / 전체 %d개", mMemorizeTargetJvCount - mJvList.size(), mMemorizeTargetJvCount));
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
    	if (v.getId() == R.id.vocabulary && mJvCurrentIndex != -1) {
        	switch (event.getAction()) {
		    	case MotionEvent.ACTION_DOWN:
		    		mTouchEventHandler.removeMessages(MSG_TOUCHEVT_LONG_PRESS);
		    		mTouchEventHandler.sendEmptyMessageAtTime(MSG_TOUCHEVT_LONG_PRESS, event.getDownTime() + LONG_PRESS_TIMEOUT);
		    		break;

		    	case MotionEvent.ACTION_MOVE:
		    		// 약간의 움직임만으로 메시지가 제거되므로 주석처리한다.
		    		// mHandler.removeMessages(LONG_PRESS);
		    		break;

		    	case MotionEvent.ACTION_UP:
		    		// MSG_TOUCHEVT_LONG_PRESS 메시지가 처리전이라면 MSG_TOUCHEVT_TAP으로 인식되어 처리된다.
		    		if (mTouchEventHandler.hasMessages(MSG_TOUCHEVT_LONG_PRESS) == true) {
		    			mTouchEventHandler.removeMessages(MSG_TOUCHEVT_LONG_PRESS);
		    			mTouchEventHandler.sendEmptyMessage(MSG_TOUCHEVT_TAP);
		    		}
		    		break;
		    	
		    	case MotionEvent.ACTION_CANCEL:
		    		mTouchEventHandler.removeMessages(MSG_TOUCHEVT_LONG_PRESS);
		    		break;
        	}
    	} else {
    		mTouchEventHandler.removeMessages(MSG_TOUCHEVT_LONG_PRESS);
    	}

    	return true;
	}

	private Handler mVocabularyDataLoadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_PROGRESS_DIALOG_REFRESH) {
				if (mProgressDialog != null)
					mProgressDialog.setMessage((String)msg.obj);
			} else if (msg.what == MSG_VOCABULARY_MEMORIZE_START) {
		    	updateJapanVocabularyInfo();
	        	showNextVocabulary();

				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				mProgressDialog = null;
			} else if (msg.what == MSG_TOAST_SHOW) {
				Toast.makeText(JvActivity.this, (String)msg.obj, Toast.LENGTH_LONG).show();
			}
		};
	};

	private Handler mTouchEventHandler = new Handler() {
		@Override
    	public void handleMessage(Message msg){
    		switch(msg.what) {
	    		case MSG_TOUCHEVT_LONG_PRESS:
	    			if (mJvCurrentIndex != -1) {
						// 진동을 발생시킨다.
						Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
						vibrator.vibrate(30);

	    				new AlertDialog.Builder(JvActivity.this)
	    				.setTitle("암기완료")
	            		.setMessage("단어를 암기 완료하셨나요?")
	            		.setPositiveButton("예", new DialogInterface.OnClickListener() {
	    					@Override
	    					public void onClick(DialogInterface dialog, int which) {
	    						mJvList.get(mJvCurrentIndex).setMemorizeCompleted(true, true);
	    						mJvList.remove(mJvCurrentIndex);
	    						updateJapanVocabularyInfo();
	    						showNextVocabulary();
	    		
	    						// 진동을 발생시킨다.
	    						Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
	    						vibrator.vibrate(30);

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
			
	    		case MSG_TOUCHEVT_TAP:
	    			if (mJvCurrentIndex != -1) {
	    				Intent intent = new Intent(JvActivity.this, JvDetailActivity.class);
	    				intent.putExtra("idx", mJvList.get(mJvCurrentIndex).getIdx());
	    				startActivity(intent);
	    			}
	    			break;
	    			
				default:
					 throw new RuntimeException("Unknown message " + msg);
    		}
    	}
    };

}
