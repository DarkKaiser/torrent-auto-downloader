package kr.co.darkkaiser.jv.vocabulary.data;

import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import kr.co.darkkaiser.jv.common.Constants;

public class JapanVocabularyDbHelper {

	public static ArrayList<String> getLatestVocabularyDbInfoList() throws Exception {
		URL url = new URL(Constants.JV_DB_VERSION_CHECK_URL);

        // @@@@@ todo json 형태로 변경
		XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
		XmlPullParser parser = parserCreator.newPullParser();
		parser.setInput(url.openStream(), null);

		String tagName = null;
		int evtType = parser.getEventType();
		String newVocabularyDbVersion = "", newVocabularyDbFileHash = "";

		while (evtType != XmlPullParser.END_DOCUMENT) {
			switch (evtType) {
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

			evtType = parser.next();
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
