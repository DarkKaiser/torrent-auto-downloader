package kr.co.darkkaiser.jv.view.list;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.common.Constants;
import kr.co.darkkaiser.jv.view.detail.DetailActivity;
import kr.co.darkkaiser.jv.view.settings.SettingsActivity;
import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;
import kr.co.darkkaiser.jv.vocabulary.data.VocabularyComparator;
import kr.co.darkkaiser.jv.vocabulary.data.VocabularyManager;
import kr.co.darkkaiser.jv.vocabulary.list.internal.SearchResultVocabularyList;

public class SearchListActivity extends ActionBarActivity implements OnClickListener, OnScrollListener {

	// 호출자 인텐트로 넘겨 줄 액티비티 결과 값, 이 값들은 서로 배타적이어야 함.
	public static final int ACTIVITY_RESULT_DATA_CHANGED = 1;
	public static final int ACTIVITY_RESULT_PREFERENCE_CHANGED = 2;

	public static final int MSG_CHANGED_LIST_DATA = 1;
	public static final int MSG_COMPLETED_LIST_DATA_UPDATE = 2;

	// 리스트뷰의 스크롤이 멈추었을 때 Thumb를 숨기기 위한 메시지
	private static final int MSG_LISTVIEW_SCROLLBAR_THUMB_HIDE = 1;

	private WindowManager mWindowManager = null;
	private SharedPreferences mPreferences = null;
	private ProgressDialog mProgressDialog = null;

	private SearchListAdapter mJvListAdapter = null;
	private ArrayList<Vocabulary> mVocabularyListData = null;// @@@@@ SearchResultVocabularyList로 변경하는건???
	private SearchListSortMethod mJvListSortMethod = SearchListSortMethod.VOCABULARY;

	private Thread mJvListSearchThread = null;
	private SearchListCondition mJvListSearchCondition = null;

	private ScrollBarThumb mScrollThumb = null;
	private boolean mUseModeScrollBarThumb = false;
	private boolean mVisibleScrollBarThumb = false;
	private WindowManager.LayoutParams mScrollBarThumbLayout = null;

	private int mActivityResultCode = 0;

    private ListView mListView = null;

    // @@@@@
    protected ListView getListView() {
        if (mListView == null)
            mListView = (ListView)findViewById(android.R.id.list);

        return mListView;
    }

    // @@@@@
    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    // @@@@@
    protected ListAdapter getListAdapter() {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter)adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
    }

    @Override
    // @@@@@
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vocabulary_search_list);

		mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		assert mWindowManager != null;

		// 컨텍스트 메뉴를 등록한다.
		registerForContextMenu(getListView());

		// 이전에 저장해 둔 환경설정 값들을 읽어들인다.
		mPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		mJvListSortMethod = SearchListSortMethod.valueOf(mPreferences.getString(Constants.JV_SPN_LIST_SORT_METHOD, SearchListSortMethod.VOCABULARY.name()));
		mJvListSearchCondition = new SearchListCondition(this, mPreferences);

		// 단어 리스트를 초기화한다.
		mVocabularyListData = new ArrayList<Vocabulary>();
		mJvListAdapter = new SearchListAdapter(this, R.layout.activity_vocabulary_search_listitem, mVocabularyDataChangedHandler, mVocabularyListData);
		setListAdapter(mJvListAdapter);
		
		//
		// Thumb 관련 객체를 초기화합니다.
		//
		assert getListView() != null;

		mScrollThumb = new ScrollBarThumb(this, getListView());
		mScrollThumb.setVisibility(View.INVISIBLE);

		mScrollBarThumbLayout = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		mScrollBarThumbLayout.gravity = Gravity.TOP;
		mScrollBarThumbLayout.x = mScrollThumb.getScrollBarThumbLayoutX();
		mScrollBarThumbLayout.y = 0;

		mWindowManager.addView(mScrollThumb, mScrollBarThumbLayout);

		getListView().setOnScrollListener(this);

		//
		// 검색과 관련된 컨트롤들을 초기화합니다.
		//

		// 검색어 검색 조건
		EditText scSearchWordEditText = (EditText)findViewById(R.id.sc_search_word);
		scSearchWordEditText.setText(mJvListSearchCondition.getSearchWord());

		// 암기 대상 검색 조건
		ArrayAdapter<String> scMemorizeTargetAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sc_memorize_target));
		scMemorizeTargetAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);

		Spinner scMemorizeTargetSpinner = (Spinner)findViewById(R.id.sc_memorize_target);
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

    // @@@@@
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
			sb.append("전체 검색\n<선택 항목 없음>");

		Button scJLPTLevelButton = (Button)findViewById(R.id.sc_jlpt_level);
		scJLPTLevelButton.setText(sb.toString());
	}

	@Override
    // @@@@@
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_vocabulary_search_list, menu);

        // @@@@@ 정렬메뉴 선택항목 선택된 상태로 나타나도록 하기

		return true;
	}

	@Override
    // @@@@@
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.avsl_sort_vocabulary_hanja:
			startSortList(SearchListSortMethod.VOCABULARY);
			return true;
		case R.id.avsl_sort_vocabulary_gana:
			startSortList(SearchListSortMethod.VOCABULARY_GANA);
			return true;
		case R.id.avsl_sort_vocabulary_translation:
			startSortList(SearchListSortMethod.VOCABULARY_TRANSLATION);
			return true;
		case R.id.avsl_search_result_vocabulary_rememorize_all: 				// 검색된 전체 단어 재암기
		case R.id.avsl_search_result_vocabulary_memorize_completed_all: 		// 검색된 전체 단어 암기 완료
		case R.id.avsl_search_result_vocabulary_memorize_target_all: 		// 검색된 전체 단어 암기 대상 만들기
		case R.id.avsl_search_result_vocabulary_memorize_target_cancel_all: 	// 검색된 전체 단어 암기 대상 해제
			// 호출자 액티비티에게 데이터가 변경되었음을 알리도록 값을 설정한다.
			mActivityResultCode |= ACTIVITY_RESULT_DATA_CHANGED;
			setResult(mActivityResultCode);

			final int itemId = item.getItemId();
			if (item.getItemId() == R.id.avsl_search_result_vocabulary_memorize_target_all) {
				new AlertDialog.Builder(this)
					.setTitle("암기 대상 상태로 만들기")
					.setMessage("검색 결과에 포함되지 않은 단어들은 암기 대상 상태를 해제하시겠습니까?\n\n(예)를 누르시면 검색된 단어는 암기 대상 상태로, 검색 결과에 포함되지 않은 단어들은 암기 대상 상태를 해제합니다.\n\n(아니오)를 누르시면 검색된 단어만 암기 대상 상태로 만듭니다.")
					.setPositiveButton("예", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							memorizeSetupVocabulary(itemId, true);
						}
					})
					.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							memorizeSetupVocabulary(itemId, false);
						}
					})
					.show();
			} else {
				memorizeSetupVocabulary(itemId, false);
			}

			return true;

		case R.id.avsl_open_settings_activity:
			// 설정 페이지를 띄운다.
			startActivityForResult(new Intent(this, SettingsActivity.class), R.id.avsl_open_settings_activity);
			mActivityResultCode |= ACTIVITY_RESULT_PREFERENCE_CHANGED;
			setResult(mActivityResultCode);

			return true;
		}

		return false;
	}

    // @@@@@
    private void memorizeSetupVocabulary(int menuId, boolean notSearchVocabularyTargetCancel) {
		// 데이터를 처리하는 도중에 프로그레스 대화상자를 보인다.
		mProgressDialog = ProgressDialog.show(this, null, "요청하신 작업을 처리 중입니다.", true, false);

		new Thread() {

			private int mMenuItemId;
			private boolean mNotSearchVocabularyTargetCancel = false;

			public Thread setValues(int menuItemId, boolean notSearchVocabularyTargetCancel) {
				mMenuItemId = menuItemId;
				mNotSearchVocabularyTargetCancel = notSearchVocabularyTargetCancel;
				return this;
			}
			
			@Override
			public void run() {
				Vocabulary jpVocabulary = null;
				ArrayList<Long> idxList = new ArrayList<Long>();

				synchronized (mVocabularyListData) {
					if (mMenuItemId == R.id.avsl_search_result_vocabulary_rememorize_all) { 						// 검색된 전체 단어 재암기
						for (int index = 0; index < mVocabularyListData.size(); ++index) {
							jpVocabulary = mVocabularyListData.get(index);
							if (jpVocabulary.isMemorizeTarget() == false || jpVocabulary.isMemorizeCompleted() == true)
								idxList.add(jpVocabulary.getIdx());
						}
					} else if (mMenuItemId == R.id.avsl_search_result_vocabulary_memorize_completed_all) { 		// 검색된 전체 단어 암기 완료
						for (int index = 0; index < mVocabularyListData.size(); ++index) {
							jpVocabulary = mVocabularyListData.get(index);
							if (jpVocabulary.isMemorizeCompleted() == false)
								idxList.add(jpVocabulary.getIdx());
						}
					} else if (mMenuItemId == R.id.avsl_search_result_vocabulary_memorize_target_all) { 			// 검색된 전체 단어 암기 대상 만들기
						if (mNotSearchVocabularyTargetCancel == true) {
							for (int index = 0; index < mVocabularyListData.size(); ++index) {
								idxList.add(mVocabularyListData.get(index).getIdx());
							}							
						} else {
							for (int index = 0; index < mVocabularyListData.size(); ++index) {
								jpVocabulary = mVocabularyListData.get(index);
								if (jpVocabulary.isMemorizeTarget() == false)
									idxList.add(jpVocabulary.getIdx());
							}							
						}
					} else if (mMenuItemId == R.id.avsl_search_result_vocabulary_memorize_target_cancel_all) { 	// 검색된 전체 단어 암기 대상 해제
						for (int index = 0; index < mVocabularyListData.size(); ++index) {
							jpVocabulary = mVocabularyListData.get(index);
							if (jpVocabulary.isMemorizeTarget() == true)
								idxList.add(jpVocabulary.getIdx());
						}
					}

					VocabularyManager.getInstance().updateMemorizeField(mMenuItemId, mNotSearchVocabularyTargetCancel, idxList);
				}

				Message msg = Message.obtain();
				msg.what = MSG_COMPLETED_LIST_DATA_UPDATE;
				mVocabularyDataChangedHandler.sendMessage(msg);
			};
		}
		.setValues(menuId, notSearchVocabularyTargetCancel)
		.start();		
	}

	@Override
    // @@@@@
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == R.id.avsl_open_settings_activity) {
			// 수행 작업 없음
		} else if (requestCode == R.id.avd_vocabulary_detail_info) {
			DetailActivity.setSeekVocabularyList(null);

			// 상세페이지가 열릴 때 스크롤바를 커스텀 숨기도록 한다.
    		mScrollBarThumbEventHandler.removeMessages(MSG_LISTVIEW_SCROLLBAR_THUMB_HIDE);
    		mScrollBarThumbEventHandler.sendEmptyMessageDelayed(MSG_LISTVIEW_SCROLLBAR_THUMB_HIDE, 1000);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

    // @@@@@
    private void startSortList(SearchListSortMethod jvListSortMethod) {
		if (mJvListSortMethod == jvListSortMethod)
			return;

		mJvListSortMethod = jvListSortMethod;

		// 정렬중에 프로그레스 대화상자를 보인다.
		assert mProgressDialog == null;
		mProgressDialog = ProgressDialog.show(this, null, "전체 리스트를 정렬중입니다.", true, false);

		new Thread() {
			@Override
			public void run() {
				// 변경된 정렬 방법을 저장한다.
				mPreferences.edit().putString(Constants.JV_SPN_LIST_SORT_METHOD, mJvListSortMethod.name()).commit();

				// 리스트 데이터 정렬합니다.
				sortList();

				Message msg = Message.obtain();
				msg.what = MSG_COMPLETED_LIST_DATA_UPDATE;
				mVocabularyDataChangedHandler.sendMessage(msg);
			};
		}.start();
	}

    // @@@@@
    private void sortList() {
		synchronized (mVocabularyListData) {
			switch (mJvListSortMethod) {
			case VOCABULARY:
				Collections.sort(mVocabularyListData, VocabularyComparator.mVocabularyComparator);
				break;
			case VOCABULARY_GANA:
				Collections.sort(mVocabularyListData, VocabularyComparator.mVocabularyGanaComparator);
				break;
			case VOCABULARY_TRANSLATION:
				Collections.sort(mVocabularyListData, VocabularyComparator.mVocabularyTranslationComparator);
				break;
			}
		}
	}

	//@@@@@@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	//@@@@@	super.onListItemClick(l, v, position, id);

		synchronized (mVocabularyListData) {
			DetailActivity.setSeekVocabularyList(new SearchResultVocabularyList(mVocabularyListData, position));
			
			// 단어 상세페이지 호출
			Intent intent = new Intent(this, DetailActivity.class);
			intent.putExtra("idx", mVocabularyListData.get(position).getIdx());
			startActivityForResult(intent, R.id.avd_vocabulary_detail_info);
		}
	}

    // @@@@@
    private Handler mVocabularyDataChangedHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_COMPLETED_LIST_DATA_UPDATE) {
				mJvListAdapter.notifyDataSetChanged();
				updateVocabularyInfo();

				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				mProgressDialog = null;
				mJvListSearchThread = null;
			} else if (msg.what == MSG_CHANGED_LIST_DATA) {
				mJvListAdapter.notifyDataSetChanged();
				updateVocabularyInfo();

				// 호출자 액티비티에게 데이터가 변경되었음을 알리도록 한다.
				mActivityResultCode |= ACTIVITY_RESULT_DATA_CHANGED;
				setResult(mActivityResultCode);
			}
		};
	};

    // @@@@@
    private void searchVocabulary() {
		mUseModeScrollBarThumb = false;

		// 단어 검색이 끝날때까지 진행 대화상자를 보인다.
		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialog.show(this, null, "단어를 검색 중입니다.", true, true);
			mProgressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					if (mJvListSearchThread != null) {
						mJvListSearchThread.interrupt();
					}

					synchronized (mVocabularyListData) {
						// 검색을 취소하였으므로 단어를 모두 제거한다.
						mVocabularyListData.clear();
					}

					Toast.makeText(SearchListActivity.this, "단어 검색이 취소되었습니다", Toast.LENGTH_SHORT).show();

					Message msg = Message.obtain();
					msg.what = MSG_COMPLETED_LIST_DATA_UPDATE;
					mVocabularyDataChangedHandler.sendMessage(msg);
				}
			});
		}
		
		// 검색을 시작하기 전 리스트의 내용을 모두 지운다.
		// 이유) 검색 스레드에서 리스트를 모두 지운후 검색을 하였을 때 간혹 오류가 발생하는 경우 있음
		mVocabularyListData.clear();
		mJvListAdapter.notifyDataSetChanged();

		mJvListSearchThread = new JvListSearchThread(mJvListSearchCondition);
		mJvListSearchThread.start();
	}

    // @@@@@
    public class JvListSearchThread extends Thread {

		private SearchListCondition mJvListSearchCondition = null;

		public JvListSearchThread(SearchListCondition jvListSearchCondition) {
			assert jvListSearchCondition != null;
			mJvListSearchCondition = jvListSearchCondition;
		}

		@Override
		public void run() {
			// 현재 검색되는 검색 정보를 저장해 놓는다.
			mJvListSearchCondition.commit();

			synchronized (mVocabularyListData) {
				mVocabularyListData.clear();
				VocabularyManager.getInstance().searchVocabulary(SearchListActivity.this, mJvListSearchCondition, mVocabularyListData);
			}

			sortList();

			Message msg = Message.obtain();
			msg.what = MSG_COMPLETED_LIST_DATA_UPDATE;
			mVocabularyDataChangedHandler.sendMessage(msg);
		};

	}

    // @@@@@
    private void updateVocabularyInfo() {
		ArrayList<Integer> vocabularyInfo = VocabularyManager.getInstance().getVocabularyInfo();
		assert vocabularyInfo.size() == 3;

		TextView allVocabularyCount = (TextView)findViewById(R.id.all_vocabulary_count);
		TextView searchVocabularyCount = (TextView)findViewById(R.id.search_vocabulary_count);
		TextView memorizeTargetCount = (TextView)findViewById(R.id.memorize_target_count);
		TextView memorizeCompletedCount = (TextView)findViewById(R.id.avd_memorize_completed_count_text);

		allVocabularyCount.setText(String.format("%d개", vocabularyInfo.get(0)));
		searchVocabularyCount.setText(String.format("%d개", mVocabularyListData.size()));
		memorizeTargetCount.setText(String.format("%d개", vocabularyInfo.get(1)));
		memorizeCompletedCount.setText(String.format("%d개", vocabularyInfo.get(2)));
	}

	@Override
    // @@@@@
    public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sc_jlpt_level:
		{
			boolean[] checkedItems = mJvListSearchCondition.getCheckedJLPTLevelArray();
			new AlertDialog.Builder(SearchListActivity.this)
					.setTitle("검색 조건")
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
									// 사용자 경험(화면 멈춤)을 위해 아래 commit() 하는 부분은 주석처리한다.
									// mJvListSearchCondition.commit();
									updateJLPTLevelButtonText();
								}
							})
					.show();
		}
			break;

		case R.id.search_start:
		{
			// 소프트 키보드가 나타나 있다면 숨긴다.
		    InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	        EditText searchWord = (EditText)findViewById(R.id.sc_search_word);
	        mgr.hideSoftInputFromWindow(searchWord.getWindowToken(), 0);

			SlidingDrawer searchSlidingDrawer = (SlidingDrawer)findViewById(R.id.search_sliding_drawer);
			searchSlidingDrawer.animateClose();

			// 설정된 검색 조건들을 저장합니다.
			EditText scSearchWordEditText = (EditText)findViewById(R.id.sc_search_word);
			Spinner scMemorizeTargetSpinner = (Spinner)findViewById(R.id.sc_memorize_target);
			Spinner scMemorizeCompletedSpinner = (Spinner)findViewById(R.id.sc_memorize_completed);

			mJvListSearchCondition.setSearchWord(scSearchWordEditText.getText().toString().trim());
			mJvListSearchCondition.setMemorizeTargetPosition(scMemorizeTargetSpinner.getSelectedItemPosition());
			mJvListSearchCondition.setMemorizeCompletedPosition(scMemorizeCompletedSpinner.getSelectedItemPosition());

			// 사용자 경험(화면 멈춤)을 위해 아래 commit() 하는 부분은 주석처리한다.
			// 대신 commit()은 검색을 시작하기 전에 하도록 변경한다.
			// mJvListSearchCondition.commit();

			// 설정된 검색 조건을 이용하여 단어를 검색합니다.
			searchVocabulary();
		}
			break;

		case R.id.search_cancel:
			// 소프트 키보드가 나타나 있다면 숨긴다.
		    InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	        EditText searchWord = (EditText)findViewById(R.id.sc_search_word);
	        mgr.hideSoftInputFromWindow(searchWord.getWindowToken(), 0);

	        SlidingDrawer searchSlidingDrawer = (SlidingDrawer)findViewById(R.id.search_sliding_drawer);
			searchSlidingDrawer.animateClose();

			break;
		}
	}

	@Override
    // @@@@@
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.activity_vocabulary_search_list_context, menu);
		menu.setHeaderTitle("작업");
	}

	@Override
    // @@@@@
    public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.avsl_exclude_vocabulary:
			AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)item.getMenuInfo();
			mVocabularyListData.remove(menuInfo.position);
			
			updateVocabularyInfo();
			mJvListAdapter.notifyDataSetChanged();
			break;
		}

		return super.onContextItemSelected(item);
	}

	@Override
    // @@@@@
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (mUseModeScrollBarThumb == true) {
			if (mVisibleScrollBarThumb == false) {
				mVisibleScrollBarThumb = true;
				mScrollThumb.setVisibility(View.VISIBLE);
				getListView().setVerticalScrollBarEnabled(false);
			}

			mScrollThumb.onItemScroll(firstVisibleItem, visibleItemCount, totalItemCount);
		} else {
			SlidingDrawer searchSlidingDrawer = (SlidingDrawer) findViewById(R.id.search_sliding_drawer);
			if (searchSlidingDrawer.isOpened() == false) {
				if (mVocabularyListData.size() >= 50)
					mUseModeScrollBarThumb = true;				
			}
		}
	}

	@Override
    // @@@@@
    public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case SCROLL_STATE_IDLE:
    		mScrollBarThumbEventHandler.removeMessages(MSG_LISTVIEW_SCROLLBAR_THUMB_HIDE);
    		mScrollBarThumbEventHandler.sendEmptyMessageDelayed(MSG_LISTVIEW_SCROLLBAR_THUMB_HIDE, 1000);
			break;

		case SCROLL_STATE_TOUCH_SCROLL:
    		mScrollBarThumbEventHandler.removeMessages(MSG_LISTVIEW_SCROLLBAR_THUMB_HIDE);
			break;
		}
	}

    // @@@@@
    private Handler mScrollBarThumbEventHandler = new Handler() {

		@Override
    	public void handleMessage(Message msg){
    		switch(msg.what) {
	    		case MSG_LISTVIEW_SCROLLBAR_THUMB_HIDE:
	    			mUseModeScrollBarThumb = false;
	    			mVisibleScrollBarThumb = false;
	    			mScrollThumb.setVisibility(View.INVISIBLE);
	    			getListView().setVerticalScrollBarEnabled(true);
	    			break;

				default:
					 throw new RuntimeException("Unknown message " + msg);
    		}
    	}
    };

	@Override
    // @@@@@
	public void onConfigurationChanged(Configuration newConfig) {
		// 화면이 회전되었을 경우를 위해 좌표값을 다시 계산한다.
		mScrollBarThumbLayout.x = mScrollThumb.getScrollBarThumbLayoutX();
		mScrollThumb.reset();

		super.onConfigurationChanged(newConfig);
	}

    // @@@@@
	public class ScrollBarThumb extends ImageView {

		private ListView mListView = null;

		private int mThumbHeight = 0;
		private int mTitleBarHeight = 0;

		// 리스트뷰에서 Thumb 높이를 제외한 크기
		private int mListViewTraverseHeight = 0;

		// 사용자가 Thumb의 위치를 조정하여 리스트뷰가 스크롤되었을 때 Thumb의 위치를 재조정하지 않도록 막기위한 플래그 변수
		private boolean mAccInitiated = false;

		public ScrollBarThumb(Context context, ListView listView) {
			super(context, null);

			mListView = listView;
			assert mListView != null;

			Drawable drawable = context.getResources().getDrawable(R.drawable.scrollbar_thumb);
			mThumbHeight = drawable.getIntrinsicHeight();
			setImageDrawable(drawable);
			
			setAlpha(0xD0);
			setFocusable(false);
		}

		public void reset() {
			mTitleBarHeight = 0;
			mListViewTraverseHeight = 0;
		}

		public int getScrollBarThumbLayoutX() {
			Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			BitmapDrawable drawable = (BitmapDrawable)getResources().getDrawable(R.drawable.scrollbar_thumb);

            return 0;//@@@@@ 임시주석
//			if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
//				return display.getWidth() - drawable.getIntrinsicWidth();
//			} else {
//				return display.getHeight() - drawable.getIntrinsicWidth();
//			}
		}

		public void onItemScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			assert mListView != null;

			// 사용자가 Thumb의 위치를 조정하여 리스트뷰가 스크롤되었을 때 Thumb의 위치를 재조정하지 않도록 한다.
			if (mAccInitiated == true) {
				mAccInitiated = false;
				return;
			}

			if (mTitleBarHeight <= 0) {
				mTitleBarHeight = getTitleBarHeight();
				assert mTitleBarHeight > 0;
			}

			// 리스트뷰에서 Thumb 높이를 제외한 크기를 구한다.
			if (mListViewTraverseHeight <= 0) {
				mListViewTraverseHeight = mListView.getMeasuredHeight() - mThumbHeight;
				assert mListViewTraverseHeight > 0;
			}

			// 글로벌 좌표를 기준으로 리스트뷰의 영역을 구한다.
			Rect r = new Rect();
			Point globalOffset = new Point();
			mListView.getGlobalVisibleRect(r, globalOffset);

			if (totalItemCount == 0)
				totalItemCount = 1;

			mScrollBarThumbLayout.y = (int)(mListViewTraverseHeight * firstVisibleItem / (float)totalItemCount + (globalOffset.y - mTitleBarHeight));

			// Thumb의 위치를 갱신한다.
			mWindowManager.updateViewLayout(this, mScrollBarThumbLayout);
		}

		public boolean onTouchEvent(MotionEvent event) {
			assert mListView != null;

    		mScrollBarThumbEventHandler.removeMessages(MSG_LISTVIEW_SCROLLBAR_THUMB_HIDE);

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				return true;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
	    		mScrollBarThumbEventHandler.sendEmptyMessageDelayed(MSG_LISTVIEW_SCROLLBAR_THUMB_HIDE, 1000);				
			}

			if (mTitleBarHeight <= 0) {
				mTitleBarHeight = getTitleBarHeight();
				assert mTitleBarHeight > 0;
			}

			// 리스트뷰에서 Thumb 높이를 제외한 크기를 구한다.
			if (mListViewTraverseHeight <= 0) {
				mListViewTraverseHeight = mListView.getMeasuredHeight() - mThumbHeight;
				assert mListViewTraverseHeight > 0;
			}

			// 글로벌 좌표를 기준으로 리스트뷰의 영역을 구한다.
			Rect r = new Rect();
			Point globalOffset = new Point();
			mListView.getGlobalVisibleRect(r, globalOffset);

			// Thumb의 위치를 계산한다.
			mScrollBarThumbLayout.y = (int)(event.getRawY() - mTitleBarHeight - (mThumbHeight / 2.0));

			if (mScrollBarThumbLayout.y < (globalOffset.y - mTitleBarHeight))
				mScrollBarThumbLayout.y = globalOffset.y - mTitleBarHeight;
			
			// Thumb의 위치를 갱신한다.
			mWindowManager.updateViewLayout(this, mScrollBarThumbLayout);

			// 리스트뷰에 Thumb가 위치한 비율에 따라 리스트뷰의 스크롤 위치를 조정한다.
			int fy = mScrollBarThumbLayout.y - globalOffset.y + mTitleBarHeight;
			if (fy < 0)
				fy = 0;
			if (fy >= mListViewTraverseHeight)
				fy = mListViewTraverseHeight;
			fy = (int)(mListView.getCount() * (fy / (float)mListViewTraverseHeight) + 0.5);

			if (fy >= mListView.getCount())
				fy = mListView.getCount() - 1;

			mListView.setSelectionFromTop(fy, 0);				

			// 리스트뷰의 스크롤 위치가 변경될 때 Thumb의 위치를 재조정하지 않기 위한 플래그 값을 설정한다.
			mAccInitiated = true;

			return true;
		}

		private int getTitleBarHeight() {
			Rect r = new Rect();
			Window window = getWindow();
			window.getDecorView().getWindowVisibleDisplayFrame(r);

			int statusBarHeight = r.top;
			int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		
			return contentViewTop - statusBarHeight;				
		}
	}

}
