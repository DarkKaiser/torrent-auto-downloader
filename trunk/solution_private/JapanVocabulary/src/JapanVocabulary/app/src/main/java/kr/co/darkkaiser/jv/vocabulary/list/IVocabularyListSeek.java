package kr.co.darkkaiser.jv.vocabulary.list;

import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;

public interface IVocabularyListSeek {

    public Vocabulary getVocabulary();

    public Vocabulary previousVocabulary(StringBuilder sbErrorMessage);

    public Vocabulary nextVocabulary(StringBuilder sbErrorMessage);

    public void setMemorizeTarget(boolean flag);

    public void setMemorizeCompleted(boolean flag);

    public boolean canSeek();

    public boolean isValid();

}
