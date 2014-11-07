package kr.co.darkkaiser.jv.vocabularylist;

import kr.co.darkkaiser.jv.vocabularydata.JapanVocabulary;

public interface IVocabularyList {

    public JapanVocabulary getCurrentVocabulary();

    public JapanVocabulary previousVocabulary(StringBuilder sbErrMessage);

    public JapanVocabulary nextVocabulary(StringBuilder sbErrMessage);

}
