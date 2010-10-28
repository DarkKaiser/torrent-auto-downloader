package kr.co.darkkaiser.jv.detail;

import kr.co.darkkaiser.jv.JapanVocabulary;
import kr.co.darkkaiser.jv.JvManager;
import kr.co.darkkaiser.jv.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.widget.TextView;
import android.widget.Toast;

public class JvDetailActivity extends Activity implements OnGestureListener {

	private static final int SWIPE_MIN_DISTANCE = 90;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private GestureDetector mGestureScanner = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jv_detail);

		mGestureScanner = new GestureDetector(this);

		// 단어 상세 정보를 출력한다.
		Intent intent = getIntent();
		long idx = intent.getLongExtra("idx", -1);

		if (idx == -1) {
			Toast.makeText(this, "단어의 상세 정보를 얻을 수 없습니다.", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		JapanVocabulary japanVocabulary = JvManager.getInstance().getJapanVocabulary(idx);

		TextView vocabulary = (TextView)findViewById(R.id.vocabulary);
		TextView vocabularyGana = (TextView)findViewById(R.id.vocabulary_gana);
		TextView vocabularyTranslation = (TextView)findViewById(R.id.vocabulary_translation);
		TextView memorizeCompleted = (TextView)findViewById(R.id.memorize_completed);
		TextView memorizeCompletedCount = (TextView)findViewById(R.id.memorize_completed_count);
		TextView registrationDate = (TextView)findViewById(R.id.registration_date);
		TextView vocabularyDetailInfo = (TextView)findViewById(R.id.vocabulary_detail_info);

		vocabulary.setText(japanVocabulary.getVocabulary());
		vocabularyGana.setText(japanVocabulary.getVocabularyGana());
		vocabularyTranslation.setText(japanVocabulary.getVocabularyTranslation());
		registrationDate.setText(japanVocabulary.getRegistrationDateString());
		memorizeCompletedCount.setText("");

		long completedCount = japanVocabulary.getMemorizeCompletedCount();

		if (japanVocabulary.isMemorizeCompleted() == true) {
			assert completedCount > 0;

			memorizeCompleted.setText(String.format("총 %d회 암기 완료", completedCount));
			memorizeCompleted.setTextColor(getResources().getColor(R.color.memorize_completed));
		} else {
			memorizeCompleted.setText("암기 미완료");
			memorizeCompleted.setTextColor(getResources().getColor(R.color.memorize_uncompleted));

			if (completedCount > 0)
				memorizeCompletedCount.setText(String.format("총 %d회 암기 완료", completedCount));
		}

		// 단어에 대한 상세 정보를 출력한다.
		vocabularyDetailInfo.setText(JvManager.getInstance().getJapanVocabularyDetailDescription(japanVocabulary.getVocabulary()));
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
