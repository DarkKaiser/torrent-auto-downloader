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

	// �Ϻ��� ���� �ܾ �ϱ������� ��������� ����
	// �� ���� false �̸� ���󰡳�/��Ÿ������ �ϱ������� ����Ѵ�.
	private boolean mIsJapanVocabularyOutputMode = true;

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
        		.setMessage("SD ī�尡 ����Ʈ �����Ǿ� �ֽ��ϴ�. �����͸� �ε��� �� �����ϴ�.")
        		.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
					
        			@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
					
				})
        		.show();

        	return;
        }

        // ȯ�漳�� ���� �ε��Ѵ�.
        initSharedPreference(false);

        TextSwitcher vocabularyTextSwitcher = (TextSwitcher)findViewById(R.id.vocabulary);

        vocabularyTextSwitcher.setFactory(this);  
        vocabularyTextSwitcher.setOnTouchListener(this);
        vocabularyTextSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        vocabularyTextSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

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
        
		// ���α׷��� ��ȭ���ڸ� ���δ�.
		mProgressDialog = ProgressDialog.show(this, null, "�ܾ� DB�� ������Ʈ ���θ� Ȯ���ϴ� �� �Դϴ�.", true, false);

   		new Thread() {
   			
			@Override
   			public void run() {
		        // ���ο� �ܾ� �����Ͱ� ���� ���, �ܾ� �����͸� ������Ʈ�մϴ�.
		        updateJvDB();

		        // �ܾ� �����͸� �ε��մϴ�.
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

			// �����͸� ó���� ���� ������ ���α׷��� ��ȭ���ڸ� ���δ�.
			mProgressDialog = ProgressDialog.show(this, null, "��û�Ͻ� �۾��� ó�� ���Դϴ�.", true, false);

	   		new Thread() {
				@Override
	   			public void run() {
					// �ϱ� ��� �ܾ���� ��� �ϱ�̿Ϸ�� �����Ѵ�.
					JvManager.getInstance().rememorizeAllMemorizeTarget();
					
			        // �ܾ� �����͸� �ε��մϴ�.
			        readyMemorizeTargetVocabularyData();

					Message msg = Message.obtain();
					msg.what = MSG_VOCABULARY_MEMORIZE_START;
					mVocabularyDataLoadHandler.sendMessage(msg);
	   			};
	   		}.start();

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
			        readyMemorizeTargetVocabularyData();

					Message msg = Message.obtain();
					msg.what = MSG_VOCABULARY_MEMORIZE_START;
					mVocabularyDataLoadHandler.sendMessage(msg);
	   			};
	   		}.start();
		} else if (requestCode == R.id.jvm_preferences) {
			initSharedPreference(true);
		}
	}

	private void initSharedPreference(boolean showNextVocabulary) {
		SharedPreferences mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		String memorizeTargetItem = mPreferences.getString(JvDefines.JV_SPN_MEMORIZE_TARGET_ITEM, "0");
		if (mIsJapanVocabularyOutputMode != (TextUtils.equals(memorizeTargetItem, "0"))) {
			if (TextUtils.equals(memorizeTargetItem, "0") == true) {
				mIsJapanVocabularyOutputMode = true;
			} else {
				mIsJapanVocabularyOutputMode = false;
			}

			// ��µ� �ܾ��� �׸��� �ٲ���� ��쿡�� ���� ���ڸ� ���δ�.
			if (showNextVocabulary == true)
				showNextVocabulary();
		}
	}

	private void updateJvDB() {
		// ���� �ܾ� DB�� ���������� ���Ѵ�.
		SharedPreferences mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		String localDbVersion = mPreferences.getString(JvDefines.JV_SPN_DB_VERSION, "");

		// �ܾ� DB�� ���ŵǾ����� ��Ʈ��ũ�� ���Ͽ� �����͸� ���� �����޴´�.
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
			msg.obj = "�ܾ� DB�� ������Ʈ  ���θ� Ȯ���� �� �����ϴ�.";
			mVocabularyDataLoadHandler.sendMessage(msg);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());

			Message msg = Message.obtain();
			msg.what = MSG_TOAST_SHOW;
			msg.obj = "�ܾ� DB�� ������Ʈ  ���θ� Ȯ���� �� �����ϴ�.";
			mVocabularyDataLoadHandler.sendMessage(msg);
		}

		if (remoteDbVersion != null && TextUtils.isEmpty(remoteDbVersion) == false && remoteDbVersion.equals(localDbVersion) == false) {
			Message msg = Message.obtain();
			msg.what = MSG_PROGRESS_DIALOG_REFRESH;
			msg.obj = "�ܾ� DB�� ������Ʈ �ϰ� �ֽ��ϴ�.";
			mVocabularyDataLoadHandler.sendMessage(msg);

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

				int readBytes = 0;
				byte[] bytesIn = new byte[1024];
				ByteArrayBuffer baf = new ByteArrayBuffer(1024);

		        while ((readBytes = bis.read(bytesIn)) >= 0) {
		            baf.append(bytesIn, 0, readBytes);
		        }

				bis.close();

				if (contentLength > 0 && contentLength != baf.length()) {
					msg = Message.obtain();
					msg.what = MSG_TOAST_SHOW;
					msg.obj = "���ο� �ܾ� DB�� ������Ʈ�� �����Ͽ����ϴ�.";
					mVocabularyDataLoadHandler.sendMessage(msg);
				} else {
					f = new File(jvDbPath);
					f.delete();

					FileOutputStream fos = new FileOutputStream(f);
					fos.write(baf.toByteArray());
					fos.close();

					mPreferences.edit().putString(JvDefines.JV_SPN_DB_VERSION, remoteDbVersion).commit();
				}
			} catch (Exception e) {
				Log.d(TAG, e.getMessage());
				
				msg = Message.obtain();
				msg.what = MSG_TOAST_SHOW;
				msg.obj = "���ο� �ܾ� DB�� ������Ʈ�� �����Ͽ����ϴ�.";
				mVocabularyDataLoadHandler.sendMessage(msg);
			}
		}

		Message msg = Message.obtain();
		msg.what = MSG_PROGRESS_DIALOG_REFRESH;
		msg.obj = "�ϱ� �� �ܾ �ҷ����̰� �ֽ��ϴ�.\n��ø� ��ٷ��ּ���.";
		mVocabularyDataLoadHandler.sendMessage(msg);

    	// DB���� �ܾ� �����͸� �о���δ�.
		if (JvManager.getInstance().initDataFromDB() == false) {
			msg = Message.obtain();
			msg.what = MSG_TOAST_SHOW;
			msg.obj = "�ϱ� �� �ܾ� �������� �ε��� �����Ͽ����ϴ�.";
			mVocabularyDataLoadHandler.sendMessage(msg);
		}
	}

	private void readyMemorizeTargetVocabularyData() {
		Message msg = Message.obtain();
		msg.what = MSG_PROGRESS_DIALOG_REFRESH;
		msg.obj = "�ϱ� �� �ܾ �ҷ����̰� �ֽ��ϴ�.\n��ø� ��ٷ��ּ���.";
		mVocabularyDataLoadHandler.sendMessage(msg);

		// �ϱ� ��� �ܾ�鸸�� ���͸��Ѵ�.
		mJvList.clear();
		mMemorizeTargetJvCount = JvManager.getInstance().getMemorizeTargetJvList(mJvList);
	}

	private void showNextVocabulary() {
		TextSwitcher vocabularyTextSwitcher = (TextSwitcher)findViewById(R.id.vocabulary);

		if (mJvList.isEmpty() == true) {
			mJvCurrentIndex = -1;
			vocabularyTextSwitcher.setText("");

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

			// ȭ�鿡 ���� �ܾ ����Ѵ�.
			if (mIsJapanVocabularyOutputMode == true)
				vocabularyTextSwitcher.setText(mJvList.get(mJvCurrentIndex).getVocabulary());
			else
				vocabularyTextSwitcher.setText(mJvList.get(mJvCurrentIndex).getVocabularyGana());
		}
	}

	private void updateJvMemorizeInfo() {
		TextView jvInfo = (TextView)findViewById(R.id.jv_info);
		jvInfo.setText(String.format("�ϱ�Ϸ� %d�� / �ϱ��� %d��", mMemorizeTargetJvCount - mJvList.size(), mMemorizeTargetJvCount));
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

	@Override
	public View makeView() {
		TextView t = new TextView(this);  
        t.setGravity(Gravity.CENTER);
        t.setTypeface(Typeface.SERIF);
        t.setTextSize(TypedValue.COMPLEX_UNIT_PT, 30);
        t.setLayoutParams(new TextSwitcher.LayoutParams(LayoutParams.FILL_PARENT,
        		LayoutParams.FILL_PARENT));

        return t;  
	}

}
