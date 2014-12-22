package kr.co.darkkaiser.jv.view.list;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.androidquery.AQuery;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;
import kr.co.darkkaiser.jv.vocabulary.list.internal.SearchResultVocabularyList;

public class SearchListAdapter extends BaseAdapter {

	private int layout;
	private Context context = null;
    private LayoutInflater layoutInflater = null;
    private Handler vocabularyDataChangedHandler = null;
    private SearchResultVocabularyList searchResultVocabularyList = null;

	public SearchListAdapter(Context context, int layout, Handler vocabularyDataChangedHandler, SearchResultVocabularyList searchResultVocabularyList) {
        assert context != null;
        assert searchResultVocabularyList != null;
        assert vocabularyDataChangedHandler != null;

		this.layout = layout;
        this.context = context;
        this.searchResultVocabularyList = searchResultVocabularyList;
		this.vocabularyDataChangedHandler = vocabularyDataChangedHandler;
		this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return this.searchResultVocabularyList.getCount();
	}

	@Override
	public String getItem(int position) {
		assert this.searchResultVocabularyList != null;

        Vocabulary vocabulary = this.searchResultVocabularyList.getVocabulary(position);
        if (vocabulary == null)
            return "";

		return vocabulary.getVocabulary();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = this.layoutInflater.inflate(this.layout, parent, false);

        AQuery aq = new AQuery(convertView);

        Vocabulary vocabulary = this.searchResultVocabularyList.getVocabulary(position);
        if (vocabulary == null) {
            aq.id(R.id.avsli_memorize_bar).invisible();
            aq.id(R.id.avsli_vocabulary_panel).invisible();
            return convertView;
        } else {
            aq.id(R.id.avsli_memorize_bar).visible();
            aq.id(R.id.avsli_vocabulary_panel).visible();
        }

        aq.id(R.id.avsli_vocabulary).text(vocabulary.getVocabulary());
        aq.id(R.id.avsli_vocabulary_gana).text(vocabulary.getVocabularyGana());
        aq.id(R.id.avsli_vocabulary_translation).text(vocabulary.getVocabularyTranslation());

        long memorizeCompletedCount = vocabulary.getMemorizeCompletedCount();
        if (memorizeCompletedCount > 0)
            aq.id(R.id.avsli_memorize_completed_count).text(String.format(this.context.getString(R.string.avsli_memorize_completed_count), memorizeCompletedCount)).visible();
        else
            aq.id(R.id.avsli_memorize_completed_count).invisible();

		if (vocabulary.isMemorizeCompleted() == true) {
            aq.id(R.id.avsli_memorize_bar).backgroundColor(this.context.getResources().getColor(R.color.avsli_memorize_bar_completed));
            aq.id(R.id.avsli_vocabulary_panel).backgroundColor(this.context.getResources().getColor(R.color.avsli_vocabulary_panel_bg_completed));
        } else {
            aq.id(R.id.avsli_memorize_bar).backgroundColor(this.context.getResources().getColor(R.color.avsli_memorize_bar_uncompleted));
            aq.id(R.id.avsli_vocabulary_panel).backgroundColor(this.context.getResources().getColor(R.color.avsli_vocabulary_panel_bg_uncompleted));
        }

        aq.id(R.id.avsli_memorize_target).tag(position).checked(vocabulary.isMemorizeTarget()).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cbMemorizeTarget = (CheckBox)view;
                int position = (Integer)cbMemorizeTarget.getTag();
                if (position >= 0 && position < searchResultVocabularyList.getCount()) {
                    // 암기대상 설정한 후, 화면을 업데이트합니다.
                    if (searchResultVocabularyList.setMemorizeTarget(position, cbMemorizeTarget.isChecked()) == true)
                        vocabularyDataChangedHandler.sendEmptyMessage(SearchListActivity.MSG_SEARCH_RESULT_LIST_DATA_CHANGED);
                }
            }
        });

        aq.id(R.id.avsli_memorize_completed).tag(position).checked(vocabulary.isMemorizeCompleted()).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cbMemorizeCompleted = (CheckBox)view;
                int position = (Integer)cbMemorizeCompleted.getTag();
                if (position >= 0 && position < searchResultVocabularyList.getCount()) {
                    // 암기완료 설정한 후, 화면을 업데이트합니다.
                    if (searchResultVocabularyList.setMemorizeCompleted(position, cbMemorizeCompleted.isChecked()) == true)
                        vocabularyDataChangedHandler.sendEmptyMessage(SearchListActivity.MSG_SEARCH_RESULT_LIST_DATA_CHANGED);
                }
            }
        });

		return convertView;
	}

}
