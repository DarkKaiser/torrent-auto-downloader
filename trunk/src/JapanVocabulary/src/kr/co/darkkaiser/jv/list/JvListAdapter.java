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
	private LayoutInflater mLayoutInflater = null;
	
	// 암기완료, 암기대상등의 이벤트로 값이 변경되었을 경우 화면을 다시 그리기 위한 핸들러
	private Handler mDataChangedHandler = null;

	public JvListAdapter(Context context, int layout, ArrayList<JapanVocabulary> jvList, Handler dataChangedHandler) {
		mLayout = layout;
		mJvList = jvList;
		mContext = context;
		mDataChangedHandler = dataChangedHandler;
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

		tvVocabulary.setText(jv.getVocabulary());
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
				mDataChangedHandler.sendEmptyMessage(position);
			}
		});
		
		cboMemorizeCompleted.setTag(position);
		cboMemorizeCompleted.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cboMemorizeCompleted = (CheckBox)v;
				int position = (Integer) cboMemorizeCompleted.getTag();
				mJvList.get(position).setMemorizeCompleted(cboMemorizeCompleted.isChecked(), true);
				mDataChangedHandler.sendEmptyMessage(position);
			}
		});

		return convertView;
	}

}
