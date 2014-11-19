package kr.co.darkkaiser.jv.vocabulary.list;

import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;

public interface IVocabularyList {

    public Vocabulary getCurrentVocabulary();

    public Vocabulary previousVocabulary(StringBuilder sbErrMessage);

    public Vocabulary nextVocabulary(StringBuilder sbErrMessage);

    public boolean isValid();

}
