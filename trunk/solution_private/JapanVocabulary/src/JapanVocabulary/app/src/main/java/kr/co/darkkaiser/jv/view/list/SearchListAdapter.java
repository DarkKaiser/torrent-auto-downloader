package kr.co.darkkaiser.jv.view.list;

import java.util.ArrayList;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

public class SearchListAdapter extends BaseAdapter {

	private int mLayout;
	private Context mContext = null;
	private ArrayList<Vocabulary> mVocabularyList = null;
	private Handler mVocabularyListDataChangedHandler = null;
	private LayoutInflater mLayoutInflater = null;

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
    // @@@@@
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
            convertView = mLayoutInflater.inflate(mLayout, parent, false);

        Vocabulary vocabulary = mVocabularyList.get(position);
        if (vocabulary == null) {
            convertView.findViewById(R.id.memorize_target).setVisibility(View.INVISIBLE);
            convertView.findViewById(R.id.avd_vocabulary_memorize_completed).setVisibility(View.INVISIBLE);

            assert false;
            return convertView;
        }

        AQuery aq = new AQuery(convertView);

        TextView memorizeBar = (TextView) convertView.findViewById(R.id.memorize_bar);
		CheckBox memorizeCompleted = (CheckBox) convertView.findViewById(R.id.avd_vocabulary_memorize_completed);
		CheckBox memorizeTarget = (CheckBox) convertView.findViewById(R.id.memorize_target);
		LinearLayout layoutMemorizeBg = (LinearLayout) convertView.findViewById(R.id.memorize_bg);

        aq.id(R.id.avd_vocabulary).text(String.format("%s (%d회)", vocabulary.getVocabulary(), vocabulary.getMemorizeCompletedCount()));
        aq.id(R.id.avd_vocabulary_gana).text(vocabulary.getVocabularyGana());
        aq.id(R.id.avd_vocabulary_translation).text(vocabulary.getVocabularyTranslation());

		if (vocabulary.isMemorizeCompleted() == true) {
			memorizeCompleted.setChecked(true);
			memorizeBar.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.memorize_bar));
			layoutMemorizeBg.setBackgroundColor(mContext.getResources().getColor(R.color.jv_listitem_memorize_completed_bg));
		} else {
			memorizeCompleted.setChecked(false);
			memorizeBar.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.unmemorize_bar));
			layoutMemorizeBg.setBackgroundColor(mContext.getResources().getColor(R.color.jv_listitem_memorize_uncompleted_bg));
		}

		memorizeTarget.setTag(position);
		memorizeTarget.setChecked(vocabulary.isMemorizeTarget());

		memorizeTarget.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cboMemorizeTarget = (CheckBox)v;
				int position = (Integer)cboMemorizeTarget.getTag();
				if (position < mVocabularyList.size()) {
					mVocabularyList.get(position).setMemorizeTarget(cboMemorizeTarget.isChecked(), true);

					// 화면을 업데이트합니다.
					mVocabularyListDataChangedHandler.sendEmptyMessage(SearchListActivity.MSG_CHANGED_LIST_DATA);
				}
			}
		});
		
		memorizeCompleted.setTag(position);

		memorizeCompleted.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cboMemorizeCompleted = (CheckBox)v;
				int position = (Integer)cboMemorizeCompleted.getTag();
				if (position < mVocabularyList.size()) {
					mVocabularyList.get(position).setMemorizeCompleted(cboMemorizeCompleted.isChecked(), true, true);

					// 화면을 업데이트합니다.
					mVocabularyListDataChangedHandler.sendEmptyMessage(SearchListActivity.MSG_CHANGED_LIST_DATA);
				}
			}
		});

		return convertView;
	}

}
