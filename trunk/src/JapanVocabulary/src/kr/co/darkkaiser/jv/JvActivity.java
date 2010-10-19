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

        TextView tvVocabulary = (TextView)findViewById(R.id.vocabulary);
        tvVocabulary.setOnTouchListener(this);

        Button btnNextVocabulary = (Button)findViewById(R.id.next_vocabulary);
        btnNextVocabulary.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showNextVocabulary();

				// ������ �߻���Ų��.
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

					// ������ �߻���Ų��.
					Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(30);
				}
			}
		});

		// �����͸� �ε��ϴ� ���߿� ���α׷��� ��ȭ���ڸ� ���δ�.
		mProgressDialog = ProgressDialog.show(this, null, "��ø� ��ٷ� �ּ���...", true, false);

   		new Thread() {
			@Override
   			public void run() {
		        // �ܾ� �����͸� ������Ʈ�մϴ�.
		        updateData();

		        // ���α׷��� �ʱ�ȭ�մϴ�.
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

			// �����͸� �ε��ϴ� ���߿� ���α׷��� ��ȭ���ڸ� ���δ�.
			mProgressDialog = ProgressDialog.show(this, null, "��ø� ��ٷ� �ּ���...", true, false);

	   		new Thread() {
				@Override
	   			public void run() {
					// �ϱ� ��� �ܾ���� ��� �ϱ�̿Ϸ�� �����Ѵ�.
					JvManager.getInstance().rememorizeAllMemorizeTarget();
					
			        // ���α׷��� �ʱ�ȭ�մϴ�.
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

		if (requestCode == 0/* �ܾ� �󼼺��� */) {
			// �����ϴ� �۾� ����!
		} else if (requestCode == R.id.jvm_show_all_vocabulary) {
			assert mProgressDialog == null;

			// �����͸� �ε��ϴ� ���߿� ���α׷��� ��ȭ���ڸ� ���δ�.
			mProgressDialog = ProgressDialog.show(this, null, "��ø� ��ٷ� �ּ���...", true, false);

	   		new Thread() {
				@Override
	   			public void run() {
			        // ���α׷��� �ʱ�ȭ�մϴ�.
			        readyMemorizeTargetVocabularyData();

					mVocabularyDataLoadedHandler.sendEmptyMessage(-1);
	   			};
	   		}.start();
		}
	}

	private void updateData() {
		mVocabularyDataLoadedHandler.sendEmptyMessage(1);
		
		// ������ DB ���������� ���Ѵ�.
		SharedPreferences mPreferences = getSharedPreferences("jv_setup", MODE_PRIVATE);
		String localDbVersion = mPreferences.getString("jv_db_version", "");

		// ��Ʈ��ũ�� ���Ͽ� �����͸� �����޴´�.
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

			// DB ������ �����޴´�.
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
		
    	// DB���� �ܾ� �����͸� �о���δ�.
		if (JvManager.getInstance().initDataFromDB() == false) {
			mVocabularyDataLoadedHandler.sendEmptyMessage(3);
		}
	}

	private void readyMemorizeTargetVocabularyData() {
		mVocabularyDataLoadedHandler.sendEmptyMessage(2);

		// �о���� �������߿��� �ϱ� ��� �ܾ�鸸�� ���͸��Ѵ�.
		mJapanVocabularyList.clear();
    	JvManager.getInstance().getMemorizeTargetVocabulary(mJapanVocabularyList);
	}

	private void showNextVocabulary() {
		TextView tvJapanVocabulary = (TextView)findViewById(R.id.vocabulary);

		if (mJapanVocabularyList.isEmpty() == true) {
			mCurrentJapanVocabularyIndex = -1;
			Toast.makeText(this, "�ϱ��� ���ڰ� �����ϴ�.", Toast.LENGTH_SHORT).show();

			tvJapanVocabulary.setText("");
		} else {
			mCurrentJapanVocabularyIndex = mRandom.nextInt(mJapanVocabularyList.size());

			// ȭ�鿡 ���� ���ڸ� ����Ѵ�.
			tvJapanVocabulary.setText(mJapanVocabularyList.get(mCurrentJapanVocabularyIndex).getVocabulary());
		}
	}

	private void updateJapanVocabularyInfo() {
		TextView tvJapanVocabularyInfo = (TextView)findViewById(R.id.jv_info);
		tvJapanVocabularyInfo.setText(String.format("�ϱ� ��� �ܾ� : %d��", mJapanVocabularyList.size()));
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
			// �۾� �����忡�� ���� �ڵ鷯 �޽����� ��쿡�� ���� ��ȭ���ڸ� �ݴ´�.
			if (msg.what == -1) {
		    	updateJapanVocabularyInfo();
	        	showNextVocabulary();

				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				mProgressDialog = null;
			} else if (msg.what == 1) {
				if (mProgressDialog != null)
					mProgressDialog.setMessage("�ܾ� �����͸� ������Ʈ���Դϴ�...");
			} else if (msg.what == 2) {
				if (mProgressDialog != null)
					mProgressDialog.setMessage("�ϱ��� �ܾ �ҷ����� �ֽ��ϴ�...");
			} else if (msg.what == 3) {
				Toast.makeText(JvActivity.this, "DB���� �ܾ �ε��� �� �����ϴ�.", Toast.LENGTH_LONG).show();
			} else if (msg.what == 4) {
				Toast.makeText(JvActivity.this, "������ ������ DB ���� ������ �ε��� �� �����ϴ�.", Toast.LENGTH_LONG).show();
			} else if (msg.what == 5) {
				Toast.makeText(JvActivity.this, "���ο� DB ������ �ٿ�ε� �� �� �����ϴ�.", Toast.LENGTH_LONG).show();
			}
		};
	};

}
