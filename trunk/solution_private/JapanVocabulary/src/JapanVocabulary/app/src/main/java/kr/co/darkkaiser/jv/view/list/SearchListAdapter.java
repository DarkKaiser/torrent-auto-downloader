package kr.co.darkkaiser.jv.view.list;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.androidquery.AQuery;

import java.util.ArrayList;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;

public class SearchListAdapter extends BaseAdapter {

	private int mLayout;
	private Context mContext = null;
    private LayoutInflater mLayoutInflater = null;
    private ArrayList<Vocabulary> mVocabularyList = null;
    private Handler mVocabularyListDataChangedHandler = null;

	public SearchListAdapter(Context context, int layout, Handler vocabularyListDataChangedHandler, ArrayList<Vocabulary> vocabularyList) {
		mLayout = layout;
        mContext = context;
        mVocabularyList = vocabularyList;
		mVocabularyListDataChangedHandler = vocabularyListDataChangedHandler;
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mVocabularyList.size();
	}

	@Override
	public String getItem(int position) {
		assert mVocabularyList != null;
		assert mVocabularyList.get(position) != null;

		return mVocabularyList.get(position).getVocabulary();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = mLayoutInflater.inflate(mLayout, parent, false);

        AQuery aq = new AQuery(convertView);

        Vocabulary vocabulary = mVocabularyList.get(position);
        if (vocabulary == null) {
            assert false;

            aq.id(R.id.avsli_vocabulary_panel).invisible();
            aq.id(R.id.avsli_vocabulary_memorize_bar).invisible();

            return convertView;
        } else {
            aq.id(R.id.avsli_vocabulary_panel).visible();
            aq.id(R.id.avsli_vocabulary_memorize_bar).visible();
        }

        aq.id(R.id.avsli_vocabulary).text(vocabulary.getVocabulary());
        aq.id(R.id.avsli_vocabulary_gana).text(vocabulary.getVocabularyGana());
        aq.id(R.id.avsli_vocabulary_translation).text(vocabulary.getVocabularyTranslation());

        long memorizeCompletedCount = vocabulary.getMemorizeCompletedCount();
        if (memorizeCompletedCount > 0)
            aq.id(R.id.avsli_vocabulary_memorize_completed_count).text(String.format(mContext.getString(R.string.avsli_vocabulary_memorize_completed_count), memorizeCompletedCount)).visible();
        else
            aq.id(R.id.avsli_vocabulary_memorize_completed_count).invisible();

		if (vocabulary.isMemorizeCompleted() == true) {
            aq.id(R.id.avsli_vocabulary_panel).backgroundColor(mContext.getResources().getColor(R.color.avsli_vocabulary_panel_completed));
            aq.id(R.id.avsli_vocabulary_memorize_bar).backgroundColor(mContext.getResources().getColor(R.color.avsli_vocabulary_memorize_completed_bar));
        } else {
            aq.id(R.id.avsli_vocabulary_panel).backgroundColor(mContext.getResources().getColor(R.color.avsli_vocabulary_panel_uncompleted));
            aq.id(R.id.avsli_vocabulary_memorize_bar).backgroundColor(mContext.getResources().getColor(R.color.avsli_vocabulary_memorize_uncompleted_bar));
        }

        aq.id(R.id.avsli_vocabulary_memorize_target).tag(position).checked(vocabulary.isMemorizeTarget()).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cbMemorizeTarget = (CheckBox)view;
                int position = (Integer) cbMemorizeTarget.getTag();
                if (position < mVocabularyList.size()) {
                    // @@@@@ manager로 변경???
                    mVocabularyList.get(position).setMemorizeTarget(cbMemorizeTarget.isChecked(), true);

                    // 화면을 업데이트합니다.
                    mVocabularyListDataChangedHandler.sendEmptyMessage(SearchListActivity.MSG_CHANGED_LIST_DATA);
                }
            }
        });

        aq.id(R.id.avsli_vocabulary_memorize_completed).tag(position).checked(vocabulary.isMemorizeCompleted()).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cbMemorizeCompleted = (CheckBox)view;
                int position = (Integer) cbMemorizeCompleted.getTag();
                if (position < mVocabularyList.size()) {
                    // @@@@@ manager로 변경???
                    mVocabularyList.get(position).setMemorizeCompleted(cbMemorizeCompleted.isChecked(), true, true);

                    // 화면을 업데이트합니다.
                    mVocabularyListDataChangedHandler.sendEmptyMessage(SearchListActivity.MSG_CHANGED_LIST_DATA);
                }
            }
        });

		return convertView;
	}

}
