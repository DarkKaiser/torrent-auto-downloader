package kr.co.darkkaiser.jv.vocabulary.data;

import java.text.Collator;
import java.util.Comparator;

// @@@@@
public class VocabularyComparator {
	
	public final static Comparator<Vocabulary> mVocabularyComparator = new Comparator<Vocabulary>() {
		private final Collator collator = Collator.getInstance();

		@Override
		public int compare(Vocabulary lhs, Vocabulary rhs) {
			return collator.compare(lhs.getVocabulary(), rhs.getVocabulary());
		}
	};

	public final static Comparator<Vocabulary> mVocabularyGanaComparator = new Comparator<Vocabulary>() {
		private final Collator collator = Collator.getInstance();

		@Override
		public int compare(Vocabulary lhs, Vocabulary rhs) {
			return collator.compare(lhs.getVocabularyGana(), rhs.getVocabularyGana());
		}
	};

	public final static Comparator<Vocabulary> mVocabularyTranslationComparator = new Comparator<Vocabulary>() {
		private final Collator collator = Collator.getInstance();

		@Override
		public int compare(Vocabulary lhs, Vocabulary rhs) {
			return collator.compare(lhs.getVocabularyTranslation(), rhs.getVocabularyTranslation());
		}
	};

}
