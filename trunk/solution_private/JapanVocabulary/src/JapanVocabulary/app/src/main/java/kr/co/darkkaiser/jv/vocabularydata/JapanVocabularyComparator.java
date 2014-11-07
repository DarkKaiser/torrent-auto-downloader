package kr.co.darkkaiser.jv.vocabularydata;

import java.text.Collator;
import java.util.Comparator;

public class JapanVocabularyComparator {
	
	public final static Comparator<JapanVocabulary> mVocabularyComparator = new Comparator<JapanVocabulary>() {
		private final Collator collator = Collator.getInstance();

		@Override
		public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
			return collator.compare(lhs.getVocabulary(), rhs.getVocabulary());
		}
	};

	public final static Comparator<JapanVocabulary> mVocabularyGanaComparator = new Comparator<JapanVocabulary>() {
		private final Collator collator = Collator.getInstance();

		@Override
		public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
			return collator.compare(lhs.getVocabularyGana(), rhs.getVocabularyGana());
		}
	};

	public final static Comparator<JapanVocabulary> mVocabularyTranslationComparator = new Comparator<JapanVocabulary>() {
		private final Collator collator = Collator.getInstance();

		@Override
		public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
			return collator.compare(lhs.getVocabularyTranslation(), rhs.getVocabularyTranslation());
		}
	};

}
