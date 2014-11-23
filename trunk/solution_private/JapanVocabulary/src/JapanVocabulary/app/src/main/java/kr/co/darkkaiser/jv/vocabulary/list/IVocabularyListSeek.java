package kr.co.darkkaiser.jv.vocabulary.list;

import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;

public interface IVocabularyListSeek {

    public Vocabulary getVocabulary();

    public Vocabulary previousVocabulary(StringBuilder sbErrMessage);

    public Vocabulary nextVocabulary(StringBuilder sbErrMessage);

    public boolean isValid();

}
