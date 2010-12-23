package kr.co.darkkaiser.jv.view.detail;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.data.JapanVocabulary;
import kr.co.darkkaiser.jv.data.JapanVocabularyManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.widget.TextView;
import android.widget.Toast;

public class JvDetailActivity extends Activity implements OnGestureListener {

	private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_MAX_OFF_PATH = 170;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private GestureDetector mGestureScanner = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jv_detail);

		// 타이틀을 설정한다.
		setTitle(String.format("%s - 단어상세정보", getResources().getString(R.string.app_name)));

		// 단어 상세 정보를 출력한다.
		long idx = -1;
		Intent intent = getIntent();
		if (intent != null)
			idx = intent.getLongExtra("idx", -1);

		JapanVocabulary jpVocabulary = null;

		if (idx != -1)
			jpVocabulary = JapanVocabularyManager.getInstance().getJapanVocabulary(idx);

		if (jpVocabulary == null) {
			assert false;

			Toast.makeText(this, "단어의 상세 정보를 얻을 수 없습니다.", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		TextView vocabulary = (TextView) findViewById(R.id.vocabulary);
		TextView vocabularyGana = (TextView) findViewById(R.id.vocabulary_gana);
		TextView vocabularyTranslation = (TextView) findViewById(R.id.vocabulary_translation);
		TextView memorizeCompletedInfo1 = (TextView) findViewById(R.id.memorize_completed_info1);
		TextView memorizeCompletedInfo2 = (TextView) findViewById(R.id.memorize_completed_info2);
		TextView registrationDate = (TextView) findViewById(R.id.registration_date);
		TextView vocabularyDetailInfo = (TextView) findViewById(R.id.vocabulary_detail_info);

		vocabulary.setText(jpVocabulary.getVocabulary());
		vocabularyGana.setText(jpVocabulary.getVocabularyGana());
		vocabularyTranslation.setText(jpVocabulary.getVocabularyTranslation());
		registrationDate.setText(jpVocabulary.getRegistrationDateString());

		long memorizeCompletedCount = jpVocabulary.getMemorizeCompletedCount();
		if (jpVocabulary.isMemorizeCompleted() == true) {
			assert memorizeCompletedCount > 0;

			memorizeCompletedInfo1.setText(String.format("총 %d회 암기 완료", memorizeCompletedCount));
			memorizeCompletedInfo1.setTextColor(getResources().getColor(R.color.jv_detail_memorize_completed_count_text));
			
			memorizeCompletedInfo2.setText("");
			memorizeCompletedInfo2.setVisibility(View.GONE);
		} else {
			memorizeCompletedInfo1.setText("미암기");
			memorizeCompletedInfo1.setTextColor(getResources().getColor(R.color.jv_detail_memorize_uncompleted_text));

			if (memorizeCompletedCount > 0) {
				memorizeCompletedInfo2.setText(String.format("총 %d회 암기 완료", memorizeCompletedCount));
				memorizeCompletedInfo2.setTextColor(getResources().getColor(R.color.jv_detail_memorize_completed_count_text));
				memorizeCompletedInfo2.setVisibility(View.VISIBLE);
			} else {
				memorizeCompletedInfo2.setText("");
				memorizeCompletedInfo2.setVisibility(View.GONE);
			}
		}

		vocabularyDetailInfo.setText(JapanVocabularyManager.getInstance().getJapanVocabularyDetailDescription(jpVocabulary.getVocabulary()));

		// 제스쳐 감지 객체를 생성한다.
		mGestureScanner = new GestureDetector(this);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;

//            // right to left swipe
//            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
//            }

            // left to right swipe
            if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                finish();
            }

//            // down to up swipe
//            else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//                Toast.makeText(getApplicationContext(), "Swipe up", Toast.LENGTH_SHORT).show();
//            }
//            // up to down swipe
//            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//                Toast.makeText(getApplicationContext(), "Swipe down", Toast.LENGTH_SHORT).show();
//            }
        } catch (Exception e) {
        }

		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureScanner.onTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return mGestureScanner.onTouchEvent(ev);
	}

}
