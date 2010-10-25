package kr.co.darkkaiser.jv.list;

import java.util.ArrayList;

import kr.co.darkkaiser.jv.JapanVocabulary;
import kr.co.darkkaiser.jv.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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

		JapanVocabulary jv = mJvList.get(position);
		TextView tvMemorizeBar = (TextView)convertView.findViewById(R.id.memorize_bar);
		TextView tvVocabulary = (TextView)convertView.findViewById(R.id.vocabulary);
		TextView tvVocabularyGana = (TextView)convertView.findViewById(R.id.vocabulary_gana);
		TextView tvVocabularyTranslation = (TextView)convertView.findViewById(R.id.vocabulary_translation);
		TextView tvRegistrationDate = (TextView)convertView.findViewById(R.id.registration_date);
		CheckBox cboMemorizeCompleted = (CheckBox)convertView.findViewById(R.id.memorize_completed);
		CheckBox cboMemorizeTarget = (CheckBox)convertView.findViewById(R.id.memorize_target);
		LinearLayout layoutMemorizeBg = (LinearLayout)convertView.findViewById(R.id.memorize_bg);

		tvVocabulary.setText(String.format("%s (%d회)", jv.getVocabulary(), jv.getMemorizeCompletedCount()));
		tvVocabularyGana.setText(jv.getVocabularyGana());
		tvVocabularyTranslation.setText(jv.getVocabularyTranslation());
		tvRegistrationDate.setText(String.format("%s:%s", mContext.getResources().getString(R.string.registration_date_text), jv.getRegistrationDateString()));

		if (jv.isMemorizeCompleted() == true) {
			cboMemorizeCompleted.setChecked(true);
			tvMemorizeBar.setBackgroundColor(mContext.getResources().getColor(R.color.memorize_completed));
			layoutMemorizeBg.setBackgroundColor(mContext.getResources().getColor(R.color.memorize_completed_bg));
		} else {
			cboMemorizeCompleted.setChecked(false);
			tvMemorizeBar.setBackgroundColor(mContext.getResources().getColor(R.color.memorize_uncompleted));
			layoutMemorizeBg.setBackgroundColor(mContext.getResources().getColor(R.color.memorize_uncompleted_bg));
		}

		if (jv.isMemorizeTarget() == true)
			cboMemorizeTarget.setChecked(true);
		else
			cboMemorizeTarget.setChecked(false);

		cboMemorizeTarget.setTag(position);
		cboMemorizeTarget.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cboMemorizeTarget = (CheckBox)v;
				int position = (Integer)cboMemorizeTarget.getTag();
				mJvList.get(position).setMemorizeTarget(cboMemorizeTarget.isChecked(), true);

				// 화면을 업데이트합니다.
				Message msg = Message.obtain();
				msg.what = JvListActivity.MSG_UPDATE_LIST_ITEM_DATA;
				mJvListDataChangedHandler.sendMessage(msg);
			}
		});
		
		cboMemorizeCompleted.setTag(position);
		cboMemorizeCompleted.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cboMemorizeCompleted = (CheckBox)v;
				int position = (Integer)cboMemorizeCompleted.getTag();
				mJvList.get(position).setMemorizeCompleted(cboMemorizeCompleted.isChecked(), true, true);
				
				// 화면을 업데이트합니다.
				Message msg = Message.obtain();
				msg.what = JvListActivity.MSG_UPDATE_LIST_ITEM_DATA;
				mJvListDataChangedHandler.sendMessage(msg);
			}
		});

		return convertView;
	}

}
