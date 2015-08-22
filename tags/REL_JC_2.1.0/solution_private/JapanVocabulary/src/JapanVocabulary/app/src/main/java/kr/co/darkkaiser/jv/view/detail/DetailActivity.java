package kr.co.darkkaiser.jv.view.detail;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.androidquery.AQuery;

import kr.co.darkkaiser.jv.R;
import kr.co.darkkaiser.jv.common.Constants;
import kr.co.darkkaiser.jv.vocabulary.data.Vocabulary;
import kr.co.darkkaiser.jv.vocabulary.data.VocabularyManager;
import kr.co.darkkaiser.jv.vocabulary.list.VocabularyListSeek;

public class DetailActivity extends ActionBarActivity implements OnClickListener {

    private static final String TAG = "DetailActivity";

    public static final int ACTIVITY_RESULT_DATA_CHANGED = 1;
	public static final int ACTIVITY_RESULT_POSITION_CHANGED = 2;

	private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_MAX_OFF_PATH = 170;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private GestureDetector gestureDetector = null;

    // 이전/다음 단어로 이동하기 위한 단어리스트 래퍼객체
	private static VocabularyListSeek vocabularyListSeek = null;

    // 이전/다음 버튼이 화면에 나타나고 나서 일정시간 이후에 버튼을 자동으로 숨기기 위한 핸들러
    private Handler prevNextVocabularyButtonInVisibleHandler = new Handler();

    // 이전/다음 버튼이 화면에 나타나고 나서 자동으로 숨겨지기까지의 시간
    private static final int PREV_NEXT_VOCABULARY_BUTTON_INVISIBLE_MILLISECOND = 1500;

    // 단어 상세정보 및 예문 데이터 로딩의 비동기 태스크
    private LoadVocabularyDataAsyncTask loadVocabularyDataAsyncTask = null;

    private int activityResultCode = 0;

    public static int setVocabularyListSeek(VocabularyListSeek vocabularyListSeek) {
        Log.d(TAG, "set VocabularyListSeek : " + vocabularyListSeek);

        int position = -1;
        if (DetailActivity.vocabularyListSeek != null)
            position = DetailActivity.vocabularyListSeek.getPosition();

        DetailActivity.vocabularyListSeek = vocabularyListSeek;

        return position;
	}

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vocabulary_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AQuery aq = new AQuery(this);

        // 상세 정보를 출력할 단어에 대한 정보를 구한다.
		Vocabulary vocabulary = null;
		if (vocabularyListSeek != null && vocabularyListSeek.isValid() == true)
			vocabulary = vocabularyListSeek.getVocabulary();

		if (vocabulary == null) {
			// 이전/다음 단어로 이동하기 위한 관리자 객체가 유효하지 않으므로 null로 설정한다.
			setVocabularyListSeek(null);
		} else if (vocabularyListSeek.canSeek() == true) {
            aq.id(R.id.avd_move_vocabulary_button_panel).visible();
            aq.id(R.id.avd_prev_vocabulary).clicked(this, "onClick");
            aq.id(R.id.avd_next_vocabulary).clicked(this, "onClick");

            // 상세정보 페이지가 열릴 때, 이전/다음 버튼이 바로 화면에 보이는 상태이므로, 일정시간 이후에 버튼을 자동으로 숨겨지도록 설정한다.
            this.prevNextVocabularyButtonInVisibleHandler.postDelayed(mPrevNextVocabularyButtonVisibleRunnable, PREV_NEXT_VOCABULARY_BUTTON_INVISIBLE_MILLISECOND);
		}

		if (vocabulary == null) {
			assert false;

			Toast.makeText(this, getString(R.string.avd_nofind_vocabulary_detail_info), Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		updateVocabularyDetailInfo(vocabulary);

		// 제스쳐 감지 객체를 생성한다.
        this.gestureDetector = new GestureDetector(DetailActivity.this, simpleOnGestureListener);

        // 스크롤뷰에 제스처를 등록한다.
        aq.id(R.id.avd_vocabulary_detail_info_panel).getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

	@Override
	protected void onDestroy() {
        if (this.loadVocabularyDataAsyncTask != null)
            this.loadVocabularyDataAsyncTask.cancel(true);

        this.prevNextVocabularyButtonInVisibleHandler.removeCallbacks(mPrevNextVocabularyButtonVisibleRunnable);
        setVocabularyListSeek(null);
		super.onDestroy();
	}

	private void updateVocabularyDetailInfo(Vocabulary vocabulary) {
		assert vocabulary != null;

        Log.d(TAG, "updateVocabularyDetailInfo : " + vocabulary.getVocabulary());

        AQuery aq = new AQuery(this);
        aq.id(R.id.avd_vocabulary).text(vocabulary.getVocabulary());
        aq.id(R.id.avd_vocabulary_gana).text(vocabulary.getVocabularyGana());
        aq.id(R.id.avd_vocabulary_translation).text(vocabulary.getVocabularyTranslation());
        aq.id(R.id.avd_vocabulary_detail_description).text("");
        aq.id(R.id.avd_vocabulary_example).text("");

        updateVocabularyDetailMemorizeInfo(vocabulary);

        // 단어 상세정보 및 예문은 불러들이는 데 오래 걸리므로 비동기로 처리한다.
        if (this.loadVocabularyDataAsyncTask != null)
            this.loadVocabularyDataAsyncTask.cancel(true);

        this.loadVocabularyDataAsyncTask = new LoadVocabularyDataAsyncTask();
        this.loadVocabularyDataAsyncTask.execute();
    }

    private void updateVocabularyDetailMemorizeInfo(Vocabulary vocabulary) {
        assert vocabulary != null;

        AQuery aq = new AQuery(this);

        if (vocabulary.isMemorizeTarget() == true)
            aq.id(R.id.avd_memorize_target).text(getString(R.string.avd_vocabulary_memorize_target)).textColor(getResources().getColor(R.color.avd_vocabulary_memorize_target));
        else
            aq.id(R.id.avd_memorize_target).text(getString(R.string.avd_vocabulary_memorize_untarget)).textColor(getResources().getColor(R.color.avd_vocabulary_memorize_untarget));

        long memorizeCompletedCount = vocabulary.getMemorizeCompletedCount();
        if (vocabulary.isMemorizeCompleted() == true) {
            assert memorizeCompletedCount > 0;
            aq.id(R.id.avd_memorize_completed_info).text(String.format("%s(암기완료 %d회)", getString(R.string.avd_vocabulary_memorize_completed), memorizeCompletedCount)).textColor(getResources().getColor(R.color.avd_vocabulary_memorize_completed_count));
        } else {
            aq.id(R.id.avd_memorize_completed_info).text(String.format("%s(암기완료 %d회)", getString(R.string.avd_vocabulary_memorize_uncompleted), memorizeCompletedCount)).textColor(getResources().getColor(R.color.avd_vocabulary_memorize_uncompleted_count));
        }
    }

    @Override
    public void onClick(View v) {
        if (vocabularyListSeek != null && vocabularyListSeek.isValid() == true && vocabularyListSeek.canSeek() == true) {
            resetInvisiblePrevNextVocabularyButtonAnimate();

            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
            if (sharedPreferences.getBoolean(getString(R.string.as_vibrate_on_next_vocabulary_key), getResources().getBoolean(R.bool.vibrate_next_vocabulary_default_value)) == true) {
                // 진동을 발생시킨다.
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(30);
            }

            Vocabulary vocabulary = null;
            StringBuilder sbErrMessage = new StringBuilder();

            switch (v.getId()) {
                case R.id.avd_prev_vocabulary:
                    vocabulary = vocabularyListSeek.previousVocabulary(sbErrMessage);
                    break;

                case R.id.avd_next_vocabulary:
                    vocabulary = vocabularyListSeek.nextVocabulary(sbErrMessage);
                    break;
            }

            if (vocabulary != null) {
                this.activityResultCode |= ACTIVITY_RESULT_POSITION_CHANGED;
                setResult(this.activityResultCode);

                updateVocabularyDetailInfo(vocabulary);

                // 옵션메뉴의 항목을 다시 그린다.
                supportInvalidateOptionsMenu();
            } else if (sbErrMessage.length() > 0) {
                Toast.makeText(this, sbErrMessage.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_vocabulary_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        Vocabulary vocabulary = vocabularyListSeek.getVocabulary();
        if (vocabulary != null) {
            menu.findItem(R.id.avd_vocabulary_rememorize).setVisible(vocabulary.isMemorizeCompleted() == true);
            menu.findItem(R.id.avd_vocabulary_memorize_completed).setVisible(vocabulary.isMemorizeCompleted() == false);
            menu.findItem(R.id.avd_add_vocabulary_memorize_target).setVisible(vocabulary.isMemorizeTarget() == false);
            menu.findItem(R.id.avd_remove_vocabulary_memorize_target).setVisible(vocabulary.isMemorizeTarget() == true);
        } else {
            menu.findItem(R.id.avd_vocabulary_rememorize).setVisible(false);
            menu.findItem(R.id.avd_vocabulary_memorize_completed).setVisible(false);
            menu.findItem(R.id.avd_add_vocabulary_memorize_target).setVisible(false);
            menu.findItem(R.id.avd_remove_vocabulary_memorize_target).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        assert vocabularyListSeek != null;

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.avd_vocabulary_rememorize:
                vocabularyListSeek.setMemorizeCompleted(false);
                break;

            case R.id.avd_vocabulary_memorize_completed:
                vocabularyListSeek.setMemorizeCompleted(true);
                break;

            case R.id.avd_add_vocabulary_memorize_target:
                vocabularyListSeek.setMemorizeTarget(true);
                break;

            case R.id.avd_remove_vocabulary_memorize_target:
                vocabularyListSeek.setMemorizeTarget(false);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        this.activityResultCode |= ACTIVITY_RESULT_DATA_CHANGED;
        setResult(this.activityResultCode);

        Vocabulary vocabulary = vocabularyListSeek.getVocabulary();
        if (vocabulary != null)
            updateVocabularyDetailMemorizeInfo(vocabulary);

        return true;
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
        public boolean onDown(MotionEvent e) {
            resetInvisiblePrevNextVocabularyButtonAnimate();

            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            resetInvisiblePrevNextVocabularyButtonAnimate();

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (vocabularyListSeek != null && vocabularyListSeek.isValid() == true && vocabularyListSeek.canSeek() == true) {
                AQuery aq = new AQuery(DetailActivity.this);

                if (aq.id(R.id.avd_move_vocabulary_button_panel).getView().getVisibility() == View.VISIBLE) {
                    int rawX = (int)e.getRawX();
                    int rawY = (int)e.getRawY();

                    Rect prevVocabularyRect = new Rect();
                    Rect nextVocabularyRect = new Rect();
                    aq.id(R.id.avd_prev_vocabulary).getView().getGlobalVisibleRect(prevVocabularyRect);
                    aq.id(R.id.avd_next_vocabulary).getView().getGlobalVisibleRect(nextVocabularyRect);
                    if (prevVocabularyRect.contains(rawX, rawY) == true || nextVocabularyRect.contains(rawX, rawY) == true)
                        return false;
                }

                return true;
            }

            return super.onSingleTapConfirmed(e);
        }
    };

    private Runnable mPrevNextVocabularyButtonVisibleRunnable = new Runnable() {
        @Override
        public void run() {
            AQuery aq = new AQuery(DetailActivity.this);
            if (aq.id(R.id.avd_move_vocabulary_button_panel).getView().getVisibility() == View.VISIBLE) {
                aq.id(R.id.avd_move_vocabulary_button_panel).animate(AnimationUtils.loadAnimation(DetailActivity.this, android.R.anim.fade_out)).invisible();
            }
        }
    };

    private void resetInvisiblePrevNextVocabularyButtonAnimate() {
        if (vocabularyListSeek != null && vocabularyListSeek.isValid() && vocabularyListSeek.canSeek() == true) {
            AQuery aq = new AQuery(DetailActivity.this);
            if (aq.id(R.id.avd_move_vocabulary_button_panel).getView().getVisibility() == View.INVISIBLE) {
                // 이전/다음 버튼이 숨겨지고 있는 도중에 클릭 된 거라면, 애니메이션 효과를 중지하고 화면에 나타나도록 한다.
                aq.id(R.id.avd_move_vocabulary_button_panel).getView().clearAnimation();
                aq.id(R.id.avd_move_vocabulary_button_panel).visible();
            }
        }

        // 일정시간이후에 이전/다음 버튼이 자동으로 숨겨지도록 한다.
        this.prevNextVocabularyButtonInVisibleHandler.removeCallbacks(mPrevNextVocabularyButtonVisibleRunnable);
        this.prevNextVocabularyButtonInVisibleHandler.postDelayed(mPrevNextVocabularyButtonVisibleRunnable, PREV_NEXT_VOCABULARY_BUTTON_INVISIBLE_MILLISECOND);
    }

    private class LoadVocabularyDataAsyncTask extends AsyncTask<Void, Void, Void> {

        private String mVocabularyExample = "";
        private String mVocabularyDetailDescription = "";

        @Override
        protected void onPreExecute() {
            AQuery aq = new AQuery(DetailActivity.this);

            aq.id(R.id.avd_vocabulary_detail_description).text("");
            aq.id(R.id.avd_vocabulary_example).text("");

            aq.id(R.id.avd_vocabulary_detail_info_progress).visibility(View.VISIBLE);
            aq.id(R.id.avd_vocabulary_example_progress).visibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            assert vocabularyListSeek != null;

            Vocabulary vocabulary = vocabularyListSeek.getVocabulary();
            if (vocabulary != null) {
                if (isCancelled() == false)
                    mVocabularyDetailDescription = VocabularyManager.getInstance().getVocabularyDetailDescription(vocabulary);

                if (isCancelled() == false)
                    mVocabularyExample = VocabularyManager.getInstance().getVocabularyExample(vocabulary);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            AQuery aq = new AQuery(DetailActivity.this);

            aq.id(R.id.avd_vocabulary_detail_info_progress).visibility(View.GONE);
            aq.id(R.id.avd_vocabulary_example_progress).visibility(View.GONE);

            aq.id(R.id.avd_vocabulary_detail_description).text(mVocabularyDetailDescription);
            aq.id(R.id.avd_vocabulary_example).text(Html.fromHtml(mVocabularyExample));
        }
    }

}
