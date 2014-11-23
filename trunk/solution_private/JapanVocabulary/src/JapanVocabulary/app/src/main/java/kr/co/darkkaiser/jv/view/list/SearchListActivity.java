package kr.co.darkkaiser.jv.view.list;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.util.ArrayList;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.common.Constants;
import kr.co.darkkaiser.jv.view.ActionBarListActivity;
import kr.co.darkkaiser.jv.view.detail.DetailActivity;
import kr.co.darkkaiser.jv.view.settings.SettingsActivity;
import kr.co.darkkaiser.jv.vocabulary.data.VocabularyManager;
import kr.co.darkkaiser.jv.vocabulary.list.internal.SearchResultVocabularyList;

public class SearchListActivity extends ActionBarListActivity implements OnClickListener, OnScrollListener {

	// 호출자 인텐트로 넘겨 줄 액티비티 결과 값
	public static final int ACTIVITY_RESULT_DATA_CHANGED = 1;
	public static final int ACTIVITY_RESULT_PREFERENCE_CHANGED = 2;

    public static final int MSG_SEARCH_RESULT_LIST_DATA_CHANGED = 1;

    private static final int REQ_CODE_OPEN_SETTINGS_ACTIVITY = 1;
    private static final int REQ_CODE_OPEN_VOCABULARY_DETAIL_ACTIVITY = 2;

	// 리스트뷰의 스크롤이 멈추었을 때 Thumb를 숨기기 위한 메시지
	private static final int MSG_LISTVIEW_SCROLLBAR_THUMB_HIDE = 1;

    private WindowManager mWindowManager = null;
	private SharedPreferences mPreferences = null;
	private ProgressDialog mProgressDialog = null;

    private SearchResultVocabularyList mSearchResultVocabularyList = null;
    private SearchListAdapter mSearchResultVocabularyListAdapter = null;

	private SearchListCondition mJvListSearchCondition = null;

	private ScrollBarThumb mScrollThumb = null;
	private boolean mUseModeScrollBarThumb = false;
	private boolean mVisibleScrollBarThumb = false;
	private WindowManager.LayoutParams mScrollBarThumbLayout = null;

	private int mActivityResultCode = 0;

    public SearchListActivity() {

    }

    @Override
    // @@@@@
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vocabulary_search_list);

		mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		assert mWindowManager != null;

		// 리스트뷰에 컨텍스트 메뉴를 등록한다.
		registerForContextMenu(getListView());

		// 이전에 저장해 둔 환경설정 값들을 읽어들인다.
		mPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
		mJvListSearchCondition = new SearchListCondition(this, mPreferences);

		// 단어 리스트를 초기화한다.
        mSearchResultVocabularyList = new SearchResultVocabularyList(this, getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE));
		mSearchResultVocabularyListAdapter = new SearchListAdapter(this, R.layout.activity_vocabulary_search_listitem, mVocabularyDataChangedHandler, mSearchResultVocabularyList);
		setListAdapter(mSearchResultVocabularyListAdapter);
		
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_vocabulary_search_list, menu);
        return true;
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        SubMenu sm = menu.getItem(0/* 정렬 */).getSubMenu();
        SearchListSort searchListSort = mSearchResultVocabularyList.getSortMethod();
        if (searchListSort == SearchListSort.VOCABULARY_GANA)
            sm.findItem(R.id.avsl_sort_vocabulary_gana).setChecked(true);
        else if (searchListSort == SearchListSort.VOCABULARY_TRANSLATION)
            sm.findItem(R.id.avsl_sort_vocabulary_translation).setChecked(true);
        else
            sm.findItem(R.id.avsl_sort_vocabulary).setChecked(true);

        return true;
    }

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.avsl_sort_vocabulary:
                item.setChecked(true);
                sortVocabulary(SearchListSort.VOCABULARY);
                return true;
            case R.id.avsl_sort_vocabulary_gana:
                item.setChecked(true);
                sortVocabulary(SearchListSort.VOCABULARY_GANA);
                return true;
            case R.id.avsl_sort_vocabulary_translation:
                item.setChecked(true);
                sortVocabulary(SearchListSort.VOCABULARY_TRANSLATION);
                return true;
            case R.id.avsl_search_result_vocabulary_rememorize_all: 				// 검색된 전체 단어 재암기
            case R.id.avsl_search_result_vocabulary_memorize_completed_all: 		// 검색된 전체 단어 암기 완료
            case R.id.avsl_search_result_vocabulary_memorize_target_all: 		// 검색된 전체 단어 암기 대상 만들기
            case R.id.avsl_search_result_vocabulary_memorize_target_cancel_all: 	// 검색된 전체 단어 암기 대상 해제
                // 호출자 액티비티에 데이터가 변경되었음을 알리도록 값을 설정한다.
                mActivityResultCode |= ACTIVITY_RESULT_DATA_CHANGED;
                setResult(mActivityResultCode);

                final int itemId = item.getItemId();
                if (item.getItemId() == R.id.avsl_search_result_vocabulary_memorize_target_all) {
                    new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.avsl_memorize_settings_vocabulary_ad_title))
                        .setMessage(getString(R.string.avsl_memorize_settings_vocabulary_ad_message))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                memorizeSettingsVocabulary(itemId, true);
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                memorizeSettingsVocabulary(itemId, false);
                            }
                        })
                        .show();
                } else {
                    memorizeSettingsVocabulary(itemId, false);
                }

                return true;
            case R.id.avsl_open_settings_activity:
                // 설정 페이지를 띄운다.
                startActivityForResult(new Intent(this, SettingsActivity.class), REQ_CODE_OPEN_SETTINGS_ACTIVITY);

                // 호출자 액티비티에 설정값이 변경되었음을 알리도록 값을 설정한다.
                mActivityResultCode |= ACTIVITY_RESULT_PREFERENCE_CHANGED;
                setResult(mActivityResultCode);

                return true;
		}

		return false;
	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // 컨텍스트 메뉴의 헤더타이틀을 현재 선택된 단어로 설정한다.
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(mSearchResultVocabularyListAdapter.getItem(info.position));

        getMenuInflater().inflate(R.menu.activity_vocabulary_search_list_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.avsl_exclude_vocabulary:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                mSearchResultVocabularyList.excludeVocabulary(info.position);

                updateSearchResultVocabularyInfo();
                mSearchResultVocabularyListAdapter.notifyDataSetChanged();
                break;
		}

        return super.onContextItemSelected(item);
    }

    private void searchVocabulary() {
        mUseModeScrollBarThumb = false;//@@@@@

        // 검색을 시작하기 전에 이전 검색단어를 모두 지운다.
        mSearchResultVocabularyList.clear();
        mSearchResultVocabularyListAdapter.notifyDataSetChanged();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                assert mProgressDialog == null;

                // 프로그레스 대화상자를 보인다.
                mProgressDialog = ProgressDialog.show(SearchListActivity.this, null, getString(R.string.avsl_sort_progress_message), true, false);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                mSearchResultVocabularyList.search(SearchListActivity.this);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                updateSearchResultVocabularyInfo();
                mSearchResultVocabularyListAdapter.notifyDataSetChanged();

                if (mProgressDialog != null)
                    mProgressDialog.dismiss();

                mProgressDialog = null;
            }
        }.execute();
    }

    private void sortVocabulary(SearchListSort searchListSort) {
        // 정렬 방법이 변경되지 않았다면 재정렬하지 않도록 한다.
        if (mSearchResultVocabularyList.getSortMethod() == searchListSort)
            return;

        mSearchResultVocabularyList.setSortMethod(searchListSort);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                assert mProgressDialog == null;

                // 프로그레스 대화상자를 보인다.
                mProgressDialog = ProgressDialog.show(SearchListActivity.this, null, getString(R.string.avsl_sort_progress_message), true, false);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                // 검색결과 단어 리스트를 정렬합니다.
                mSearchResultVocabularyList.sort();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                updateSearchResultVocabularyInfo();
                mSearchResultVocabularyListAdapter.notifyDataSetChanged();

                if (mProgressDialog != null)
                    mProgressDialog.dismiss();

                mProgressDialog = null;
            }
        }.execute();
    }

    private void memorizeSettingsVocabulary(final int menuId, final boolean notSearchVocabularyTargetCancel) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                assert mProgressDialog == null;

                // 프로그레스 대화상자를 보인다.
                mProgressDialog = ProgressDialog.show(SearchListActivity.this, null, getString(R.string.avsl_memorize_settings_vocabulary_progress_message), true, false);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                mSearchResultVocabularyList.memorizeSettingsVocabulary(menuId, notSearchVocabularyTargetCancel);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                updateSearchResultVocabularyInfo();
                mSearchResultVocabularyListAdapter.notifyDataSetChanged();

                if (mProgressDialog != null)
                    mProgressDialog.dismiss();

                mProgressDialog = null;
            }
        }.execute();
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_OPEN_SETTINGS_ACTIVITY:
                // 수행 작업 없음
                break;
            case REQ_CODE_OPEN_VOCABULARY_DETAIL_ACTIVITY:
                DetailActivity.setSeekVocabularyList(null);

                // @@@@@
                // 상세페이지가 열릴 때 커스텀 스크롤바를 숨기도록 한다.
                mScrollBarThumbEventHandler.removeMessages(MSG_LISTVIEW_SCROLLBAR_THUMB_HIDE);
                mScrollBarThumbEventHandler.sendEmptyMessageDelayed(MSG_LISTVIEW_SCROLLBAR_THUMB_HIDE, 1000);
                break;
        }

		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
	    super.onListItemClick(l, v, position, id);

        //@@@@@
//		synchronized (mVocabularyListData) {
//			DetailActivity.setSeekVocabularyList(new SearchResultVocabularyList(mVocabularyListData, position));
//
//			// 단어 상세페이지 호출
//			Intent intent = new Intent(this, DetailActivity.class);
//			intent.putExtra("idx", mVocabularyListData.get(position).getIdx());
//			startActivityForResult(intent, REQ_CODE_OPEN_VOCABULARY_DETAIL_ACTIVITY);
//		}
	}

    private Handler mVocabularyDataChangedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_SEARCH_RESULT_LIST_DATA_CHANGED) {
                updateSearchResultVocabularyInfo();
                mSearchResultVocabularyListAdapter.notifyDataSetChanged();

				// 호출자 액티비티에게 데이터가 변경되었음을 알리도록 한다.
				mActivityResultCode |= ACTIVITY_RESULT_DATA_CHANGED;
				setResult(mActivityResultCode);
			}
		}
	};

    // @@@@@
    private void updateSearchResultVocabularyInfo() {
		ArrayList<Integer> vocabularyInfo = VocabularyManager.getInstance().getVocabularyInfo();
		assert vocabularyInfo.size() == 3;

        AQuery aq = new AQuery(this);
        aq.id(R.id.all_vocabulary_count).text(String.format("%d개", vocabularyInfo.get(0)));
        aq.id(R.id.search_vocabulary_count).text(String.format("%d개", mSearchResultVocabularyList.getCount()));
        aq.id(R.id.memorize_target_count).text(String.format("%d개", vocabularyInfo.get(1)));
        aq.id(R.id.avd_memorize_completed_count_text).text(String.format("%d개", vocabularyInfo.get(2)));
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

			break;
		}
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
            if (mSearchResultVocabularyList.getCount() >= 50)
                mUseModeScrollBarThumb = true;
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

//            return 0;//@@@@@ 임시주석
            //noinspection ResourceType
            if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
				return display.getWidth() - drawable.getIntrinsicWidth();
			} else {
				return display.getHeight() - drawable.getIntrinsicWidth();
			}
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
