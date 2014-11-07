package kr.co.darkkaiser.jv.view.list;

import java.util.ArrayList;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.data.JapanVocabulary;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

//@@@@@
public class JvSearchListAdapter extends BaseAdapter {

	private int mLayout;
	private Context mContext = null;
	private ArrayList<JapanVocabulary> mJvList = null;
	private Handler mJvListDataChangedHandler = null;
	private LayoutInflater mLayoutInflater = null;

	public JvSearchListAdapter(Context context, int layout, Handler jvListDataChangedHandler, ArrayList<JapanVocabulary> jvList) {
		mLayout = layout;
		mJvList = jvList;
		mContext = context;
		mJvListDataChangedHandler = jvListDataChangedHandler;
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mJvList.size();
	}

	@Override
	public String getItem(int position) {
		assert mJvList != null;
		assert mJvList.get(position) != null;

		return mJvList.get(position).getVocabulary();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(mLayout, parent, false);
		}

		JapanVocabulary jpVocabulary = mJvList.get(position);
		if (jpVocabulary == null) {
			convertView.findViewById(R.id.memorize_target).setVisibility(View.INVISIBLE);
			convertView.findViewById(R.id.memorize_completed).setVisibility(View.INVISIBLE);
			
			assert false;
			return convertView;
		}

		TextView memorizeBar = (TextView) convertView.findViewById(R.id.memorize_bar);
		TextView vocabulary = (TextView) convertView.findViewById(R.id.vocabulary);
		TextView vocabularyGana = (TextView) convertView.findViewById(R.id.vocabulary_gana);
		TextView vocabularyTranslation = (TextView) convertView.findViewById(R.id.vocabulary_translation);
		TextView registrationDate = (TextView) convertView.findViewById(R.id.registration_date);
		CheckBox memorizeCompleted = (CheckBox) convertView.findViewById(R.id.memorize_completed);
		CheckBox memorizeTarget = (CheckBox) convertView.findViewById(R.id.memorize_target);
		LinearLayout layoutMemorizeBg = (LinearLayout) convertView.findViewById(R.id.memorize_bg);

		vocabulary.setText(String.format("%s (%d회)", jpVocabulary.getVocabulary(), jpVocabulary.getMemorizeCompletedCount()));
		registrationDate.setText(String.format("%s:%s", "등록일", jpVocabulary.getRegistrationDateString()));
		vocabularyGana.setText(jpVocabulary.getVocabularyGana());
		vocabularyTranslation.setText(jpVocabulary.getVocabularyTranslation());

		if (jpVocabulary.isMemorizeCompleted() == true) {
			memorizeCompleted.setChecked(true);
			memorizeBar.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.memorize_bar));
			layoutMemorizeBg.setBackgroundColor(mContext.getResources().getColor(R.color.jv_listitem_memorize_completed_bg));
		} else {
			memorizeCompleted.setChecked(false);
			memorizeBar.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.unmemorize_bar));
			layoutMemorizeBg.setBackgroundColor(mContext.getResources().getColor(R.color.jv_listitem_memorize_uncompleted_bg));
		}

		memorizeTarget.setTag(position);
		memorizeTarget.setChecked(jpVocabulary.isMemorizeTarget());
		
		memorizeTarget.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckBox cboMemorizeTarget = (CheckBox)v;
				int position = (Integer)cboMemorizeTarget.getTag();
				if (position < mJvList.size()) {
					mJvList.get(position).setMemorizeTarget(cboMemorizeTarget.isChecked(), true);

					// 화면을 업데이트합니다.
					mJvListDataChangedHandler.sendEmptyMessage(JvSearchListActivity.MSG_CHANGED_LIST_DATA);					
				}
			}
		});
		
		memorizeCompleted.setTag(position);

		memorizeCompleted.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckBox cboMemorizeCompleted = (CheckBox)v;
				int position = (Integer)cboMemorizeCompleted.getTag();
				if (position < mJvList.size()) {
					mJvList.get(position).setMemorizeCompleted(cboMemorizeCompleted.isChecked(), true, true);

					// 화면을 업데이트합니다.
					mJvListDataChangedHandler.sendEmptyMessage(JvSearchListActivity.MSG_CHANGED_LIST_DATA);					
				}
			}
		});

		return convertView;
	}

}
