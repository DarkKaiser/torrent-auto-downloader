package kr.co.darkkaiser.jv.vocabulary.list.internal;

import kr.co.darkkaiser.jv.BuildConfig;
import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;
import kr.co.darkkaiser.jv.vocabulary.list.VocabularyListSeek;

public class SearchResultVocabularyListSeek implements VocabularyListSeek {

	private int position = -1;

    private SearchResultVocabularyList searchResultVocabularyList = null;

	public SearchResultVocabularyListSeek(SearchResultVocabularyList searchResultVocabularyList, int position) {
		assert searchResultVocabularyList != null;

        if (BuildConfig.DEBUG) {
            if (!(position >= 0 && position < this.searchResultVocabularyList.getCount())) {
                throw new RuntimeException();
            }
        }

        this.position = position;
        this.searchResultVocabularyList = searchResultVocabularyList;
	}

    @Override
    public synchronized Vocabulary getVocabulary() {
        if (isValid() == true) {
            return this.searchResultVocabularyList.getVocabulary(this.position);
        }

        return null;
    }

    @Override
	public synchronized Vocabulary previousVocabulary(StringBuilder sbErrorMessage) {
        assert sbErrorMessage != null;

		int oldPosition = this.position;

		--this.position;
		if (isValid() == true) {
            return this.searchResultVocabularyList.getVocabulary(this.position);
		} else {
            this.position = oldPosition;
			sbErrorMessage.append("이전 단어가 없습니다.");
		}

		return null;
	}

	@Override
	public synchronized Vocabulary nextVocabulary(StringBuilder sbErrorMessage) {
        assert sbErrorMessage != null;

		int oldPosition = this.position;

		++this.position;
		if (isValid() == true) {
            return this.searchResultVocabularyList.getVocabulary(this.position);
		} else {
            this.position = oldPosition;
			sbErrorMessage.append("다음 단어가 없습니다.");
		}

		return null;
	}

    @Override
    public synchronized void setMemorizeTarget(boolean flag) {
        if (isValid() == true) {
            this.searchResultVocabularyList.setMemorizeTarget(this.position, flag);
        } else {
            if (BuildConfig.DEBUG) {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public synchronized void setMemorizeCompleted(boolean flag) {
        if (isValid() == true) {
            this.searchResultVocabularyList.setMemorizeCompleted(this.position, flag);
        } else {
            if (BuildConfig.DEBUG) {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public synchronized int getPosition() {
        return this.position;
    }

    @Override
	public synchronized boolean isValid() {
        return this.searchResultVocabularyList.isValidPosition(this.position);
    }

    @Override
    public boolean canSeek() {
        return true;
    }

}
