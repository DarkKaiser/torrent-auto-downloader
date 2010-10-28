package kr.co.darkkaiser.jv.list;

import java.text.Collator;
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
import kr.co.darkkaiser.jv.option.OptionActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class JvListActivity extends ListActivity implements OnClickListener {

	// 인텐트로 넘겨 줄 액티비티 결과 값, 이 값들은 서로 배타적이어야 함.
	public static final int ACTIVITY_RESULT_DATA_CHANGED = 1;
	public static final int ACTIVITY_RESULT_PREFERENCE_CHANGED = 2;

	public static final int MSG_CHANGED_LIST_DATA = 1;
	public static final int MSG_COMPLETED_LIST_DATA_UPDATE = 2;

	private ProgressDialog mProgressDialog = null;
	private SharedPreferences mPreferences = null;

	private JvListAdapter mJvListAdapter = null;
	private ArrayList<JapanVocabulary> mJvListData = null;
	private JvListSortMethod mJvListSortMethod = JvListSortMethod.REGISTRATION_DATE_DOWN;

	private Thread mJvListSearchThread = null;
	private JvListSearchCondition mJvListSearchCondition = null;

	private int mActivityResultCode = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jv_list);

		// 이전에 저장해 둔 환경설정 값들을 읽어들인다.
		mPreferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		mJvListSortMethod = JvListSortMethod.valueOf(mPreferences.getString(JvDefines.JV_SPN_LIST_SORT_METHOD, JvListSortMethod.REGISTRATION_DATE_DOWN.name()));
		mJvListSearchCondition = new JvListSearchCondition(this, mPreferences);

		// 단어 리스트를 초기화한다.
		mJvListData = new ArrayList<JapanVocabulary>();
		mJvListAdapter = new JvListAdapter(this, R.layout.jv_listitem,
				mJvListDataChangedHandler, mJvListData);
		setListAdapter(mJvListAdapter);

		//
		// 검색과 관련된 컨트롤들을 초기화합니다.
		//

		// 검색어 검색 조건
		EditText scSearchWordEditText = (EditText) findViewById(R.id.sc_search_word);
		scSearchWordEditText.setText(mJvListSearchCondition.getSearchWord());

		// 암기 대상 검색 조건
		ArrayAdapter<String> scMemorizeTargetAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.sc_memorize_target));
		scMemorizeTargetAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);

		Spinner scMemorizeTargetSpinner = (Spinner) findViewById(R.id.sc_memorize_target);
		scMemorizeTargetSpinner.setAdapter(scMemorizeTargetAdapter);
		scMemorizeTargetSpinner.setPrompt("검색 조건");
		scMemorizeTargetSpinner.setSelection(mJvListSearchCondition.getMemorizeTargetPosition());

		// 암기 완료 검색 조건
		ArrayAdapter<String> scMemorizeCompletedAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sc_memorize_completed));
		scMemorizeCompletedAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);

		Spinner scMemorizeCompletedSpinner = (Spinner)findViewById(R.id.sc_memorize_completed);
		scMemorizeCompletedSpinner.setAdapter(scMemorizeCompletedAdapter);
		scMemorizeCompletedSpinner.setPrompt("검색 조건");
		scMemorizeCompletedSpinner.setSelection(mJvListSearchCondition.getMemorizeCompletedPosition());

		// 단어 등록일 검색 조건
		Button btnLastSearchDate = (Button) findViewById(R.id.sc_last_search_date);
		Button btnFirstSearchDate = (Button) findViewById(R.id.sc_first_search_date);
		CheckBox cboAllRegDateSearch = (CheckBox) findViewById(R.id.sc_all_reg_date_search);

		if (mJvListSearchCondition.isAllRegDateSearch() == true) {
			cboAllRegDateSearch.setChecked(true);

			Calendar currentDate = Calendar.getInstance();
			btnLastSearchDate.setText(String.format("%04d/%02d/%02d",
					currentDate.get(Calendar.YEAR), currentDate
							.get(Calendar.MONTH) + 1, currentDate
							.get(Calendar.DATE)));
			currentDate.add(Calendar.DAY_OF_MONTH, -7);
			btnFirstSearchDate.setText(String.format("%04d/%02d/%02d",
					currentDate.get(Calendar.YEAR), currentDate
							.get(Calendar.MONTH) + 1, currentDate
							.get(Calendar.DATE)));
		} else {
			cboAllRegDateSearch.setChecked(false);

			btnFirstSearchDate.setText(mJvListSearchCondition
					.getFirstSearchDate());
			btnLastSearchDate.setText(mJvListSearchCondition
					.getLastSearchDate());

			btnLastSearchDate.setEnabled(true);
			btnFirstSearchDate.setEnabled(true);
		}

		btnLastSearchDate.setOnClickListener(this);
		btnFirstSearchDate.setOnClickListener(this);
		cboAllRegDateSearch.setOnClickListener(this);

		// JLPT 급수 검색 조건
		updateJLPTLevelButtonText();
		findViewById(R.id.sc_jlpt_level).setOnClickListener(this);

		// 기타
		findViewById(R.id.search_start).setOnClickListener(this);
		findViewById(R.id.search_cancel).setOnClickListener(this);

		//
		// 최근의 검색 조건을 이용하여 검색을 수행한다.
		//
		searchVocabulary();
	}

	private void updateJLPTLevelButtonText() {
		String[] items = getResources().getStringArray(R.array.sc_jlpt_simple_level_list);
		boolean[] checkedItems = mJvListSearchCondition.getCheckedJLPTLevelArray();
		assert items.length == checkedItems.length;

		StringBuilder sb = new StringBuilder();
		for (int index = 0; index < checkedItems.length; ++index) {
			if (checkedItems[index] == true) {
				if (sb.length() > 0)
					sb.append(", ");

				sb.append(items[index]);
			}
		}
		
		if (sb.length() == 0)
			sb.append("<선택 항목 없음>");

		Button scJLPTLevelButton = (Button)findViewById(R.id.sc_jlpt_level);
		scJLPTLevelButton.setText(sb.toString());
	}

	@Override
	public void onBackPressed() {
		SlidingDrawer searchSlidingDrawer = (SlidingDrawer) findViewById(R.id.search_sliding_drawer);
		if (searchSlidingDrawer.isOpened() == true) {
			searchSlidingDrawer.animateClose();
			return;
		}

		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.jv_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
		case R.id.jvlm_all_rememorize: // 검색된 전체 단어 재암기
		case R.id.jvlm_all_memorize_completed: // 검색된 전체 단어 암기 완료
		case R.id.jvlm_all_memorize_target: // 검색된 전체 단어 암기 대상 만들기
		case R.id.jvlm_all_memorize_target_cancel: // 검색된 전체 단어 암기 대상 해제
			// 호출자 액티비티에게 데이터가 변경되었음을 알리도록 한다.
			mActivityResultCode |= ACTIVITY_RESULT_DATA_CHANGED;
			setResult(mActivityResultCode);
			
			// 데이터를 처리하는 도중에 프로그레스 대화상자를 보인다.
			mProgressDialog = ProgressDialog.show(this, null,
					"요청하신 작업을 처리 중입니다.", true, false);

			new Thread() {

				private int mMenuItemId = 0;

				public Thread setMenuItemId(int menuItemId) {
					mMenuItemId = menuItemId;
					return this;
				}

				@Override
				public void run() {
					JapanVocabulary jv = null;
					ArrayList<Long> idxList = new ArrayList<Long>();

					if (mMenuItemId == R.id.jvlm_all_rememorize) { // 검색된 전체 단어
																	// 재암기
						for (int index = 0; index < mJvListData.size(); ++index) {
							jv = mJvListData.get(index);
							if (jv.isMemorizeTarget() == false
									|| jv.isMemorizeCompleted() == true)
								idxList.add(jv.getIdx());
						}
					} else if (mMenuItemId == R.id.jvlm_all_memorize_completed) { // 검색된
																					// 전체
																					// 단어
																					// 암기
																					// 완료
						for (int index = 0; index < mJvListData.size(); ++index) {
							jv = mJvListData.get(index);
							if (jv.isMemorizeCompleted() == false)
								idxList.add(jv.getIdx());
						}
					} else if (mMenuItemId == R.id.jvlm_all_memorize_target) { // 검색된
																				// 전체
																				// 단어
																				// 암기
																				// 대상
																				// 만들기
						for (int index = 0; index < mJvListData.size(); ++index) {
							jv = mJvListData.get(index);
							if (jv.isMemorizeTarget() == false)
								idxList.add(jv.getIdx());
						}
					} else if (mMenuItemId == R.id.jvlm_all_memorize_target_cancel) { // 검색된
																						// 전체
																						// 단어
																						// 암기
																						// 대상
																						// 해제
						for (int index = 0; index < mJvListData.size(); ++index) {
							jv = mJvListData.get(index);
							if (jv.isMemorizeTarget() == true)
								idxList.add(jv.getIdx());
						}
					}

					JvManager.getInstance().updateMemorizeField(mMenuItemId,
							idxList);

					Message msg = Message.obtain();
					msg.what = MSG_COMPLETED_LIST_DATA_UPDATE;
					mJvListDataChangedHandler.sendMessage(msg);
				};
			}.setMenuItemId(item.getItemId()).start();

			return true;

		case R.id.jvm_preferences:
			// 설정 페이지를 띄운다.
			startActivityForResult(new Intent(this, OptionActivity.class), R.id.jvm_preferences);
			mActivityResultCode |= ACTIVITY_RESULT_PREFERENCE_CHANGED;
			setResult(mActivityResultCode);

			return true;
		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == R.id.jvm_preferences) {
			// 수행 작업 없음
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startSortList(JvListSortMethod jvListSortMethod) {
		if (mJvListSortMethod == jvListSortMethod)
			return;

		// 바뀐 정렬 방법을 저장한다.
		mJvListSortMethod = jvListSortMethod;
		mPreferences.edit().putString(JvDefines.JV_SPN_LIST_SORT_METHOD,
				mJvListSortMethod.name()).commit();

		assert mProgressDialog == null;

		// 정렬중에 프로그레스 대화상자를 보인다.
		mProgressDialog = ProgressDialog.show(this, null, "전체 리스트를 정렬중입니다.",
				true, false);

		new Thread() {
			@Override
			public void run() {
				// 리스트 데이터 정렬합니다.
				sortList();

				Message msg = Message.obtain();
				msg.what = MSG_COMPLETED_LIST_DATA_UPDATE;
				mJvListDataChangedHandler.sendMessage(msg);
			};
		}.start();
	}

	private void sortList() {
		switch (mJvListSortMethod) {
		case VOCABULARY:
			Collections.sort(mJvListData, mJvVocabularyComparator);
			break;
		case VOCABULARY_GANA:
			Collections.sort(mJvListData, mJvVocabularyGanaComparator);
			break;
		case VOCABULARY_TRANSLATION:
			Collections.sort(mJvListData, mJvVocabularyTranslationComparator);
			break;
		case REGISTRATION_DATE_UP:
			Collections.sort(mJvListData, mJvRegistrationDateUpComparator);
			break;
		case REGISTRATION_DATE_DOWN:
			Collections.sort(mJvListData, mJvRegistrationDateDownComparator);
			break;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// 단어 상세페이지 호출
		Intent intent = new Intent(this, JvDetailActivity.class);
		intent.putExtra("idx", mJvListData.get(position).getIdx());
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
			return collator.compare(lhs.getVocabularyGana(), rhs
					.getVocabularyGana());
		}
	};

	private final static Comparator<JapanVocabulary> mJvVocabularyTranslationComparator = new Comparator<JapanVocabulary>() {
		private final Collator collator = Collator.getInstance();

		@Override
		public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
			return collator.compare(lhs.getVocabularyTranslation(), rhs
					.getVocabularyTranslation());
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

	private Handler mJvListDataChangedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_COMPLETED_LIST_DATA_UPDATE) {
				updateVocabularyInfo();
				mJvListAdapter.notifyDataSetChanged();

				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				findViewById(R.id.vocabulary_info_area).setVisibility(
						View.VISIBLE);

				mProgressDialog = null;
				mJvListSearchThread = null;
			} else if (msg.what == MSG_CHANGED_LIST_DATA) {
				// 호출자 액티비티에게 데이터가 변경되었음을 알리도록 한다.
				mActivityResultCode |= ACTIVITY_RESULT_DATA_CHANGED;
				setResult(mActivityResultCode);

				updateVocabularyInfo();
				mJvListAdapter.notifyDataSetChanged();
			}
		};
	};

	private void searchVocabulary() {
		// 단어 검색이 끝날때까지 진행 대화상자를 보인다.
		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialog.show(this, null, "단어를 검색 중입니다.",
					true, true);
			mProgressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					if (mJvListSearchThread != null) {
						mJvListSearchThread.interrupt();
					}

					// 검색을 취소하였으므로 단어를 모두 제거한다.
					mJvListData.clear();

					Toast.makeText(JvListActivity.this, "단어 검색이 취소되었습니다",
							Toast.LENGTH_SHORT).show();

					Message msg = Message.obtain();
					msg.what = MSG_COMPLETED_LIST_DATA_UPDATE;
					mJvListDataChangedHandler.sendMessage(msg);
				}
			});

		}

		// 검색이 시작되기 전 화면을 정리한다.
		mJvListData.clear();
		mJvListAdapter.notifyDataSetChanged();
		findViewById(R.id.vocabulary_info_area).setVisibility(View.INVISIBLE);

		mJvListSearchThread = new JvListSearchThread(mJvListSearchCondition);
		mJvListSearchThread.start();
	}

	public class JvListSearchThread extends Thread {

		private JvListSearchCondition mJvListSearchCondition = null;

		public JvListSearchThread(JvListSearchCondition jvListSearchCondition) {
			assert jvListSearchCondition != null;
			mJvListSearchCondition = jvListSearchCondition;
		}

		@Override
		public void run() {
			mJvListData.clear();
			JvManager.getInstance().searchVocabulary(mJvListSearchCondition,
					mJvListData);
			sortList();

			Message msg = Message.obtain();
			msg.what = MSG_COMPLETED_LIST_DATA_UPDATE;
			mJvListDataChangedHandler.sendMessage(msg);
		};

	}

	private void updateVocabularyInfo() {
		int memorizeTargetCount = 0;
		int memorizeCompletedCount = 0;

		for (JapanVocabulary jv : mJvListData) {
			if (jv.isMemorizeTarget() == true)
				++memorizeTargetCount;
			if (jv.isMemorizeCompleted() == true)
				++memorizeCompletedCount;
		}

		TextView tvAllVocabularyCount = (TextView) findViewById(R.id.all_vocabulary_count);
		TextView tvMemorizeTargetCount = (TextView) findViewById(R.id.memorize_target_count);
		TextView tvMemorizeCompletedCount = (TextView) findViewById(R.id.memorize_completed_count);

		tvAllVocabularyCount.setText(String.format("%d개", mJvListData.size()));
		tvMemorizeTargetCount
				.setText(String.format("%d개", memorizeTargetCount));
		tvMemorizeCompletedCount.setText(String.format("%d개",
				memorizeCompletedCount));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sc_jlpt_level: {
			boolean[] checkedItems = mJvListSearchCondition.getCheckedJLPTLevelArray();

			new AlertDialog.Builder(JvListActivity.this).setTitle("검색 조건")
					.setMultiChoiceItems(R.array.sc_jlpt_level_list, checkedItems, new OnMultiChoiceClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int item, boolean isChecked) {
									mJvListSearchCondition.setCheckedJLPTLevel(item, isChecked);
								}
							})
					.setPositiveButton("확인",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									mJvListSearchCondition.commit();
									updateJLPTLevelButtonText();
								}
							})
					.show();
		}
			break;

		case R.id.sc_first_search_date: {
			Button btnSearchDateFirst = (Button) v;
			String searchDateString = btnSearchDateFirst.getText().toString()
					.replace("/", "");
			new DatePickerDialog(
					this,
					new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							Button searchDateFirst = (Button) findViewById(R.id.sc_first_search_date);
							searchDateFirst.setText(String.format(
									"%04d/%02d/%02d", year, monthOfYear + 1,
									dayOfMonth));

							// 검색 종료일보다 최근 날짜이면 검색 종료일도 변경한다.
							Calendar calSearchDateFirst = Calendar
									.getInstance();
							calSearchDateFirst.set(year, monthOfYear,
									dayOfMonth);

							Button searchDateLast = (Button) findViewById(R.id.sc_last_search_date);
							String searchDateLastString = searchDateLast
									.getText().toString().replace("/", "");

							Calendar calSearchDateLast = Calendar.getInstance();
							calSearchDateLast.set(Integer
									.parseInt(searchDateLastString.substring(0,
											4)), Integer
									.parseInt(searchDateLastString.substring(4,
											6)) - 1, Integer
									.parseInt(searchDateLastString.substring(6,
											8)));

							if (calSearchDateFirst.after(calSearchDateLast) == true)
								searchDateLast.setText(searchDateFirst
										.getText());
						}
					}, Integer.parseInt(searchDateString.substring(0, 4)),
					Integer.parseInt(searchDateString.substring(4, 6)) - 1,
					Integer.parseInt(searchDateString.substring(6, 8))).show();
		}
			break;

		case R.id.sc_last_search_date: {
			Button btnSearchDateLast = (Button) v;
			String searchDateString = btnSearchDateLast.getText().toString()
					.replace("/", "");
			new DatePickerDialog(
					JvListActivity.this,
					new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							Button searchDateLast = (Button) findViewById(R.id.sc_last_search_date);
							searchDateLast.setText(String.format(
									"%04d/%02d/%02d", year, monthOfYear + 1,
									dayOfMonth));

							// 검색 시작일보다 이전 날짜이면 검색 시작일도 변경한다.
							Calendar calSearchDateLast = Calendar.getInstance();
							calSearchDateLast
									.set(year, monthOfYear, dayOfMonth);

							Button searchDateFirst = (Button) findViewById(R.id.sc_first_search_date);
							String searchDateFirstString = searchDateFirst
									.getText().toString().replace("/", "");

							Calendar calSearchDateFirst = Calendar
									.getInstance();
							calSearchDateFirst.set(Integer
									.parseInt(searchDateFirstString.substring(
											0, 4)), Integer
									.parseInt(searchDateFirstString.substring(
											4, 6)) - 1, Integer
									.parseInt(searchDateFirstString.substring(
											6, 8)));

							if (calSearchDateLast.before(calSearchDateFirst) == true)
								searchDateFirst.setText(searchDateLast
										.getText());
						}
					}, Integer.parseInt(searchDateString.substring(0, 4)),
					Integer.parseInt(searchDateString.substring(4, 6)) - 1,
					Integer.parseInt(searchDateString.substring(6, 8))).show();
		}
			break;

		case R.id.sc_all_reg_date_search: {
			CheckBox cboAllRegDateSearch = (CheckBox) v;
			Button btnSearchDateFirst = (Button) findViewById(R.id.sc_first_search_date);
			Button btnSearchDateLast = (Button) findViewById(R.id.sc_last_search_date);
			btnSearchDateFirst.setEnabled(!cboAllRegDateSearch.isChecked());
			btnSearchDateLast.setEnabled(!cboAllRegDateSearch.isChecked());
		}
			break;

		case R.id.search_start: {
			SlidingDrawer searchSlidingDrawer = (SlidingDrawer) findViewById(R.id.search_sliding_drawer);
			searchSlidingDrawer.animateClose();

			// 설정된 검색 조건들을 저장합니다.
			EditText scSearchWordEditText = (EditText) findViewById(R.id.sc_search_word);
			Spinner scMemorizeTargetSpinner = (Spinner) findViewById(R.id.sc_memorize_target);
			Spinner scMemorizeCompletedSpinner = (Spinner) findViewById(R.id.sc_memorize_completed);
			CheckBox scAllRegDateSearchCheckBox = (CheckBox) findViewById(R.id.sc_all_reg_date_search);
			Button scSearchDateFirstButton = (Button) findViewById(R.id.sc_first_search_date);
			Button scSearchDateLastButton = (Button) findViewById(R.id.sc_last_search_date);

			mJvListSearchCondition.setSearchWord(scSearchWordEditText.getText()
					.toString().trim());
			mJvListSearchCondition
					.setMemorizeTargetPosition(scMemorizeTargetSpinner
							.getSelectedItemPosition());
			mJvListSearchCondition
					.setMemorizeCompletedPosition(scMemorizeCompletedSpinner
							.getSelectedItemPosition());
			mJvListSearchCondition
					.setAllRegDateSearch(scAllRegDateSearchCheckBox.isChecked());
			mJvListSearchCondition.setSearchDateRange(scSearchDateFirstButton
					.getText().toString(), scSearchDateLastButton.getText()
					.toString());
			mJvListSearchCondition.commit();

			// 설정된 검색 조건을 이용하여 단어를 검색합니다.
			searchVocabulary();
		}
			break;

		case R.id.search_cancel:
			SlidingDrawer searchSlidingDrawer = (SlidingDrawer) findViewById(R.id.search_sliding_drawer);
			searchSlidingDrawer.animateClose();
			break;
		}
	}

}
