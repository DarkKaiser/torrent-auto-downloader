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
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// @@@@@
public class JvActivity extends Activity implements OnTouchListener {

	private Random mRandom = new Random();
	private ProgressDialog mProgressDialog = null;

	private int mCurrentJapanVocabularyIndex = -1;
	private ArrayList<JapanVocabulary> mJapanVocabularyList = new ArrayList<JapanVocabulary>();

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
        
        Button btnMemorizeCompleted = (Button)findViewById(R.id.memorize_completed);
        btnMemorizeCompleted.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCurrentJapanVocabularyIndex != -1) {
					mJapanVocabularyList.get(mCurrentJapanVocabularyIndex).setMemorizeCompleted(true, true);
					mJapanVocabularyList.remove(mCurrentJapanVocabularyIndex);
					updateJapanVocabularyInfo();
					showNextVocabulary();

					// 진동을 발생시킨다.
					Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(30);
				}
			}
		});

		// 데이터를 로딩하는 도중에 프로그레스 대화상자를 보인다.
		mProgressDialog = ProgressDialog.show(this, null, "잠시만 기다려 주세요...", true, false);

   		new Thread() {
			@Override
   			public void run() {
		        // 단어 데이터를 업데이트합니다.
		        updateData();

		        // 프로그램을 초기화합니다.
		        readyMemorizeTargetVocabularyData();

				mVocabularyDataLoadedHandler.sendEmptyMessage(-1);
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

			// 데이터를 로딩하는 도중에 프로그레스 대화상자를 보인다.
			mProgressDialog = ProgressDialog.show(this, null, "잠시만 기다려 주세요...", true, false);

	   		new Thread() {
				@Override
	   			public void run() {
					// 암기 대상 단어들을 모두 암기미완료로 리셋한다.
					JvManager.getInstance().rememorizeAllMemorizeTarget();
					
			        // 프로그램을 초기화합니다.
			        readyMemorizeTargetVocabularyData();

					mVocabularyDataLoadedHandler.sendEmptyMessage(-1);
	   			};
	   		}.start();

			return true;
		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0/* 단어 상세보기 */) {
			// 수행하는 작업 없음!
		} else if (requestCode == R.id.jvm_show_all_vocabulary) {
			assert mProgressDialog == null;

			// 데이터를 로딩하는 도중에 프로그레스 대화상자를 보인다.
			mProgressDialog = ProgressDialog.show(this, null, "잠시만 기다려 주세요...", true, false);

	   		new Thread() {
				@Override
	   			public void run() {
			        // 프로그램을 초기화합니다.
			        readyMemorizeTargetVocabularyData();

					mVocabularyDataLoadedHandler.sendEmptyMessage(-1);
	   			};
	   		}.start();
		}
	}

	private void updateData() {
		mVocabularyDataLoadedHandler.sendEmptyMessage(1);
		
		// 로컬의 DB 버전정보를 구한다.
		SharedPreferences mPreferences = getSharedPreferences("jv_setup", MODE_PRIVATE);
		String localDbVersion = mPreferences.getString("jv_db_version", "");

		// 네트워크를 통하여 데이터를 내려받는다.
		String remoteDbVersion = null;

		try {
			URL url = new URL("http://darkkaiser.cafe24.com/data/jv_db_version.html");
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
			Log.d("JapanVocabulary", e.getMessage());
			mVocabularyDataLoadedHandler.sendEmptyMessage(4);
		} catch (Exception e) {
			Log.d("JapanVocabulary", e.getMessage());
			mVocabularyDataLoadedHandler.sendEmptyMessage(4);
		}

		if (remoteDbVersion != null && TextUtils.isEmpty(remoteDbVersion) == false && remoteDbVersion.equals(localDbVersion) == false) {
			String jvDbPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/JapanVocabulary/";
			File f = new File(jvDbPath);
			if (f.exists() == false) {
				f.mkdir();
			}

			jvDbPath += "jv.db";

			// DB 파일을 내려받는다.
			try {
				URL url = new URL("http://darkkaiser.cafe24.com/data/jv.db");
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
				
				mPreferences.edit().putString("jv_db_version", remoteDbVersion).commit();
			} catch (Exception e) {
				Log.d("JapanVocabulary", e.getMessage());
				mVocabularyDataLoadedHandler.sendEmptyMessage(5);
			}
		}
		
    	// DB에서 단어 데이터를 읽어들인다.
		if (JvManager.getInstance().initDataFromDB() == false) {
			mVocabularyDataLoadedHandler.sendEmptyMessage(3);
		}
	}

	private void readyMemorizeTargetVocabularyData() {
		mVocabularyDataLoadedHandler.sendEmptyMessage(2);

		// 읽어들인 데이터중에서 암기 대상 단어들만을 필터링한다.
		mJapanVocabularyList.clear();
    	JvManager.getInstance().getMemorizeTargetVocabulary(mJapanVocabularyList);
	}

	private void showNextVocabulary() {
		TextView tvJapanVocabulary = (TextView)findViewById(R.id.vocabulary);

		if (mJapanVocabularyList.isEmpty() == true) {
			mCurrentJapanVocabularyIndex = -1;
			Toast.makeText(this, "암기할 한자가 없습니다.", Toast.LENGTH_SHORT).show();

			tvJapanVocabulary.setText("");
		} else {
			mCurrentJapanVocabularyIndex = mRandom.nextInt(mJapanVocabularyList.size());

			// 화면에 다음 한자를 출력한다.
			tvJapanVocabulary.setText(mJapanVocabularyList.get(mCurrentJapanVocabularyIndex).getVocabulary());
		}
	}

	private void updateJapanVocabularyInfo() {
		TextView tvJapanVocabularyInfo = (TextView)findViewById(R.id.jv_info);
		tvJapanVocabularyInfo.setText(String.format("암기 대상 단어 : %d개", mJapanVocabularyList.size()));
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.vocabulary) {
			if (event.getAction() == MotionEvent.ACTION_UP && mCurrentJapanVocabularyIndex != -1) {
				Intent intent = new Intent(this, JvDetailActivity.class);
				intent.putExtra("idx", mJapanVocabularyList.get(mCurrentJapanVocabularyIndex).getIdx());
				startActivityForResult(intent, 0);
			}

			return true;
		}

		return false;
	}

	private Handler mVocabularyDataLoadedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 작업 스레드에서 보낸 핸들러 메시지인 경우에는 진행 대화상자를 닫는다.
			if (msg.what == -1) {
		    	updateJapanVocabularyInfo();
	        	showNextVocabulary();

				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				mProgressDialog = null;
			} else if (msg.what == 1) {
				if (mProgressDialog != null)
					mProgressDialog.setMessage("단어 데이터를 업데이트중입니다...");
			} else if (msg.what == 2) {
				if (mProgressDialog != null)
					mProgressDialog.setMessage("암기할 단어를 불러오고 있습니다...");
			} else if (msg.what == 3) {
				Toast.makeText(JvActivity.this, "DB에서 단어를 로드할 수 없습니다.", Toast.LENGTH_LONG).show();
			} else if (msg.what == 4) {
				Toast.makeText(JvActivity.this, "원격지 서버의 DB 버전 정보를 로드할 수 없습니다.", Toast.LENGTH_LONG).show();
			} else if (msg.what == 5) {
				Toast.makeText(JvActivity.this, "새로운 DB 파일을 다운로드 할 수 없습니다.", Toast.LENGTH_LONG).show();
			}
		};
	};

}
