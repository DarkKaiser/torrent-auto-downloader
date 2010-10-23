package kr.co.darkkaiser.jv.list;

import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import kr.co.darkkaiser.jv.JapanVocabulary;
import kr.co.darkkaiser.jv.JvDefines;
import kr.co.darkkaiser.jv.JvManager;
import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.detail.JvDetailActivity;
import kr.co.darkkaiser.jv.list.JvListAdapter;
import kr.co.darkkaiser.jv.list.JvListSortMethod;

import android.R.layout;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
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

	private static final int MSG_UPDATE_LIST_DATA_COMPLETED = 1;

	private Thread mSearchThread = null;
	private ProgressDialog mProgressDialog = null;

	private SharedPreferences mPreferences = null;

	private JvListAdapter mJvListAdapter = null;
	private ArrayList<JapanVocabulary> mJvList = null;

	private JvListSortMethod mJvListSortMethod = JvListSortMethod.REGISTRATION_DATE_DOWN;

	// @@@@@ JLPT(단어 디비에 있음), 품사, 암기완료/미완료/전체, 암기대상/비대상/전체, 결과내 검색??
	private String mPartsOfSpeech = null;
	private String mSearchWord = null;
	private long mSearchDateFirst = 0;
	private long mSearchDateLast = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jv_list);

        // 환경설정값을 읽어들인다.
		mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		mJvListSortMethod = JvListSortMethod.valueOf(mPreferences.getString(JvDefines.JV_SPN_LIST_SORT_METHOD, JvListSortMethod.REGISTRATION_DATE_DOWN.name()));

        // 리스트를 초기화한다.
        mJvList = new ArrayList<JapanVocabulary>();
        mJvListAdapter = new JvListAdapter(this, R.layout.jv_listitem, mJvList);
        setListAdapter(mJvListAdapter);

        // 검색 조건에 따라 단어를 검색한다.
        searchVocabulary(""); // @@@@@ 이전의 검색 조건을 넘겨야 함
        
//				//@@@@@ 임시
//				String []choiceItems = 
//			       {"중학 과정", "고교 과정", "토익 과정", "토플 과정", "공무원/편입 과정", "사용자 파일내"};
//			       
//			       boolean []selecteditems = {false, false, false, false, false, false };
//			       
//			       new AlertDialog.Builder(JvListActivity.this).setTitle("검색주제를 선택해주세요")
//			          .setMultiChoiceItems(choiceItems, selecteditems, new OnMultiChoiceClickListener() {
//			     
//			     @Override
//			     public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
//			           }
//			    })
//			          .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//			 
//			    public void onClick(DialogInterface arg0, int arg1) {
////			    	for (boolean state : selecteditems) {
//			    		//String.valueOf(state)
////			    	}
//			    }
//			   })
//			   .show();
	}

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
//		case R.id.jvlm_search:
//			final LinearLayout linear = (LinearLayout)View.inflate(JvListActivity.this, R.layout.jv_list_search, null);
//
//			CheckBox cboAllDataSearch = (CheckBox)linear.findViewById(R.id.all_data_search);
//			cboAllDataSearch.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					CheckBox cboAllDataSearch = (CheckBox)v;
//					Button btnSearchDateFirst = (Button)linear.findViewById(R.id.SearchDateFirst);
//					Button btnSearchDateLast = (Button)linear.findViewById(R.id.SearchDateLast);
//					btnSearchDateFirst.setEnabled(!cboAllDataSearch.isChecked());
//					btnSearchDateLast.setEnabled(!cboAllDataSearch.isChecked());
//				}
//			});
//
//	        // 현재의 날짜를 구하여 컨트롤에 반영한다.
//	        Calendar currentDate = Calendar.getInstance();
//			Button btnSearchDateFirst = (Button)linear.findViewById(R.id.SearchDateFirst);
//			Button btnSearchDateLast = (Button)linear.findViewById(R.id.SearchDateLast);
//			btnSearchDateFirst.setText(String.format("%04d/%02d/%02d", currentDate.get(Calendar.YEAR),
//					currentDate.get(Calendar.MONTH) + 1,
//					currentDate.get(Calendar.DATE)));
//
//			btnSearchDateLast.setText(String.format("%04d/%02d/%02d", currentDate.get(Calendar.YEAR),
//					currentDate.get(Calendar.MONTH) + 1,
//					currentDate.get(Calendar.DATE)));
//
//			btnSearchDateFirst.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Button btnSearchDateFirst = (Button)v;
//		    		String searchDateString = btnSearchDateFirst.getText().toString().replace("/", "");
//		        	new DatePickerDialog(JvListActivity.this,
//		        			new DatePickerDialog.OnDateSetListener() {
//								@Override
//								public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//									Button searchDateFirst = (Button)linear.findViewById(R.id.SearchDateFirst);				
//									searchDateFirst.setText(String.format("%04d/%02d/%02d", year, monthOfYear + 1, dayOfMonth));
//								}
//							},
//		        			Integer.parseInt(searchDateString.substring(0, 4)),
//		        			Integer.parseInt(searchDateString.substring(4, 6)) - 1,
//		        			Integer.parseInt(searchDateString.substring(6, 8))).show();
//				}
//			});
//			
//			btnSearchDateLast.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Button btnSearchDateLast = (Button)v;
//		    		String searchDateString = btnSearchDateLast.getText().toString().replace("/", "");
//		        	new DatePickerDialog(JvListActivity.this,
//		        			new DatePickerDialog.OnDateSetListener() {
//								@Override
//								public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//									Button searchDateLast = (Button)linear.findViewById(R.id.SearchDateLast);
//									searchDateLast.setText(String.format("%04d/%02d/%02d", year, monthOfYear + 1, dayOfMonth));
//								}
//							},
//		        			Integer.parseInt(searchDateString.substring(0, 4)),
//		        			Integer.parseInt(searchDateString.substring(4, 6)) - 1,
//		        			Integer.parseInt(searchDateString.substring(6, 8))).show();
//				}
//			});
//			
//			new AlertDialog.Builder(JvListActivity.this)
//				.setTitle("검색")
//				.setView(linear)
//				.setPositiveButton("검색", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						EditText etSearchWord = (EditText)linear.findViewById(R.id.SearchWord);
//						String searchWord = etSearchWord.getText().toString().trim();
//						
//						CheckBox cboAllDataSearch = (CheckBox)linear.findViewById(R.id.all_data_search);
//						if (cboAllDataSearch.isChecked() == true) {
//							searchVocabulary(searchWord);
//						} else {
//							try {
//								Button btnSearchDateFirst = (Button)linear.findViewById(R.id.SearchDateFirst);
//								long searchDateFirst = new SimpleDateFormat("yyyy/MM/dd").parse(btnSearchDateFirst.getText().toString()).getTime();
//
//								Button btnSearchDateLast = (Button)linear.findViewById(R.id.SearchDateLast);
//								long searchDateLast = new SimpleDateFormat("yyyy/MM/dd").parse(btnSearchDateLast.getText().toString()).getTime();
//								
//								if (searchDateFirst > searchDateLast) {
//									long temp = searchDateFirst;
//									searchDateFirst = searchDateLast;
//									searchDateLast = temp;
//								}
//
//								searchDateLast += new SimpleDateFormat("HH:mm:ss").parse("23:59:59").getTime();
//								searchDateLast += 999/* 밀리초 */;
//								
//								searchVocabulary(searchWord, searchDateFirst, searchDateLast);
//							} catch (ParseException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//				})
//				.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.cancel();
//					}
//				})
//				.show();
//
//			return true;
		case R.id.jvlm_sort_vocabulary:
			startSortList(JvListSortMethod.VOCABULARY);
			return true;
		case R.id.jvlm_sort_vocabulary_gana:
			startSortList(JvListSortMethod.VOCABULARY_GANA);
			return true;
		case R.id.jvlm_sort_vocabulary_translation:
			startSortList(JvListSortMethod.VOCABULARY_TRANSLATION);
			return true;
		case R.id.jvlm_sort_registration_date_up:
			startSortList(JvListSortMethod.REGISTRATION_DATE_UP);
			return true;
		case R.id.jvlm_sort_registration_date_down:
			startSortList(JvListSortMethod.REGISTRATION_DATE_DOWN);
			return true;
		case R.id.jvlm_all_rememorize:				// 검색된 전체 단어 재암기
		case R.id.jvlm_all_memorize_completed:		// 검색된 전체 단어 암기 완료
		case R.id.jvlm_all_memorize_target:			// 검색된 전체 단어 암기 대상 만들기
		case R.id.jvlm_all_memorize_target_cancel:	// 검색된 전체 단어 암기 대상 해제
			// 데이터를 처리하는 도중에 프로그레스 대화상자를 보인다.
			mProgressDialog = ProgressDialog.show(this, null, "요청하신 작업을 처리 중 입니다.", true, false);

	   		new Thread() {
	   			
	   			private int mMenuItemId = 0;

	   			public Thread setMenuItemId(int menuItemId)
	   			{
	   				mMenuItemId = menuItemId;
	   				return this;
	   			}

				@Override
	   			public void run() {
					JapanVocabulary jv = null;
					ArrayList<Long> idxList = new ArrayList<Long>();

					if (mMenuItemId == R.id.jvlm_all_rememorize) {							// 검색된 전체 단어 재암기
						for (int index = 0; index < mJvList.size(); ++index) {
							jv = mJvList.get(index);
							if (jv.isMemorizeTarget() == false || jv.isMemorizeCompleted() == true)
								idxList.add(jv.getIdx());
						}
					} else if (mMenuItemId == R.id.jvlm_all_memorize_completed) {			// 검색된 전체 단어 암기 완료
						for (int index = 0; index < mJvList.size(); ++index) {
							jv = mJvList.get(index);
							if (jv.isMemorizeCompleted() == false)
								idxList.add(jv.getIdx());
						}
					} else if (mMenuItemId == R.id.jvlm_all_memorize_target) {				// 검색된 전체 단어 암기 대상 만들기
						for (int index = 0; index < mJvList.size(); ++index) {
							jv = mJvList.get(index);
							if (jv.isMemorizeTarget() == false)
								idxList.add(jv.getIdx());
						}
					} else if (mMenuItemId == R.id.jvlm_all_memorize_target_cancel) {		// 검색된 전체 단어 암기 대상 해제
						for (int index = 0; index < mJvList.size(); ++index) {
							jv = mJvList.get(index);
							if (jv.isMemorizeTarget() == true)
								idxList.add(jv.getIdx());
						}
					}

					JvManager.getInstance().updateMemorizeField(mMenuItemId, idxList);

					Message msg = Message.obtain();
					msg.what = MSG_UPDATE_LIST_DATA_COMPLETED;
					mDataChangedHandler.sendMessage(msg);
	   			};
	   		}
	   		.setMenuItemId(item.getItemId())
	   		.start();
	   		
			return true;
		}

		return false;
	}

	private void startSortList(JvListSortMethod jvListSortMethod) {
		if (mJvListSortMethod == jvListSortMethod)
			return;

		// 바뀐 정렬 방법을 저장한다.
		mJvListSortMethod = jvListSortMethod;
		mPreferences.edit().putString(JvDefines.JV_SPN_LIST_SORT_METHOD, mJvListSortMethod.name()).commit();

		assert mProgressDialog == null;

		// 정렬중에 프로그레스 대화상자를 보인다.
		mProgressDialog = ProgressDialog.show(this, null, "전체 리스트를 정렬중입니다.", true, false);

   		new Thread() {
			@Override
   			public void run() {
				// 리스트 데이터 정렬합니다. 
				sortList();

				Message msg = Message.obtain();
				msg.what = MSG_UPDATE_LIST_DATA_COMPLETED;
				mDataChangedHandler.sendMessage(msg);
   			};
   		}.start();
	}
	
	private void sortList() {
		switch (mJvListSortMethod) {
			case VOCABULARY:
				Collections.sort(mJvList, mJvVocabularyComparator);
				break;
			case VOCABULARY_GANA:
				Collections.sort(mJvList, mJvVocabularyGanaComparator);
				break;
			case VOCABULARY_TRANSLATION:
				Collections.sort(mJvList, mJvVocabularyTranslationComparator);
				break;
			case REGISTRATION_DATE_UP:
				Collections.sort(mJvList, mJvRegistrationDateUpComparator);
				break;
			case REGISTRATION_DATE_DOWN:
				Collections.sort(mJvList, mJvRegistrationDateDownComparator);
				break;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// 단어 상세페이지 호출
		Intent intent = new Intent(this, JvDetailActivity.class);
		intent.putExtra("idx", mJvList.get(position).getIdx());
		startActivity(intent);
	}

	private final static Comparator<JapanVocabulary> mJvVocabularyComparator = new Comparator<JapanVocabulary>() {
         private final Collator collator = Collator.getInstance();

         @Override
         public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
        	 return collator.compare(lhs.getVocabulary(), rhs.getVocabulary());
         }
	};

	private final static Comparator<JapanVocabulary> mJvVocabularyGanaComparator = new Comparator<JapanVocabulary>() {
        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
       	 	return collator.compare(lhs.getVocabularyGana(), rhs.getVocabularyGana());
        }
	};
	
	private final static Comparator<JapanVocabulary> mJvVocabularyTranslationComparator = new Comparator<JapanVocabulary>() {
        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
       	 	return collator.compare(lhs.getVocabularyTranslation(), rhs.getVocabularyTranslation());
        }
	};
	
	private final static Comparator<JapanVocabulary> mJvRegistrationDateUpComparator = new Comparator<JapanVocabulary>() {

        @Override
        public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
        	if (lhs.getRegistrationDate() > rhs.getRegistrationDate())
        		return 1;
        	else if (lhs.getRegistrationDate() < rhs.getRegistrationDate())
        		return -1;

        	return 0;
        }
	};

	private final static Comparator<JapanVocabulary> mJvRegistrationDateDownComparator = new Comparator<JapanVocabulary>() {

        @Override
        public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
        	if (lhs.getRegistrationDate() > rhs.getRegistrationDate())
        		return -1;
        	else if (lhs.getRegistrationDate() < rhs.getRegistrationDate())
        		return 1;

        	return 0;
        }
	};

	// @@@@@
	private Handler mDataChangedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == -1) {
				mJvListAdapter.notifyDataSetChanged();

				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				TextView tvSearchInfo = (TextView)findViewById(R.id.search_info);
				TextView tvVocabularyCount = (TextView)findViewById(R.id.vocabulary_count);

				tvSearchInfo.setVisibility(View.VISIBLE);
				tvVocabularyCount.setVisibility(View.VISIBLE);
				tvVocabularyCount.setText(String.format("단어:%d개", mJvList.size()));
				
				mSearchThread = null;
				mProgressDialog = null;
			} else if (msg.what == MSG_UPDATE_LIST_DATA_COMPLETED) {
				mJvListAdapter.notifyDataSetChanged();

				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				TextView tvSearchInfo = (TextView)findViewById(R.id.search_info);
				TextView tvVocabularyCount = (TextView)findViewById(R.id.vocabulary_count);

				tvSearchInfo.setVisibility(View.VISIBLE);
				tvVocabularyCount.setVisibility(View.VISIBLE);
				tvVocabularyCount.setText(String.format("단어:%d개", mJvList.size()));

				mSearchThread = null;
				mProgressDialog = null;
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
