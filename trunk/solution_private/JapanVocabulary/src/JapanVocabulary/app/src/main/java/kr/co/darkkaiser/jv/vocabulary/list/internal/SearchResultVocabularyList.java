package kr.co.darkkaiser.jv.vocabulary.list.internal;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.common.Constants;
import kr.co.darkkaiser.jv.view.list.SearchListCondition;
import kr.co.darkkaiser.jv.view.list.SearchListSort;
import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;
import kr.co.darkkaiser.jv.vocabulary.data.VocabularyComparator;
import kr.co.darkkaiser.jv.vocabulary.data.VocabularyManager;
import kr.co.darkkaiser.jv.vocabulary.list.IVocabularyList;

public class SearchResultVocabularyList implements IVocabularyList {

    // 검색결과 단어리스트
	private ArrayList<Vocabulary> mVocabularyListData = new ArrayList<Vocabulary>();

    // 검색결과 단어리스트 정렬방법
    private SearchListSort mSearchListSort = SearchListSort.VOCABULARY;

    // 단어리스트 검색조건
    private SearchListCondition mSearchListCondition = null;

    private SharedPreferences mSharedPreferences = null;

    public SearchResultVocabularyList(Context context, SharedPreferences sharedPreferences) {
        assert context != null;
        assert sharedPreferences != null;

        mSharedPreferences = sharedPreferences;
        mSearchListCondition = new SearchListCondition(context, mSharedPreferences);
        mSearchListSort = SearchListSort.valueOf(mSharedPreferences.getString(Constants.SPKEY_SEARCH_LIST_SORT, SearchListSort.VOCABULARY.name()));

        clear();
    }

    public synchronized void clear() {
        mVocabularyListData.clear();
    }

    public synchronized void search(Context context) {
        assert context != null;

        // 이전에 검색된 단어를 제거한다.
        clear();

        // 검색 조건에 맞는 단어를 검색한다.
        VocabularyManager.getInstance().searchVocabulary(context, mSearchListCondition, mVocabularyListData);

        // 검색된 단어를 정렬한다.
        sort();
    }

    public synchronized void sort() {
        switch (mSearchListSort) {
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

    public synchronized Vocabulary getVocabulary(int position) {
        assert isValidPosition(position);

        return mVocabularyListData.get(position);
    }

    public synchronized void excludeVocabulary(int position) {
        assert isValidPosition(position);
        mVocabularyListData.remove(position);
    }

    public synchronized void memorizeSettingsVocabulary(int menuId, boolean notSearchVocabularyTargetCancel) {
        ArrayList<Long> idxList = new ArrayList<Long>();

        if (menuId == R.id.avsl_search_result_vocabulary_rememorize_all) { 						// 검색된 단어 재암기
            for (Vocabulary vocabulary : mVocabularyListData) {
                if (vocabulary.isMemorizeTarget() == false || vocabulary.isMemorizeCompleted() == true)
                    idxList.add(vocabulary.getIdx());
            }
        } else if (menuId == R.id.avsl_search_result_vocabulary_memorize_completed_all) { 		// 검색된 단어 암기완료
            for (Vocabulary vocabulary : mVocabularyListData) {
                if (vocabulary.isMemorizeCompleted() == false)
                    idxList.add(vocabulary.getIdx());
            }
        } else if (menuId == R.id.avsl_search_result_vocabulary_memorize_target_all) { 			// 검색된 단어 암기대상 설정
            if (notSearchVocabularyTargetCancel == true) {
                for (Vocabulary vocabulary : mVocabularyListData) {
                    idxList.add(vocabulary.getIdx());
                }
            } else {
                for (Vocabulary vocabulary : mVocabularyListData) {
                    if (vocabulary.isMemorizeTarget() == false)
                        idxList.add(vocabulary.getIdx());
                }
            }
        } else if (menuId == R.id.avsl_search_result_vocabulary_memorize_target_cancel_all) {   // 검색된 단어 암기대상 해제
            for (Vocabulary vocabulary : mVocabularyListData) {
                if (vocabulary.isMemorizeTarget() == true)
                    idxList.add(vocabulary.getIdx());
            }
        }

        VocabularyManager.getInstance().memorizeSettingsVocabulary(menuId, notSearchVocabularyTargetCancel, idxList);
    }

    public synchronized int getCount() {
        return mVocabularyListData.size();
    }

    public synchronized SearchListSort getSortMethod() {
        return mSearchListSort;
    }

    public synchronized void setSortMethod(SearchListSort searchListSort) {
        mSearchListSort = searchListSort;

        // 변경된 정렬 방법을 저장한다.
        mSharedPreferences.edit().putString(Constants.SPKEY_SEARCH_LIST_SORT, mSearchListSort.name()).commit();
    }

    // @@@@@ 함수명 고민
    public synchronized boolean setMemorizeTarget(int position) {
        if (isValidPosition(position) == true) {
            Vocabulary vocabulary = mVocabularyListData.get(position);
//            vocabulary.setMemorizeTarget();
        } else {
            assert false;
        }
        // isValidPosition
//        Vocabulary vocabulary = mSearchVocabularyList.getVocabulary();
//        vocabulary.setMemorizeTarget(cbMemorizeTarget.isChecked());
//
//        // @@@@@ 해당 단어만 상태 업데이트
//        VocabularyManager.getInstance().writeUserVocabularyInfo();

        return true;
    }

//    // @@@@@ 함수명 고민
    public synchronized boolean setMemorizeCompleted(int position) {
        // isValidPosition
//        Vocabulary vocabulary = mSearchVocabularyList.getVocabulary();
//        vocabulary.setMemorizeCompleted(cbMemorizeCompleted.isChecked(), true);
//
//        // @@@@@ 해당 단어만 상태 업데이트
//        VocabularyManager.getInstance().writeUserVocabularyInfo();

        /////////////////////////////////////////////////////////////////////////

//        if (isValidPosition() == true) {
//            Vocabulary vocabulary = mVocabularyListData.get(mPosition);
//            if (vocabulary != null && vocabulary.isMemorizeCompleted() == false) {
//                ++mMemorizeCompletedCount;
//                vocabulary.setMemorizeCompleted(true, true);
//
//                // @@@@@ 해당 단어만 상태 업데이트
//                VocabularyManager.getInstance().writeUserVocabularyInfo();
//            }
//        } else {
//            assert false;
//        }
        return true;
    }

    public synchronized boolean isValidPosition(int position) {
        return (position >= 0 && position < mVocabularyListData.size());
    }

}
