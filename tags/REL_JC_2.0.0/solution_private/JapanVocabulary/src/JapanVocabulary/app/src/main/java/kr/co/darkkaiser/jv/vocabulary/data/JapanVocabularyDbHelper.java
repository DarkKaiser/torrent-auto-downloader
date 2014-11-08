package kr.co.darkkaiser.jv.vocabulary.data;

import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import kr.co.darkkaiser.jv.common.JvDefines;

public class JapanVocabularyDbHelper {

	public static ArrayList<String> getLatestVocabularyDbInfoList() throws Exception {
		URL url = new URL(JvDefines.JV_DB_VERSION_CHECK_URL);

		XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
		XmlPullParser parser = parserCreator.newPullParser();
		parser.setInput(url.openStream(), null);

		String tagName = null;
		int eventType = parser.getEventType();
		String newVocabularyDbVersion = "", newVocabularyDbFileHash = "";

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.TEXT:
				if (tagName != null) {
					if (tagName.equals("version") == true) {
						newVocabularyDbVersion = parser.getText();
					} else if (tagName.equals("sha1") == true) {
						newVocabularyDbFileHash = parser.getText();
					}
				}
				break;
			case XmlPullParser.END_TAG:
				tagName = null;
				break;                
			case XmlPullParser.START_TAG:
				tagName = parser.getName();
				break;
			}

			eventType = parser.next();
		}
		
		ArrayList<String> result = new ArrayList<String>();
		result.add(newVocabularyDbVersion);
		result.add(newVocabularyDbFileHash);
		return result;
	}

    public static String getLatestVocabularyDbVersion() throws Exception {
        ArrayList<String> vocaDbInfo = getLatestVocabularyDbInfoList();

        if (vocaDbInfo.size() >= 1) {
            return vocaDbInfo.get(0/* DB Version */);
        }

        return "";
    }

}
