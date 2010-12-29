package kr.co.darkkaiser.jv.controller;

import kr.co.darkkaiser.jv.data.JapanVocabulary;

public interface JvList {
	public JapanVocabulary getCurrentVocabulary();
	public JapanVocabulary previousVocabulary(StringBuilder sbErrorMessage);
	public JapanVocabulary nextVocabulary(StringBuilder sbErrMessage);
}
