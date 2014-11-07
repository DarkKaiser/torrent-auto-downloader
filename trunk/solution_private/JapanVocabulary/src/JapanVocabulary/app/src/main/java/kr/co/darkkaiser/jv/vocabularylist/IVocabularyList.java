package kr.co.darkkaiser.jv.vocabularylist;

import kr.co.darkkaiser.jv.vocabulary.data.JapanVocabulary;

public interface IVocabularyList {

    public JapanVocabulary getCurrentVocabulary();

    public JapanVocabulary previousVocabulary(StringBuilder sbErrMessage);

    public JapanVocabulary nextVocabulary(StringBuilder sbErrMessage);

}
