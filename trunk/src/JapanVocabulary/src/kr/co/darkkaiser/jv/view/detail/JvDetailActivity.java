package kr.co.darkkaiser.jv.view.detail;

import kr.co.darkkaiser.jv.JvDefines;
import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.controller.JvList;
import kr.co.darkkaiser.jv.controller.JvListManager;
import kr.co.darkkaiser.jv.data.JapanVocabulary;
import kr.co.darkkaiser.jv.data.JapanVocabularyManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class JvDetailActivity extends Activity implements OnGestureListener, OnClickListener {

	// 호출자 인텐트로 넘겨 줄 액티비티 결과 값, 이 값들은 서로 배타적이어야 함.
	public static final int ACTIVITY_RESULT_POSITION_CHANGED = 1;

	private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_MAX_OFF_PATH = 170;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private GestureDetector mGestureScanner = null;

    // 이전/다음 단어로 이동하기 위한 관리자 객체
	private static JvListManager mJvListManager = new JvListManager();

	public static void setVocabularySeekList(JvList list) {
		mJvListManager.setVocabularySeekList(list);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jv_detail);

		// 타이틀을 설정한다.
		setTitle(String.format("%s - 단어상세정보", getResources().getString(R.string.app_name)));

		// 상세 정보를 출력할 단어에 대한 정보를 구한다.
		JapanVocabulary jpVocabulary = null;

		if (mJvListManager.isValid() == true) {
			jpVocabulary = mJvListManager.getCurrentVocabulary();
		}

		if (jpVocabulary == null) {
			// 이전/다음 단어로 이동하기 위한 관리자 객체가 유효하지 않으므로 null로 설정한다.
			setVocabularySeekList(null);

			long idx = -1;
			Intent intent = getIntent();
			if (intent != null) {
				idx = intent.getLongExtra("idx", -1);

				if (idx != -1)
					jpVocabulary = JapanVocabularyManager.getInstance().getJapanVocabulary(idx);
			}
		} else {
			ImageButton prevVocabulary = (ImageButton)findViewById(R.id.prev_vocabulary);
			ImageButton nextVocabulary = (ImageButton)findViewById(R.id.next_vocabulary);

			prevVocabulary.setAlpha(100);
			nextVocabulary.setAlpha(100);
			prevVocabulary.setOnClickListener(this);
			nextVocabulary.setOnClickListener(this);

			RelativeLayout layout = (RelativeLayout)findViewById(R.id.move_vocabulary_area);
			layout.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
			layout.setVisibility(View.VISIBLE);
		}

		if (jpVocabulary == null) {
			assert false;

			Toast.makeText(this, "단어의 상세 정보를 얻을 수 없습니다.", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		updateDetailVocabularyInfo(jpVocabulary);

		// 제스쳐 감지 객체를 생성한다.
		mGestureScanner = new GestureDetector(this);
	}

	@Override
	protected void onDestroy() {
		setVocabularySeekList(null);
		super.onDestroy();
	}

	private void updateDetailVocabularyInfo(JapanVocabulary jpVocabulary) {
		assert jpVocabulary != null;

		TextView vocabulary = (TextView) findViewById(R.id.vocabulary);
		TextView vocabularyGana = (TextView) findViewById(R.id.vocabulary_gana);
		TextView vocabularyTranslation = (TextView) findViewById(R.id.vocabulary_translation);
		TextView memorizeCompletedInfo1 = (TextView) findViewById(R.id.memorize_completed_info1);
		TextView memorizeCompletedInfo2 = (TextView) findViewById(R.id.memorize_completed_info2);
		TextView registrationDate = (TextView) findViewById(R.id.registration_date);
		TextView vocabularyDetailInfo = (TextView) findViewById(R.id.vocabulary_detail_info);
		TextView vocabularyExample = (TextView) findViewById(R.id.vocabulary_example);

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
		vocabularyExample.setText(Html.fromHtml(JapanVocabularyManager.getInstance().getJapanVocabularyExample(jpVocabulary.getIdx())));
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
		if (mJvListManager.isValid() == true) {
			RelativeLayout layout = (RelativeLayout)findViewById(R.id.move_vocabulary_area);
			if (layout.getVisibility() == View.VISIBLE) {
				ImageButton prevVocabulary = (ImageButton)findViewById(R.id.prev_vocabulary);
				ImageButton nextVocabulary = (ImageButton)findViewById(R.id.next_vocabulary);
				
				Rect r = new Rect();
				int rawX = (int)e.getRawX();
				int rawY = (int)e.getRawY();

				prevVocabulary.getGlobalVisibleRect(r);
				if (r.contains(rawX, rawY) == true) {
					return false;
				}
				
				nextVocabulary.getGlobalVisibleRect(r);
				if (r.contains(rawX, rawY) == true) {
					return false;
				}

				layout.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
				layout.setVisibility(View.INVISIBLE);
			} else {
				layout.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
				layout.setVisibility(View.VISIBLE);
			}

			return true;
		}

		return false;
	}

	@Override
	public void onClick(View v) {
		if (mJvListManager.isValid() == true) {
			SharedPreferences preferences = getSharedPreferences(JvDefines.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
			if (preferences.getBoolean(JvDefines.JV_SPN_VIBRATE_NEXT_VOCABULARY, true) == true) {
				// 진동을 발생시킨다.
				Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(30);				
			}
			
			JapanVocabulary jpVocabulary = null;
			StringBuilder sbErrorMessage = new StringBuilder();

			switch (v.getId()) {
			case R.id.prev_vocabulary:
				jpVocabulary = mJvListManager.previousVocabulary(sbErrorMessage);
				break;

			case R.id.next_vocabulary:
				jpVocabulary = mJvListManager.nextVocabulary(sbErrorMessage);
				break;
			}

			if (jpVocabulary != null) {
				setResult(ACTIVITY_RESULT_POSITION_CHANGED);

				updateDetailVocabularyInfo(jpVocabulary);
			} else if (sbErrorMessage.length() > 0) {
				Toast.makeText(this, sbErrorMessage.toString(), Toast.LENGTH_SHORT).show();
			}
		}
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
