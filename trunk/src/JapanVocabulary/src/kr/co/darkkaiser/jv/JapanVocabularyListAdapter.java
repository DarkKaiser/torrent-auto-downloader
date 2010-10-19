package kr.co.darkkaiser.jv;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class JapanVocabularyListAdapter extends BaseAdapter {

	private int mLayout = 0;
	private LayoutInflater mLayoutInflater = null;
	private ArrayList<JapanVocabulary> mJvList = null;

	private Context mContext = null;
	private Handler mDataChangedHandler = null;

	public JapanVocabularyListAdapter(Context context, int layout, ArrayList<JapanVocabulary> jvList, Handler dataChangedHandler) {
		mContext = context;
		mLayout = layout;
		mJvList = jvList;
		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDataChangedHandler = dataChangedHandler;
	}

	@Override
	public int getCount() {
		return mJvList.size();
	}

	@Override
	public String getItem(int position) {
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

		JapanVocabulary japanVocabulary = mJvList.get(position);
		assert japanVocabulary != null;

		TextView tvMemorizeBar = (TextView)convertView.findViewById(R.id.memorize_bar);
		TextView tvVocabulary = (TextView)convertView.findViewById(R.id.vocabulary);
		TextView tvVocabularyGana = (TextView)convertView.findViewById(R.id.vocabulary_gana);
		TextView tvRegistrationDate = (TextView)convertView.findViewById(R.id.registration_date);
		TextView tvVocabularyTranslation = (TextView)convertView.findViewById(R.id.vocabulary_translation);
		CheckBox cboMemorizeCompleted = (CheckBox)convertView.findViewById(R.id.memorize_completed);
		CheckBox cboMemorizeTarget = (CheckBox)convertView.findViewById(R.id.memorize_target);

		tvVocabulary.setText(japanVocabulary.getVocabulary());
		tvVocabularyGana.setText(japanVocabulary.getVocabularyGana());
		tvVocabularyTranslation.setText(japanVocabulary.getVocabularyTranslation());
		tvRegistrationDate.setText(String.format("µÓ∑œ¿œ:%s", japanVocabulary.getRegistrationDateString()));

		if (japanVocabulary.isMemorizeCompleted() == true) {
			cboMemorizeCompleted.setChecked(true);
			tvMemorizeBar.setBackgroundColor(mContext.getResources().getColor(R.color.memorize_completed));
		} else {
			cboMemorizeCompleted.setChecked(false);
			tvMemorizeBar.setBackgroundColor(mContext.getResources().getColor(R.color.memorize_uncompleted));
		}

		if (japanVocabulary.isMemorizeTarget() == true)
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
