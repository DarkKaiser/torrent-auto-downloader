package kr.co.darkkaiser.jv.controller;

import kr.co.darkkaiser.jv.vocabularydata.JapanVocabulary;

public interface IVocabularyList {

    public JapanVocabulary getCurrentVocabulary();

    public JapanVocabulary previousVocabulary(StringBuilder sbErrMessage);

    public JapanVocabulary nextVocabulary(StringBuilder sbErrMessage);

}
