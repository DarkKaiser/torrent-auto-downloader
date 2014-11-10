package kr.co.darkkaiser.jv.view.detail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.androidquery.AQuery;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.common.Constants;
import kr.co.darkkaiser.jv.vocabulary.data.JapanVocabulary;
import kr.co.darkkaiser.jv.vocabulary.data.JapanVocabularyManager;
import kr.co.darkkaiser.jv.vocabulary.list.IVocabularyList;
import kr.co.darkkaiser.jv.vocabulary.list.VocabularyListWrapper;

// todo 액션바에 암기완료/암기미완료 토글버튼 추가
// todo 이전/다음 버튼 이미지 심플하게 변경
//@@@@@ todo 이전/다음 버튼 일정 시간지나면 사라지도록
public class DetailActivity extends ActionBarActivity implements OnClickListener {

	// 상세정보 페이지에서 이전/다음 버튼으로 암기단어를 변경하면 호출한 페이지에서도
	// 변경된 단어로 바로 보여주도록 하기 위해 호출자 인텐트로 넘겨 줄 액티비티 결과 값
	public static final int ACTIVITY_RESULT_POSITION_CHANGED = 1;

	private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_MAX_OFF_PATH = 170;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private GestureDetector mGestureDetector = null;

    // 이전/다음 단어로 이동하기 위한 단어리스트 래퍼객체
	private static VocabularyListWrapper mVocabularyListWrapper = new VocabularyListWrapper();

	public static void setVocabularySeekList(IVocabularyList vocabularyList) {
		mVocabularyListWrapper.setVocabularySeekList(vocabularyList);
	}

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_japan_vocabulary_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AQuery aq = new AQuery(this);

        // 상세 정보를 출력할 단어에 대한 정보를 구한다.
		JapanVocabulary vocabulary = null;

		if (mVocabularyListWrapper.isValid() == true)
			vocabulary = mVocabularyListWrapper.getCurrentVocabulary();

		if (vocabulary == null) {
			// 이전/다음 단어로 이동하기 위한 관리자 객체가 유효하지 않으므로 null로 설정한다.
			setVocabularySeekList(null);

			Intent intent = getIntent();
			if (intent != null) {
				long idx = intent.getLongExtra("idx", -1);

				if (idx != -1)
					vocabulary = JapanVocabularyManager.getInstance().getJapanVocabulary(idx);
			}
		} else {
            aq.id(R.id.move_vocabulary_area).visible();
            aq.id(R.id.prev_vocabulary).clicked(this, "onClick");
            aq.id(R.id.next_vocabulary).clicked(this, "onClick");
		}

		if (vocabulary == null) {
			assert false;

			Toast.makeText(this, "단어의 상세 정보를 얻을 수 없습니다.", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		updateVocabularyDetailInfo(vocabulary);

		// 제스쳐 감지 객체를 생성한다.
        mGestureDetector = new GestureDetector(DetailActivity.this, simpleOnGestureListener);

        // 스크롤뷰에 제스처를 등록한다.
        aq.id(R.id.ddd).getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });
	}

	@Override
	protected void onDestroy() {
		setVocabularySeekList(null);
		super.onDestroy();
	}

	private void updateVocabularyDetailInfo(JapanVocabulary vocabulary) {
		assert vocabulary != null;

        AQuery aq = new AQuery(this);
        JapanVocabularyManager vm = JapanVocabularyManager.getInstance();

        aq.id(R.id.vocabulary).text(vocabulary.getVocabulary());
        aq.id(R.id.vocabulary_gana).text(vocabulary.getVocabularyGana());
        aq.id(R.id.vocabulary_translation).text(vocabulary.getVocabularyTranslation());
        aq.id(R.id.vocabulary_detail_info).text(vm.getVocabularyDetailDescription(vocabulary));
        aq.id(R.id.vocabulary_example).text(Html.fromHtml(vm.getVocabularyExample(vocabulary)));

		long memorizeCompletedCount = vocabulary.getMemorizeCompletedCount();
		if (vocabulary.isMemorizeCompleted() == true) {
			assert memorizeCompletedCount > 0;
            aq.id(R.id.memorize_uncompleted_text).gone();
		} else {
            aq.id(R.id.memorize_uncompleted_text).visible();
		}
        aq.id(R.id.memorize_completed_count_text).text(String.format("총 %d회 암기완료", memorizeCompletedCount));
	}

    @Override
    public void onClick(View v) {
        if (mVocabularyListWrapper.isValid() == true) {
            SharedPreferences preferences = getSharedPreferences(Constants.JV_SHARED_PREFERENCE_NAME, MODE_PRIVATE);
            if (preferences.getBoolean(Constants.JV_SPN_VIBRATE_NEXT_VOCABULARY, true) == true) {
                // 진동을 발생시킨다.
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(30);
            }

            JapanVocabulary vocabulary = null;
            StringBuilder sbErrMessage = new StringBuilder();

            switch (v.getId()) {
            case R.id.prev_vocabulary:
                vocabulary = mVocabularyListWrapper.previousVocabulary(sbErrMessage);
                break;

            case R.id.next_vocabulary:
                vocabulary = mVocabularyListWrapper.nextVocabulary(sbErrMessage);
                break;
            }

            if (vocabulary != null) {
                setResult(ACTIVITY_RESULT_POSITION_CHANGED);
                updateVocabularyDetailInfo(vocabulary);
            } else if (sbErrMessage.length() > 0) {
                Toast.makeText(this, sbErrMessage.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;

                // right to left swipe
                /*
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
                }
                */

                // left to right swipe
                if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    finish();
                }

                // down to up swipe
                /*
                else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    Toast.makeText(getApplicationContext(), "Swipe up", Toast.LENGTH_SHORT).show();
                }
                */

                // up to down swipe
                /*
                else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    Toast.makeText(getApplicationContext(), "Swipe down", Toast.LENGTH_SHORT).show();
                }
                */
            } catch (Exception ignored) {
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mVocabularyListWrapper.isValid() == true) {
                AQuery aq = new AQuery(DetailActivity.this);

                if (aq.id(R.id.move_vocabulary_area).getView().getVisibility() == View.VISIBLE) {
                    int rawX = (int)e.getRawX();
                    int rawY = (int)e.getRawY();

                    Rect prevVocabularyRect = new Rect();
                    Rect nextVocabularyRect = new Rect();
                    aq.id(R.id.prev_vocabulary).getView().getGlobalVisibleRect(prevVocabularyRect);
                    aq.id(R.id.next_vocabulary).getView().getGlobalVisibleRect(nextVocabularyRect);
                    if (prevVocabularyRect.contains(rawX, rawY) == true || nextVocabularyRect.contains(rawX, rawY) == true)
                        return false;

                    aq.id(R.id.move_vocabulary_area).animate(AnimationUtils.loadAnimation(DetailActivity.this, android.R.anim.fade_out)).invisible();
                } else {
                    aq.id(R.id.move_vocabulary_area).animate(AnimationUtils.loadAnimation(DetailActivity.this, android.R.anim.fade_in)).visible();
                }

                return true;
            }

            return super.onSingleTapConfirmed(e);
        }

    };

}
