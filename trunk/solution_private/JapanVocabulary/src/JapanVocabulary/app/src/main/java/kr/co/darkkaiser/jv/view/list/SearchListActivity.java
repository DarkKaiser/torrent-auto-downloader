package kr.co.darkkaiser.jv.view.list;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.List;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.common.Constants;
import kr.co.darkkaiser.jv.view.ActionBarListActivity;
import kr.co.darkkaiser.jv.view.detail.DetailActivity;
import kr.co.darkkaiser.jv.view.settings.SettingsActivity;
import kr.co.darkkaiser.jv.view.widgets.MultiChoiceSpinner;
import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;
import kr.co.darkkaiser.jv.vocabulary.data.VocabularyManager;
import kr.co.darkkaiser.jv.vocabulary.list.internal.SearchResultVocabularyList;
import kr.co.darkkaiser.jv.vocabulary.list.internal.SearchResultVocabularyListSeek;

public class SearchListActivity extends ActionBarListActivity {

	public static final int ACTIVITY_RESULT_DATA_CHANGED = 1;
	public static final int ACTIVITY_RESULT_PREFERENCE_CHANGED = 2;

    public static final int MSG_SEARCH_RESULT_LIST_DATA_CHANGED = 1;

    private static final int REQ_CODE_OPEN_SETTINGS_ACTIVITY = 1;
    private static final int REQ_CODE_OPEN_VOCABULARY_DETAIL_ACTIVITY = 2;

    private SearchListAdapter mSearchResultVocabularyListAdapter = null;
    private SearchResultVocabularyList mSearchResultVocabularyList = null;

    private ArrayAdapter<String> mMemorizeTargetAdapter = null;
    private ArrayAdapter<String> mMemorizeCompletedAdapter = null;

    private ProgressDialog mProgressDialog = null;

    private int mActivityResultCode = 0;

    public SearchListActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vocabulary_search_list);

		// 리스트뷰에 컨텍스트 메뉴를 등록한다.
		registerForContextMenu(getListView());

		// 리스트뷰를 초기화한다.
        mSearchResultVocabularyList = new SearchResultVocabularyList(this, getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, MODE_PRIVATE));
		mSearchResultVocabularyListAdapter = new SearchListAdapter(this, R.layout.activity_vocabulary_search_listitem, mVocabularyDataChangedHandler, mSearchResultVocabularyList);
		setListAdapter(mSearchResultVocabularyListAdapter);

        // 검색조건내의 암기대상, 암기완료 스피너 어댑터를 초기화한다.
        mMemorizeTargetAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.search_condition_memorize_target));
        mMemorizeTargetAdapter.setDropDownViewResource(R.layout.widget_custom_spinner_dropdown_item);
        mMemorizeCompletedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.search_condition_memorize_completed));
        mMemorizeCompletedAdapter.setDropDownViewResource(R.layout.widget_custom_spinner_dropdown_item);

        // 가장 마지막에 검색한 조건을 이용하여 단어를 검색한다.
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
            case R.id.avsl_search_vocabulary:
                View v = getLayoutInflater().inflate(R.layout.view_search_vocabulary, new LinearLayout(SearchListActivity.this), false);

                if (v != null) {
                    final AQuery aq = new AQuery(v);
                    final SearchListCondition searchListCondition = mSearchResultVocabularyList.getSearchListCondition();
                    final MultiChoiceSpinner jlptRankingSpinner = (MultiChoiceSpinner)v.findViewById(R.id.avsl_search_condition_jlpt_ranking);

                    // 검색조건 컨트롤을 초기화한다.
                    jlptRankingSpinner.setItems(searchListCondition.getJLPTRankingNames());
                    jlptRankingSpinner.setSelection(searchListCondition.getJLPTRankingSelectedIndicies());
                    aq.id(R.id.avsl_search_condition_search_word).text(searchListCondition.getSearchWord());
                    aq.id(R.id.avsl_search_condition_memorize_target).adapter(mMemorizeTargetAdapter).setSelection(searchListCondition.getMemorizeTarget().ordinal());
                    aq.id(R.id.avsl_search_condition_memorize_completed).adapter(mMemorizeCompletedAdapter).setSelection(searchListCondition.getMemorizeCompleted().ordinal());

                    new AlertDialog.Builder(SearchListActivity.this)
                            .setTitle(getString(R.string.avsl_search))
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int memorizeTarget = aq.id(R.id.avsl_search_condition_memorize_target).getSelectedItemPosition();
                                    int memorizeCompleted = aq.id(R.id.avsl_search_condition_memorize_completed).getSelectedItemPosition();

                                    searchListCondition.setSearchWord(aq.id(R.id.avsl_search_condition_search_word).getText().toString().trim());
                                    searchListCondition.setMemorizeTarget(SearchListCondition.MemorizeTarget.parseMemorizeTarget(memorizeTarget));
                                    searchListCondition.setMemorizeCompleted(SearchListCondition.MemorizeCompleted.parseMemorizeCompleted(memorizeCompleted));
                                    searchListCondition.setJLPTRanking(jlptRankingSpinner.getSelectedIndicies());

                                    // 설정된 검색 조건을 이용하여 단어를 검색합니다.
                                    searchVocabulary();
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setView(v)
                            .show();
                }
                return true;
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
                                    memorizeSettingsVocabulary(itemId, true);
                                }
                            })
                            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
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
        // 검색을 시작하기 전에 이전 검색단어를 모두 지운다.
        mSearchResultVocabularyList.clear();
        mSearchResultVocabularyListAdapter.notifyDataSetChanged();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                assert mProgressDialog == null;

                // TODO 프로그레스바가 잘 뜨지 않는 현상 있음
                // 프로그레스 대화상자를 보인다.
                mProgressDialog = ProgressDialog.show(SearchListActivity.this, null, getString(R.string.avsl_sort_progress_message), true, false);

                // 검색을 시작하기 전에 리스트뷰를 보이고, empty는 보이지 않도록 설정한다.
                AQuery aq = new AQuery(SearchListActivity.this);
                aq.id(android.R.id.list).visible();
                aq.id(android.R.id.empty).gone();
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

                if (mSearchResultVocabularyList.getCount() == 0) {
                    // 검색 결과가 없다면, 리스트뷰를 숨기고 empty는 보이도록 설정한다.
                    AQuery aq = new AQuery(SearchListActivity.this);
                    aq.id(android.R.id.empty).visible();
                    aq.id(android.R.id.list).gone();
                }

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

    private void memorizeSettingsVocabulary(final int menuId, final boolean excludeSearchVocabularyTargetCancel) {
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
                mSearchResultVocabularyList.memorizeSettingsVocabulary(menuId, excludeSearchVocabularyTargetCancel);
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
                DetailActivity.setVocabularyListSeek(null);
                break;
        }

		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
	    super.onListItemClick(l, v, position, id);

        Vocabulary vocabulary = mSearchResultVocabularyList.getVocabulary(position);
        if (vocabulary != null) {
            DetailActivity.setVocabularyListSeek(new SearchResultVocabularyListSeek(mSearchResultVocabularyList, position));

            // 단어 상세페이지 호출
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("idx", vocabulary.getIdx());
            startActivityForResult(intent, REQ_CODE_OPEN_VOCABULARY_DETAIL_ACTIVITY);
        }
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

    private void updateSearchResultVocabularyInfo() {
		ArrayList<Integer> vocabularyCountInfo = VocabularyManager.getInstance().getVocabularyCountInfo();
		assert vocabularyCountInfo.size() == 3;

        AQuery aq = new AQuery(this);
        aq.id(R.id.avsl_vocabulary_count_info).text(String.format(" %d개/%d개", mSearchResultVocabularyList.getCount(), vocabularyCountInfo.get(0/* 전체 단어 개수 */)));
        aq.id(R.id.avsl_vocabulary_memorize_count_info).text(String.format(" %d개/%d개", vocabularyCountInfo.get(2/* 전체 단어중 암기완료 개수 */), vocabularyCountInfo.get(1/* 전체 단어중 암기대상 개수 */)));
	}

}
