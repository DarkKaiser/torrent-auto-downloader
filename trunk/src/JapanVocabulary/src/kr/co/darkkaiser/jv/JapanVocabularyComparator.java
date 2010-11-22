package kr.co.darkkaiser.jv;

import java.text.Collator;
import java.util.Comparator;

public class JapanVocabularyComparator {
	
	public final static Comparator<JapanVocabulary> mJvVocabularyComparator = new Comparator<JapanVocabulary>() {
		private final Collator collator = Collator.getInstance();

		@Override
		public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
			return collator.compare(lhs.getVocabulary(), rhs.getVocabulary());
		}
	};

	public final static Comparator<JapanVocabulary> mJvVocabularyGanaComparator = new Comparator<JapanVocabulary>() {
		private final Collator collator = Collator.getInstance();

		@Override
		public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
			return collator.compare(lhs.getVocabularyGana(), rhs
					.getVocabularyGana());
		}
	};

	public final static Comparator<JapanVocabulary> mJvVocabularyTranslationComparator = new Comparator<JapanVocabulary>() {
		private final Collator collator = Collator.getInstance();

		@Override
		public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
			return collator.compare(lhs.getVocabularyTranslation(), rhs
					.getVocabularyTranslation());
		}
	};

	public final static Comparator<JapanVocabulary> mJvRegistrationDateUpComparator = new Comparator<JapanVocabulary>() {

		@Override
		public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
			if (lhs.getRegistrationDate() > rhs.getRegistrationDate())
				return 1;
			else if (lhs.getRegistrationDate() < rhs.getRegistrationDate())
				return -1;

			return 0;
		}
	};

	public final static Comparator<JapanVocabulary> mJvRegistrationDateDownComparator = new Comparator<JapanVocabulary>() {

		@Override
		public int compare(JapanVocabulary lhs, JapanVocabulary rhs) {
			if (lhs.getRegistrationDate() > rhs.getRegistrationDate())
				return -1;
			else if (lhs.getRegistrationDate() < rhs.getRegistrationDate())
				return 1;

			return 0;
		}
	};
	
}
