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

	// Long Press �� �Ǵ��ϴ� �ð� ��
	private static final int LONG_PRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();

	private Random mRandom = new Random();
	private ProgressDialog mProgressDialog = null;

	// ���� ȭ�鿡 �������� �ִ� �ܾ��� �ε���
	private int mJvCurrentIndex = -1;

	// �ϱ� ��� �ܾ� ��ü ����
	private int mMemorizeTargetJvCount = 0;

	// �ϱ� ��� �׸�
	private JapanVocabularyMemorizeTargetItem mJvMemorizeTargetItem = JapanVocabularyMemorizeTargetItem.VOCABULARY;

	// �ϱ� ��� �Ϻ��� �ܾ� ����Ʈ
	private ArrayList<JapanVocabulary> mJvList = new ArrayList<JapanVocabulary>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // SD ī���� ���¸� Ȯ���Ѵ�.
        String sdStatus = Environment.getExternalStorageState();
        if (sdStatus.equals(Environment.MEDIA_UNMOUNTED) == true) {
        	new AlertDialog.Builder(this)
        		.setTitle("SD ī�� ����")
        		.setMessage("SD ī�尡 ����Ʈ �����Ǿ� �ֽ��ϴ�. �ܾ� �����͸� �ε��� �� �����ϴ�.")
        		.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
					
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

        // ȯ�漳�� ���� �ε��Ѵ�.
        initSharedPreference(false);

        Button nextVocabulary = (Button)findViewById(R.id.next_vocabulary);
        nextVocabulary.setOnClickListener(new View.OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				// ������ �߻���Ų��.
				Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(30);

				showNextVocabulary();
			}
		});

        // ���� ���ͳݿ� ����Ǿ� �ִ����� ���θ� Ȯ���� ��, �ܾ� �����͸� ������Ʈ�Ѵ�.
        boolean isNowNetworkConnected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting() == true) {
			isNowNetworkConnected = true;

			// ���α׷��� ��ȭ���ڸ� ���δ�.
			mProgressDialog = ProgressDialog.show(this, null, "�ܾ� DB�� ������Ʈ ���θ� Ȯ���ϴ� �� �Դϴ�.", true, false);
		} else {
			// ���α׷��� ��ȭ���ڸ� ���δ�.
			mProgressDialog = ProgressDialog.show(this, null, "�ϱ� �� �ܾ �ҷ����̰� �ֽ��ϴ�.\n��ø� ��ٷ��ּ���.", true, false);			
		}

   		new Thread() {

   			// ���� ��Ʈ��ũ(3G Ȥ�� ��������)�� ����Ǿ� �ִ����� ���θ� ��Ÿ����.
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
						// ���� ����� ��Ʈ��ũ�� 3G �������� Ȯ���Ѵ�.
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
							// ���ο� �ܾ� DB�� �����մϴ�.
							boolean updateSucceeded = updateVocabularyDb(newVocabularyDbVersion, newVocabularyDbFileHash);

							// �ܾ� �����͸� �ʱ�ȭ�� ��, �ϱ⸦ �����մϴ�.
							initVocabularyDataAndStartMemorize(mIsNowNetworkConnected, updateSucceeded);
						}
					} else {
						// �ܾ� �����͸� �ʱ�ȭ�� ��, �ϱ⸦ �����մϴ�.
						initVocabularyDataAndStartMemorize(mIsNowNetworkConnected, false);
					}					
				} else {
					// �ܾ� �����͸� �ʱ�ȭ�� ��, �ϱ⸦ �����մϴ�.
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

			// �����͸� ó���� ���� ������ ���α׷��� ��ȭ���ڸ� ���δ�.
			mProgressDialog = ProgressDialog.show(this, null, "��û�Ͻ� �۾��� ó�� ���Դϴ�.", true, false);

	   		new Thread() {
				@Override
	   			public void run() {
					// �ϱ� ��� �ܾ���� ��� �ϱ�̿Ϸ�� �����Ѵ�.
					JapanVocabularyManager.getInstance().rememorizeAllMemorizeTarget();
					
			        // �ܾ� �����͸� �ε��մϴ�.
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

		// @@@@@ �ܾ� �ϱ� ������ ����� ����???

		if (requestCode == R.id.jvm_show_all_vocabulary) {
			assert mProgressDialog == null;

			// ȯ�漳�� ���� �ٲ������ Ȯ���Ѵ�.
			if ((resultCode & JvListActivity.ACTIVITY_RESULT_PREFERENCE_CHANGED) == JvListActivity.ACTIVITY_RESULT_PREFERENCE_CHANGED) {
				if ((resultCode & JvListActivity.ACTIVITY_RESULT_DATA_CHANGED) == JvListActivity.ACTIVITY_RESULT_DATA_CHANGED) {
					initSharedPreference(false);
				} else {
					initSharedPreference(true);
					return;
				}
			}

			// ����� ������ ������ �ٽ� �ε��� �ʿ䰡 �����Ƿ� �ٷ� ��ȯ�Ѵ�.
			if ((resultCode & JvListActivity.ACTIVITY_RESULT_DATA_CHANGED) != JvListActivity.ACTIVITY_RESULT_DATA_CHANGED)
				return;

			// �����͸� �ε��ϴ� ������ ��Ÿ���� ���α׷��� ��ȭ���ڸ� ���δ�.
			mProgressDialog = ProgressDialog.show(this, null, "�ϱ� �� �ܾ �ҷ����̰� �ֽ��ϴ�.\n��ø� ��ٷ��ּ���.", true, false);

	   		new Thread() {
				@Override
	   			public void run() {
			        // �ܾ� �����͸� �ε��մϴ�.
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

			// ��µ� �ܾ��� �׸��� �ٲ���� ��쿡�� ���� ���ڸ� ���δ�.
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

			Toast.makeText(this, "�ϱ� �� �ܾ �����ϴ�.", Toast.LENGTH_SHORT).show();
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

			// ���ڰ� �� ��Ʈ���� ũ�Ⱑ Ŀ�� ��� �� ������ �ʰ� ��Ʈ���� ũ�Ⱑ �پ��Ƿ�
			// ���� ��Ʈ���� ũ�⸦ ���̰� ���� ���� �ִ´�.
			vocabularyTextSwitcher.setText("");
			vocabularyTranslationTextSwitcher.setText("");
			
			JapanVocabulary jpVocabulary = mJvList.get(mJvCurrentIndex);
			if (jpVocabulary != null) {
				// ȭ�鿡 ���� �ܾ ����Ѵ�.
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
		jvInfo.setText(String.format("�ϱ�Ϸ� %d�� / �ϱ��� %d��", mMemorizeTargetJvCount - mJvList.size(), mMemorizeTargetJvCount));
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
		    		// �ణ�� �����Ӹ����� �޽����� ���ŵǹǷ� �ּ�ó���Ѵ�.
		    		// mHandler.removeMessages(LONG_PRESS);
		    		break;

		    	case MotionEvent.ACTION_UP:
		    		// MSG_TOUCHEVT_LONG_PRESS �޽����� ó�����̶�� MSG_TOUCHEVT_TAP���� �νĵǾ� ó���ȴ�.
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
			Toast.makeText(this, "'�ڷ�' ��ư�� �ѹ� �� �����ø� ����˴ϴ�.", Toast.LENGTH_SHORT).show();
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
        			.setTitle("�˸�")
        			.setMessage("Wi-Fi/3G���� ������ ��Ʈ��ũ ���°� �Ҿ����Ͽ� �ܾ� DB�� ������Ʈ ���θ� Ȯ���� �� �����ϴ�.")
        			.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
					
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
		        		.setTitle("�ܾ� ������Ʈ ����")
		        		.setPositiveButton("�ݱ�", new DialogInterface.OnClickListener() {
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
				mProgressDialog.setMessage("�ܾ� DB�� ������Ʈ�ϰ� �ֽ��ϴ�.");
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
				
				mProgressDialog = ProgressDialog.show(JvActivity.this, null, "�ϱ� �� �ܾ �ҷ����̰� �ֽ��ϴ�.\n��ø� ��ٷ��ּ���.", true, false);
			} else if (msg.what == MSG_VOCABULARY_DATA_DOWNLOAD_QUESTION) {
				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				final String newVocabularyDbVersion = msg.getData().getString("NEW_VOCABULARY_DB_VERSION");
				final String newVocabularyDbFileHash = msg.getData().getString("NEW_VOCABULARY_DB_FILE_HASH");

	        	new AlertDialog.Builder(JvActivity.this)
    				.setTitle("�˸�")
    				.setMessage("3G ��Ʈ��ũ�� ���ӵǾ����ϴ�. ������ ��ȭ�ᰡ �ΰ��� �� �ֽ��ϴ�. �ܾ� DB�� ������Ʈ�Ͻðڽ��ϱ�?")
    				.setPositiveButton("�����", new DialogInterface.OnClickListener() {
				
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						updateAndInitVocabularyDataOnMobileNetwork(newVocabularyDbVersion, newVocabularyDbFileHash, true);
    					}
    				})
					.setNegativeButton("���", new DialogInterface.OnClickListener() {
						
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
						// ������ �߻���Ų��.
						Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
						vibrator.vibrate(30);

	    				new AlertDialog.Builder(JvActivity.this)
	    					.setTitle("�ϱ�Ϸ�")
	    					.setMessage("�ܾ� �ϱ⸦ �Ϸ��ϼ̳���?")
	    					.setPositiveButton("��", new DialogInterface.OnClickListener() {
	    						
	    						@Override
	    						public void onClick(DialogInterface dialog, int which) {
	    							// ������ �߻���Ų��.
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
	    					.setNegativeButton("�ƴϿ�", new DialogInterface.OnClickListener() {
	    						
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
	    			// �����ϴ� �۾� ����
	    			break;
	    			
				default:
					 throw new RuntimeException("Unknown message " + msg);
    		}
    	}
    };

	private ArrayList<String> checkNewVocabularyDb() {
		// ���� �ܾ� DB�� ���������� ���Ѵ�.
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

			// �ܾ� DB�� ���� ���θ� Ȯ���Ѵ�.
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
			msg.obj = "�ܾ� DB�� ������Ʈ  ���θ� Ȯ���� �� �����ϴ�.";
			mVocabularyDataLoadHandler.sendMessage(msg);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());

			Message msg = Message.obtain();
			msg.what = MSG_TOAST_SHOW;
			msg.obj = "�ܾ� DB�� ������Ʈ  ���θ� Ȯ���� �� �����ϴ�.";
			mVocabularyDataLoadHandler.sendMessage(msg);
		}

		return null;
	}

	private void updateAndInitVocabularyDataOnMobileNetwork(String newVocabularyDbVersion, String newVocabularyDbFileHash, boolean isUpdateVocabularyDb) {
		if (isUpdateVocabularyDb == true)
			mProgressDialog = ProgressDialog.show(JvActivity.this, null, "�ܾ� DB�� ������Ʈ�ϰ� �ֽ��ϴ�.", true, false);			
		else
			mProgressDialog = ProgressDialog.show(JvActivity.this, null, "�ϱ� �� �ܾ �ҷ����̰� �ֽ��ϴ�.\n��ø� ��ٷ��ּ���.", true, false);			

		new Thread() {

			// �ܾ� DB�� ������Ʈ ������ ���� �÷���
			private boolean mIsUpdateVocabularyDb = false;
			
			// ���� ������Ʈ �� �ܾ� DB�� ���� �� ���� �ؽ���
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
					// ���ο� �ܾ� DB�� �����մϴ�.
					updateSucceeded = updateVocabularyDb(mNewVocabularyDbVersion, mNewVocabularyDbFileHash);					
				}

				// �ܾ� �����͸� �ʱ�ȭ�� ��, �ϱ⸦ �����մϴ�.
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

		// �ܾ� DB ������ �����޴´�.
		try {
			URL url = new URL(JvDefines.JV_DB_DOWNLOAD_URL);

			URLConnection con = url.openConnection();
			int contentLength = con.getContentLength();
			BufferedInputStream bis = new BufferedInputStream(con.getInputStream());

			// �ٿ�ε� ���� �ܾ� DB�� ũ�⿡ ���� ������ ���α׷����ٿ� ����Ѵ�.
			Bundle bundle = new Bundle();
			bundle.putInt("TOTAL_VOCABULARY_DB_SIZE", contentLength);

			msg = Message.obtain();
			msg.what = MSG_VOCABULARY_DATA_DOWNLOAD_START;
            msg.setData(bundle);
			mVocabularyDataLoadHandler.sendMessage(msg);

			// �ش� �޽����� ó���� �Ϸ�� ������ ����Ѵ�.
			while (mVocabularyDataLoadHandler.hasMessages(MSG_VOCABULARY_DATA_DOWNLOAD_START) == true) {
				Thread.sleep(10);
			}

			int readBytes = 0;
			byte[] bytesIn = new byte[1024];
			ByteArrayBuffer baf = new ByteArrayBuffer(1024);

	        while ((readBytes = bis.read(bytesIn)) >= 0) {
	            baf.append(bytesIn, 0, readBytes);

	            // ������� ���� �ܾ� DB�� ũ�� ������ ���α׷����ٿ� ����Ѵ�.
				bundle = new Bundle();
				bundle.putInt("RECV_VOCABULARY_DB_SIZE", baf.length());

				msg = Message.obtain();
				msg.what = MSG_VOCABULARY_DATA_DOWNLOADING;
	            msg.setData(bundle);
				mVocabularyDataLoadHandler.sendMessage(msg);
	        }

			// �ش� �޽����� ó���� �Ϸ�� ������ ����Ѵ�.
			while (mVocabularyDataLoadHandler.hasMessages(MSG_VOCABULARY_DATA_DOWNLOADING) == true) {
				Thread.sleep(10);
			}

			bis.close();

			if (contentLength > 0 && contentLength != baf.length()) {
				msg = Message.obtain();
				msg.what = MSG_TOAST_SHOW;
				msg.obj = "���ο� �ܾ� DB�� ������Ʈ�� �����Ͽ����ϴ�.";
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

					// �ٿ�ε� ���� ������ �ؽ����� ���Ͽ� �ùٸ� �������� ���Ѵ�.
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
						msg.obj = "���ο� �ܾ� DB�� ������Ʈ�� �����Ͽ����ϴ�(���� : ��ȿ���� ���� �ܾ� DB ����).";
						mVocabularyDataLoadHandler.sendMessage(msg);
					}
				}
			}
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());

			msg = Message.obtain();
			msg.what = MSG_TOAST_SHOW;
			msg.obj = "���ο� �ܾ� DB�� ������Ʈ�� �����Ͽ����ϴ�.";
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
		msg.obj = "�ϱ� �� �ܾ �ҷ����̰� �ֽ��ϴ�.\n��ø� ��ٷ��ּ���.";
		mVocabularyDataLoadHandler.sendMessage(msg);

		// DB���� �ܾ� �����͸� �о���δ�.
		if (JapanVocabularyManager.getInstance().initDataFromDB() == false) {
			msg = Message.obtain();
			msg.what = MSG_TOAST_SHOW;
			msg.obj = "�ܾ� DB���� �������� �ε��� �����Ͽ����ϴ�.";
			mVocabularyDataLoadHandler.sendMessage(msg);
		}

		// �ܾ� �����͸� �ε��մϴ�.
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
				
				// ������ �ѹ��̻� ������Ʈ �� ��쿡 �ѿ��� �ܾ� ������Ʈ ������ ���δ�.
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

        // �ܾ� �ϱ⸦ �����մϴ�.
		msg = Message.obtain();
		msg.what = MSG_VOCABULARY_MEMORIZE_START;
		mVocabularyDataLoadHandler.sendMessage(msg);
	}

	private void loadMemorizeTargetVocabularyData() {
		Message msg = Message.obtain();
		msg.what = MSG_PROGRESS_DIALOG_REFRESH;
		msg.obj = "�ϱ� �� �ܾ �ҷ����̰� �ֽ��ϴ�.\n��ø� ��ٷ��ּ���.";
		mVocabularyDataLoadHandler.sendMessage(msg);

		// �ϱ� ��� �ܾ�鸸�� ���͸��Ѵ�.
		mJvList.clear();
		mMemorizeTargetJvCount = JapanVocabularyManager.getInstance().getMemorizeTargetJvList(mJvList);			

		// �ܾ� �ϱ� ������ ���� �����Ѵ�.
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
