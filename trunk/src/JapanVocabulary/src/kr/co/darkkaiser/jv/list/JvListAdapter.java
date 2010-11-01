package kr.co.darkkaiser.jv.list;

import java.util.ArrayList;

import kr.co.darkkaiser.jv.JapanVocabulary;
import kr.co.darkkaiser.jv.R;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JvListAdapter extends BaseAdapter {

	private int mLayout = 0;
	private Context mContext = null;
	private ArrayList<JapanVocabulary> mJvList = null;
	private Handler mJvListDataChangedHandler = null;
	private LayoutInflater mLayoutInflater = null;

	public JvListAdapter(Context context, int layout, Handler jvListDataChangedHandler, ArrayList<JapanVocabulary> jvList) {
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
		
		TextView memorizeBar = (TextView)convertView.findViewById(R.id.memorize_bar);
		TextView vocabulary = (TextView)convertView.findViewById(R.id.vocabulary);
		TextView vocabularyGana = (TextView)convertView.findViewById(R.id.vocabulary_gana);
		TextView vocabularyTranslation = (TextView)convertView.findViewById(R.id.vocabulary_translation);
		TextView registrationDate = (TextView)convertView.findViewById(R.id.registration_date);
		CheckBox memorizeCompleted = (CheckBox)convertView.findViewById(R.id.memorize_completed);
		CheckBox memorizeTarget = (CheckBox)convertView.findViewById(R.id.memorize_target);
		LinearLayout layoutMemorizeBg = (LinearLayout)convertView.findViewById(R.id.memorize_bg);

		JapanVocabulary japanVocabulary = mJvList.get(position);
		vocabulary.setText(String.format("%s (%d회)", japanVocabulary.getVocabulary(), japanVocabulary.getMemorizeCompletedCount()));
		vocabularyGana.setText(japanVocabulary.getVocabularyGana());
		vocabularyTranslation.setText(japanVocabulary.getVocabularyTranslation());
		registrationDate.setText(String.format("%s:%s", mContext.getResources().getString(R.string.registration_date_text), japanVocabulary.getRegistrationDateString()));

		if (japanVocabulary.isMemorizeCompleted() == true) {
			memorizeCompleted.setChecked(true);
			memorizeBar.setBackgroundColor(mContext.getResources().getColor(R.color.memorize_completed));
			layoutMemorizeBg.setBackgroundColor(mContext.getResources().getColor(R.color.memorize_completed_bg));
		} else {
			memorizeCompleted.setChecked(false);
			memorizeBar.setBackgroundColor(mContext.getResources().getColor(R.color.memorize_uncompleted));
			layoutMemorizeBg.setBackgroundColor(mContext.getResources().getColor(R.color.memorize_uncompleted_bg));
		}

		memorizeTarget.setTag(position);
		memorizeTarget.setChecked(japanVocabulary.isMemorizeTarget());

		memorizeTarget.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cboMemorizeTarget = (CheckBox)v;
				int position = (Integer)cboMemorizeTarget.getTag();
				mJvList.get(position).setMemorizeTarget(cboMemorizeTarget.isChecked(), true);

				// 화면을 업데이트합니다.
				mJvListDataChangedHandler.sendEmptyMessage(JvListActivity.MSG_CHANGED_LIST_DATA);
			}
		});

		memorizeCompleted.setTag(position);

		memorizeCompleted.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cboMemorizeCompleted = (CheckBox)v;
				int position = (Integer)cboMemorizeCompleted.getTag();
				mJvList.get(position).setMemorizeCompleted(cboMemorizeCompleted.isChecked(), true, true);
				
				// 화면을 업데이트합니다.
				mJvListDataChangedHandler.sendEmptyMessage(JvListActivity.MSG_CHANGED_LIST_DATA);
			}
		});

		return convertView;
	}

}
