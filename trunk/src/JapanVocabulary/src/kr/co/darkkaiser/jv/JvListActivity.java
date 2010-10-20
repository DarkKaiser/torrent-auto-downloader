package kr.co.darkkaiser.jv;

import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import kr.co.darkkaiser.jv.detail.JvDetailActivity;
import kr.co.darkkaiser.jv.list.JvListAdapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class JvListActivity extends ListActivity {

	private Thread mSearchThread = null;
	private ProgressDialog mProgressDialog = null;

	private SharedPreferences mPreferences = null;

	private ArrayList<JapanVocabulary> mJvList = null;
	private JvListAdapter mJvListAdapter = null;
	private JvListSortMethod mJvListSortMethod = JvListSortMethod.REGISTRATION_DATE;
	
	private String mSearchWord = null;
	private long mSearchDateFirst = 0;
	private long mSearchDateLast = 0;

	// @@@@@
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jv_list);

        // 환경설정값을 읽어들인다.
		mPreferences = getSharedPreferences("jv_setup", MODE_PRIVATE);
		mJvListSortMethod = JvListSortMethod.valueOf(mPreferences.getString("jv_list_sort", JvListSortMethod.REGISTRATION_DATE.name()));

		// 리스트를 초기화한다. 
        mJvList = new ArrayList<JapanVocabulary>();
        mJvListAdapter = new JvListAdapter(this, R.layout.jv_listitem, mJvList);
        setListAdapter(mJvListAdapter);
 
        // 단어를 검색한다.
        searchVocabulary("");
	}

	// @@@@@
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.jv_list_menu, menu);
		return true;
	}

	// @@@@@
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.jvlm_search:
			final LinearLayout linear = (LinearLayout)View.inflate(JvListActivity.this, R.layout.jv_list_search, null);

			CheckBox cboAllDataSearch = (CheckBox)linear.findViewById(R.id.all_data_search);
			cboAllDataSearch.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CheckBox cboAllDataSearch = (CheckBox)v;
					Button btnSearchDateFirst = (Button)linear.findViewById(R.id.SearchDateFirst);
					Button btnSearchDateLast = (Button)linear.findViewById(R.id.SearchDateLast);
					btnSearchDateFirst.setEnabled(!cboAllDataSearch.isChecked());
					btnSearchDateLast.setEnabled(!cboAllDataSearch.isChecked());
				}
			});

	        // 현재의 날짜를 구하여 컨트롤에 반영한다.
	        Calendar currentDate = Calendar.getInstance();
			Button btnSearchDateFirst = (Button)linear.findViewById(R.id.SearchDateFirst);
			Button btnSearchDateLast = (Button)linear.findViewById(R.id.SearchDateLast);
			btnSearchDateFirst.setText(String.format("%04d/%02d/%02d", currentDate.get(Calendar.YEAR),
					currentDate.get(Calendar.MONTH) + 1,
					currentDate.get(Calendar.DATE)));

			btnSearchDateLast.setText(String.format("%04d/%02d/%02d", currentDate.get(Calendar.YEAR),
					currentDate.get(Calendar.MONTH) + 1,
					currentDate.get(Calendar.DATE)));

			btnSearchDateFirst.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Button btnSearchDateFirst = (Button)v;
		    		String searchDateString = btnSearchDateFirst.getText().toString().replace("/", "");
		        	new DatePickerDialog(JvListActivity.this,
		        			new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
									Button searchDateFirst = (Button)linear.findViewById(R.id.SearchDateFirst);				
									searchDateFirst.setText(String.format("%04d/%02d/%02d", year, monthOfYear + 1, dayOfMonth));
								}
							},
		        			Integer.parseInt(searchDateString.substring(0, 4)),
		        			Integer.parseInt(searchDateString.substring(4, 6)) - 1,
		        			Integer.parseInt(searchDateString.substring(6, 8))).show();
				}
			});
			
			btnSearchDateLast.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Button btnSearchDateLast = (Button)v;
		    		String searchDateString = btnSearchDateLast.getText().toString().replace("/", "");
		        	new DatePickerDialog(JvListActivity.this,
		        			new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
									Button searchDateLast = (Button)linear.findViewById(R.id.SearchDateLast);
									searchDateLast.setText(String.format("%04d/%02d/%02d", year, monthOfYear + 1, dayOfMonth));
								}
							},
		        			Integer.parseInt(searchDateString.substring(0, 4)),
		        			Integer.parseInt(searchDateString.substring(4, 6)) - 1,
		        			Integer.parseInt(searchDateString.substring(6, 8))).show();
				}
			});

			new AlertDialog.Builder(JvListActivity.this)
				.setTitle("한자 뜻 검색")
				.setView(linear)
				.setPositiveButton("검색", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText etSearchWord = (EditText)linear.findViewById(R.id.SearchWord);
						String searchWord = etSearchWord.getText().toString().trim();
						
						CheckBox cboAllDataSearch = (CheckBox)linear.findViewById(R.id.all_data_search);
						if (cboAllDataSearch.isChecked() == true) {
							searchVocabulary(searchWord);
						} else {
							try {
								Button btnSearchDateFirst = (Button)linear.findViewById(R.id.SearchDateFirst);
								long searchDateFirst = new SimpleDateFormat("yyyy/MM/dd").parse(btnSearchDateFirst.getText().toString()).getTime();

								Button btnSearchDateLast = (Button)linear.findViewById(R.id.SearchDateLast);
								long searchDateLast = new SimpleDateFormat("yyyy/MM/dd").parse(btnSearchDateLast.getText().toString()).getTime();
								
								if (searchDateFirst > searchDateLast) {
									long temp = searchDateFirst;
									searchDateFirst = searchDateLast;
									searchDateLast = temp;
								}

								searchDateLast += new SimpleDateFormat("HH:mm:ss").parse("23:59:59").getTime();
								searchDateLast += 999/* 밀리초 */;
								
								searchVocabulary(searchWord, searchDateFirst, searchDateLast);
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.show();

			return true;
		case R.id.jvlm_sort_kanji:
			// 리스트를 정렬합니다.
			startSortList(JvListSortMethod.KANJI);
			return true;
		case R.id.jvlm_sort_gana:
			// 리스트를 정렬합니다.
			startSortList(JvListSortMethod.GANA);
			return true;
		case R.id.jvlm_sort_translation:
			// 리스트를 정렬합니다.
			startSortList(JvListSortMethod.TRANSLATION);
			return true;
		case R.id.jvlm_sort_registration_date:
			// 리스트를 정렬합니다.
			startSortList(JvListSortMethod.REGISTRATION_DATE);
			return true;
		case R.id.jvlm_all_rememorize:				// 검색된 전체 단어 재암기
		case R.id.jvlm_all_memorize_completed:		// 검색된 전체 단어 암기 완료
		case R.id.jvlm_all_memorize_target:			// 검색된 전체 단어 암기 대상 만들기
		case R.id.jvlm_all_memorize_target_cancel:	// 검색된 전체 단어 암기 대상 해제
			// 데이터를 로딩하는 도중에 프로그레스 대화상자를 보인다.
			mProgressDialog = ProgressDialog.show(this, null, "잠시만 기다려 주세요...", true, false);

	   		new Thread() {
	   			
	   			private int mMenuItemId = 0;

	   			public Thread setMenuItemId(int menuItemId)
	   			{
	   				mMenuItemId = menuItemId;
	   				return this;
	   			}

				@Override
	   			public void run() {
					// 리스트의 idx 값을 구한다.
					ArrayList<Long> idxList = new ArrayList<Long>();
					for (int index = 0; index < mJvList.size(); ++index) {
						idxList.add(mJvList.get(index).getIdx());
					}

					JvManager.getInstance().resetMemorizeInfo(mMenuItemId, idxList);

					mDataChangedHandler.sendEmptyMessage(-2);
	   			};
	   		}
	   		.setMenuItemId(item.getItemId())
	   		.start();
	   		
			return true;
		}

		return false;
	}

	// @@@@@
	private void startSortList(JvListSortMethod jvListSortMethod) {
		// 정렬 방법을 저장한다.
		mJvListSortMethod = jvListSortMethod;
		mPreferences.edit().putString("jv_list_sort", mJvListSortMethod.name()).commit();

		assert mProgressDialog == null;

		// 정렬중에 프로그레스 대화상자를 보인다.
		mProgressDialog = ProgressDialog.show(this, null, "잠시만 기다려 주세요...", true, false);

   		new Thread() {
			@Override
   			public void run() {
				// 리스트 데이터 정렬합니다. 
				sortList();

				mDataChangedHandler.sendEmptyMessage(-1);
   			};
   		}.start();
	}
	
	// @@@@@
	private void sortList() {
		switch (mJvListSortMethod) {
		case KANJI:
			Collections.sort(mJvList, mJvVocabularyComparator);
			break;
		case GANA:
			Collections.sort(mJvList, mJvVocabularyGanaComparator);
			break;
		case TRANSLATION:
			Collections.sort(mJvList, mJvVocabularyTranslationComparator);
			break;
		case REGISTRATION_DATE:
			Collections.sort(mJvList, mJvRegistrationDateComparator);
			break;
		}
	}

	// @@@@@
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// 단어 상세페이지 호출
		Intent intent = new Intent(this, JvDetailActivity.class);
		intent.putExtra("idx", mJvList.get(position).getIdx());
		startActivity(intent);
	}

	// @@@@@
	private final static Comparator<JapanVocabulary> mJvVocabularyComparator = new Comparator<JapanVocabulary>() {
         private final Collator collator = Collator.getInstance();

         @Override
         public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
        	 return collator.compare(lhs.getVocabulary(), rhs.getVocabulary());
         }
	};

	// @@@@@
	private final static Comparator<JapanVocabulary> mJvVocabularyGanaComparator = new Comparator<JapanVocabulary>() {
        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
       	 	return collator.compare(lhs.getVocabularyGana(), rhs.getVocabularyGana());
        }
	};
	
	// @@@@@
	private final static Comparator<JapanVocabulary> mJvVocabularyTranslationComparator = new Comparator<JapanVocabulary>() {
        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
       	 	return collator.compare(lhs.getVocabularyTranslation(), rhs.getVocabularyTranslation());
        }
	};
	
	// @@@@@
	private final static Comparator<JapanVocabulary> mJvRegistrationDateComparator = new Comparator<JapanVocabulary>() {

        @Override
        public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
        	if (lhs.getRegistrationDate() > rhs.getRegistrationDate())
        		return 1;
        	else if (lhs.getRegistrationDate() < rhs.getRegistrationDate())
        		return -1;

        	return 0;
        }
	};

	// @@@@@
	private Handler mDataChangedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mJvListAdapter.notifyDataSetChanged();

			// ListActivity의 위젯이 아닌 스레드에서 보낸 핸들러 메시지인 경우에는 진행 대화상자를 닫는다.
			if (msg.what == -1) {
				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				TextView tvSearchInfo = (TextView)findViewById(R.id.search_info);
				TextView tvVocabularyCount = (TextView)findViewById(R.id.vocabulary_count);

				tvSearchInfo.setVisibility(View.VISIBLE);
				tvVocabularyCount.setVisibility(View.VISIBLE);
				tvVocabularyCount.setText(String.format("단어:%d개", mJvList.size()));
				
				mSearchThread = null;
				mProgressDialog = null;
			} else if (msg.what == -2) {
				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				mSearchThread = null;
				mProgressDialog = null;

				searchVocabulary(mSearchWord, mSearchDateFirst, mSearchDateLast);
			}
		};
	};
	
	// @@@@@
	private void searchVocabulary(String searchWord) {
		searchVocabulary(searchWord, -1, -1);
	}
	
	// @@@@@
	private void searchVocabulary(String searchWord, long searchDateFirst, long searchDateLast) {
		TextView tvSearchInfo = (TextView)findViewById(R.id.search_info);
		TextView tvVocabularyCount = (TextView)findViewById(R.id.vocabulary_count);

		tvSearchInfo.setVisibility(View.INVISIBLE);
		tvVocabularyCount.setVisibility(View.INVISIBLE);

		if (searchWord.equals("") == true) {
			tvSearchInfo.setText("검색:전체");
		} else {
			tvSearchInfo.setText("검색:" + searchWord);
		}

		// 검색중에 진행 대화상자를 보입니다.
		if (mPreferences == null) {
			mProgressDialog = ProgressDialog.show(this, null, "잠시만 기다려 주세요...", true, true);
			mProgressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					if (mSearchThread != null) {
						mSearchThread.interrupt();
					}
					
					mDataChangedHandler.sendEmptyMessage(-1);
					Toast.makeText(JvListActivity.this, "검색이 취소되었습니다", Toast.LENGTH_LONG).show();
				}
			});			
		}
		
		mSearchWord = searchWord;
		mSearchDateFirst = searchDateFirst;
		mSearchDateLast = searchDateLast;

   		mSearchThread = new JapanVocabularyListSearchThread(searchWord, searchDateFirst, searchDateLast);
   		mSearchThread.start();
	}
	
	// @@@@@
	public class JapanVocabularyListSearchThread extends Thread {

		private String mSearchWord = null;
		private long mSearchDateFirst = -1;
		private long mSearchDateLast = -1;

		public JapanVocabularyListSearchThread(String searchWord, long searchDateFirst, long searchDateLast) {
			mSearchWord = searchWord;
			if (searchDateFirst > searchDateLast) {
				mSearchDateFirst = searchDateLast;
				mSearchDateLast = searchDateFirst;
			} else {
				mSearchDateFirst = searchDateFirst;
				mSearchDateLast = searchDateLast;
			}
		}

		@Override
		public void run() {
			mJvList.clear();
			JvManager.getInstance().searchJapanVocabulary(mSearchWord, mSearchDateFirst, mSearchDateLast, mJvList);
			sortList();
	
			mDataChangedHandler.sendEmptyMessage(-1);
		};
	}

}
