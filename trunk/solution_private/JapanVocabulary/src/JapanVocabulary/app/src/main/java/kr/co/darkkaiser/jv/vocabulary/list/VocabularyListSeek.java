package kr.co.darkkaiser.jv.vocabulary.list;

import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;

public interface VocabularyListSeek {

    Vocabulary getVocabulary();

    Vocabulary previousVocabulary(StringBuilder sbErrorMessage);

    Vocabulary nextVocabulary(StringBuilder sbErrorMessage);

    void setMemorizeTarget(boolean flag);

    void setMemorizeCompleted(boolean flag);

    int getPosition();

    boolean canSeek();

    boolean isValid();

}
