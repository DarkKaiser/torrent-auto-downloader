package kr.co.darkkaiser.jc;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class JapanCharacterActivity extends Activity {

	private boolean mShowYoum = false;
	private boolean mShowHiragana = false;
	private boolean mShowGatakana = false;

	private int mCurrentShowIndex = -1;
	private boolean mIsCurrentShowHiragana = false;

	private Random mRandom = new Random();
	private ArrayList<String> mKorea = null;
	private ArrayList<String> mJapanHiragana = null;
	private ArrayList<String> mJapanGatagana = null;
	
	private SharedPreferences mPreferences = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mPreferences = getSharedPreferences("jc_setup", MODE_PRIVATE);

        TextView character = (TextView)findViewById(R.id.character);
        character.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (v.getId() == R.id.character) {
					if (event.getAction() == MotionEvent.ACTION_UP && mCurrentShowIndex != -1) {
						Dialog dlg = new Dialog(JapanCharacterActivity.this);
						dlg.setContentView(R.layout.jc_description);
						dlg.setTitle("<상세정보>");
						dlg.setCanceledOnTouchOutside(true);
						
						TextView descriptionTv = (TextView)dlg.findViewById(R.id.description);
						if (mIsCurrentShowHiragana == true)
							descriptionTv.setText(String.format("발음 : %s\n가타카나 : %s", mKorea.get(mCurrentShowIndex), mJapanGatagana.get(mCurrentShowIndex)));
						else
							descriptionTv.setText(String.format("발음 : %s\n히라가나 : %s", mKorea.get(mCurrentShowIndex), mJapanHiragana.get(mCurrentShowIndex)));

						dlg.show();
					}
					
					return true;
				}

				return false;			}
		});

        Button nextButton = (Button)findViewById(R.id.nextCharacter);
        nextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showNextCharactor();
				
				// 진동을 발생시킨다.
				Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(10);
			}
		});
        
        // 데이터를 초기화한다.
        mKorea = new ArrayList<String>();
        mJapanHiragana = new ArrayList<String>();
        mJapanGatagana = new ArrayList<String>();
        
        mKorea.add("아[a]");
        mKorea.add("이[i]");
        mKorea.add("우[u]");
        mKorea.add("에[e]");
        mKorea.add("오[o]");
        mKorea.add("카[ka]");
        mKorea.add("키[ki]");
        mKorea.add("쿠[ku]");
        mKorea.add("케[ke]");
        mKorea.add("코[ko]");
        mKorea.add("사[sa]");
        mKorea.add("시[si]");
        mKorea.add("스[su]");
        mKorea.add("세[se]");
        mKorea.add("소[so]");
        mKorea.add("타[ta]");
        mKorea.add("치[chi]");
        mKorea.add("츠[tsu]");
        mKorea.add("테[te]");
        mKorea.add("토[to]");
        mKorea.add("나[na]");
        mKorea.add("니[ni]");
        mKorea.add("누[nu]");
        mKorea.add("네[ne]");
        mKorea.add("노[no]");
        mKorea.add("하[ha]");
        mKorea.add("히[hi]");
        mKorea.add("후[hu]");
        mKorea.add("헤[he]");
        mKorea.add("호[ho]");
        mKorea.add("마[ma]");
        mKorea.add("미[mi]");
        mKorea.add("무[mu]");
        mKorea.add("메[me]");
        mKorea.add("모[mo]");
        mKorea.add("야[ya]");
        mKorea.add("유[yu]");
        mKorea.add("요[yo]");
        mKorea.add("라[ra]");
        mKorea.add("리[ri]");
        mKorea.add("루[ru]");
        mKorea.add("레[re]");
        mKorea.add("로[ro]");
        mKorea.add("와[wa]");
        mKorea.add("오[o]");
        mKorea.add("응[n]");
        mKorea.add("가[ga]");
        mKorea.add("기[gi]");
        mKorea.add("구[gu]");
        mKorea.add("게[ge]");
        mKorea.add("고[go]");
        mKorea.add("자[za]");
        mKorea.add("지[zi]");
        mKorea.add("주[zu]");
        mKorea.add("제[ze]");
        mKorea.add("조[zo]");
        mKorea.add("다[da]");
        mKorea.add("디[di]");
        mKorea.add("두[du]");
        mKorea.add("데[de]");
        mKorea.add("도[do]");
        mKorea.add("바[ba]");
        mKorea.add("비[bi]");
        mKorea.add("부[bu]");
        mKorea.add("베[be]");
        mKorea.add("보[bo]");
        mKorea.add("파[pa]");
        mKorea.add("피[pi]");
        mKorea.add("푸[pu]");
        mKorea.add("페[pe]");
        mKorea.add("포[po]");
        mKorea.add("캬[kya]");
        mKorea.add("큐[kyu]");
        mKorea.add("쿄[kyo]");
        mKorea.add("샤[sya]");
        mKorea.add("슈[syu]");
        mKorea.add("쇼[syo]");
        mKorea.add("챠[cha]");
        mKorea.add("츄[chu]");
        mKorea.add("쵸[cho]");
        mKorea.add("냐[nya]");
        mKorea.add("뉴[nyu]");
        mKorea.add("뇨[nyo]");
        mKorea.add("햐[hya]");
        mKorea.add("휴[hyu]");
        mKorea.add("효[hyo]");
        mKorea.add("먀[mya]");
        mKorea.add("뮤[myu]");
        mKorea.add("묘[myo]");
        mKorea.add("랴[rya]");
        mKorea.add("류[ryu]");
        mKorea.add("료[ryo]");
        mKorea.add("갸[gya]");
        mKorea.add("규[gyu]");
        mKorea.add("교[gyo]");
        mKorea.add("쟈[zya]");
        mKorea.add("쥬[zyu]");
        mKorea.add("죠[zyo]");
        mKorea.add("뱌[bya]");
        mKorea.add("뷰[byu]");
        mKorea.add("뵤[byo]");
        mKorea.add("퍄[pya]");
        mKorea.add("퓨[pyu]");
        mKorea.add("표[pyo]");        

        mJapanHiragana.add("あ");
        mJapanHiragana.add("い");
        mJapanHiragana.add("う");
        mJapanHiragana.add("え");
        mJapanHiragana.add("お");
        mJapanHiragana.add("か");
        mJapanHiragana.add("き");
        mJapanHiragana.add("く");
        mJapanHiragana.add("け");
        mJapanHiragana.add("こ");
        mJapanHiragana.add("さ");
        mJapanHiragana.add("し");
        mJapanHiragana.add("す");
        mJapanHiragana.add("せ");
        mJapanHiragana.add("そ");
        mJapanHiragana.add("た");
        mJapanHiragana.add("ち");
        mJapanHiragana.add("つ");
        mJapanHiragana.add("て");
        mJapanHiragana.add("と");
        mJapanHiragana.add("な");
        mJapanHiragana.add("に");
        mJapanHiragana.add("ぬ");
        mJapanHiragana.add("ね");
        mJapanHiragana.add("の");
        mJapanHiragana.add("は");
        mJapanHiragana.add("ひ");
        mJapanHiragana.add("ふ");
        mJapanHiragana.add("へ");
        mJapanHiragana.add("ほ");
        mJapanHiragana.add("ま");
        mJapanHiragana.add("み");
        mJapanHiragana.add("む");
        mJapanHiragana.add("め");
        mJapanHiragana.add("も");
        mJapanHiragana.add("や");
        mJapanHiragana.add("ゆ");
        mJapanHiragana.add("よ");
        mJapanHiragana.add("ら");
        mJapanHiragana.add("り");
        mJapanHiragana.add("る");
        mJapanHiragana.add("れ");
        mJapanHiragana.add("ろ");
        mJapanHiragana.add("わ");
        mJapanHiragana.add("を");
        mJapanHiragana.add("ん");
        mJapanHiragana.add("が");
        mJapanHiragana.add("ぎ");
        mJapanHiragana.add("ぐ");
        mJapanHiragana.add("げ");
        mJapanHiragana.add("ご");
        mJapanHiragana.add("ざ");
        mJapanHiragana.add("じ");
        mJapanHiragana.add("ず");
        mJapanHiragana.add("ぜ");
        mJapanHiragana.add("ぞ");
        mJapanHiragana.add("だ");
        mJapanHiragana.add("ぢ");
        mJapanHiragana.add("づ");
        mJapanHiragana.add("で");
        mJapanHiragana.add("ど");
        mJapanHiragana.add("ば");
        mJapanHiragana.add("び");
        mJapanHiragana.add("ぶ");
        mJapanHiragana.add("べ");
        mJapanHiragana.add("ぼ");
        mJapanHiragana.add("ぱ");
        mJapanHiragana.add("ぴ");
        mJapanHiragana.add("ぷ");
        mJapanHiragana.add("ぺ");
        mJapanHiragana.add("ぽ");
        mJapanHiragana.add("きゃ");
        mJapanHiragana.add("きゅ");
        mJapanHiragana.add("きょ");
        mJapanHiragana.add("しゃ");
        mJapanHiragana.add("しゅ");
        mJapanHiragana.add("しょ");
        mJapanHiragana.add("ちゃ");
        mJapanHiragana.add("ちゅ");
        mJapanHiragana.add("ちょ");
        mJapanHiragana.add("にゃ");
        mJapanHiragana.add("にゅ");
        mJapanHiragana.add("にょ");
        mJapanHiragana.add("ひゃ");
        mJapanHiragana.add("ひゅ");
        mJapanHiragana.add("ひょ");
        mJapanHiragana.add("みゃ");
        mJapanHiragana.add("みゅ");
        mJapanHiragana.add("みょ");
        mJapanHiragana.add("りゃ");
        mJapanHiragana.add("りゅ");
        mJapanHiragana.add("りょ");
        mJapanHiragana.add("ぎゃ");
        mJapanHiragana.add("ぎゅ");
        mJapanHiragana.add("ぎょ");
        mJapanHiragana.add("じゃ");
        mJapanHiragana.add("じゅ");
        mJapanHiragana.add("じょ");
        mJapanHiragana.add("びゃ");
        mJapanHiragana.add("びゅ");
        mJapanHiragana.add("びょ");
        mJapanHiragana.add("ぴゃ");
        mJapanHiragana.add("ぴゅ");
        mJapanHiragana.add("ぴょ");

        mJapanGatagana.add("ア");
        mJapanGatagana.add("イ");
        mJapanGatagana.add("ウ");
        mJapanGatagana.add("エ");
        mJapanGatagana.add("オ");
        mJapanGatagana.add("カ");
        mJapanGatagana.add("キ");
        mJapanGatagana.add("ク");
        mJapanGatagana.add("ケ");
        mJapanGatagana.add("コ");
        mJapanGatagana.add("サ");
        mJapanGatagana.add("シ");
        mJapanGatagana.add("ス");
        mJapanGatagana.add("セ");
        mJapanGatagana.add("ソ");
        mJapanGatagana.add("タ");
        mJapanGatagana.add("チ");
        mJapanGatagana.add("ツ");
        mJapanGatagana.add("テ");
        mJapanGatagana.add("ト");
        mJapanGatagana.add("ナ");
        mJapanGatagana.add("ニ");
        mJapanGatagana.add("ヌ");
        mJapanGatagana.add("ネ");
        mJapanGatagana.add("ノ");
        mJapanGatagana.add("ハ");
        mJapanGatagana.add("ヒ");
        mJapanGatagana.add("フ");
        mJapanGatagana.add("ヘ");
        mJapanGatagana.add("ホ");
        mJapanGatagana.add("マ");
        mJapanGatagana.add("ミ");
        mJapanGatagana.add("ム");
        mJapanGatagana.add("メ");
        mJapanGatagana.add("モ");
        mJapanGatagana.add("ヤ");
        mJapanGatagana.add("ユ");
        mJapanGatagana.add("ヨ");
        mJapanGatagana.add("ラ");
        mJapanGatagana.add("リ");
        mJapanGatagana.add("ル");
        mJapanGatagana.add("レ");
        mJapanGatagana.add("ロ");
        mJapanGatagana.add("ワ");
        mJapanGatagana.add("ヲ");
        mJapanGatagana.add("ン");
        mJapanGatagana.add("ガ");
        mJapanGatagana.add("ギ");
        mJapanGatagana.add("グ");
        mJapanGatagana.add("ゲ");
        mJapanGatagana.add("ゴ");
        mJapanGatagana.add("ザ");
        mJapanGatagana.add("ジ");
        mJapanGatagana.add("ズ");
        mJapanGatagana.add("ゼ");
        mJapanGatagana.add("ゾ");
        mJapanGatagana.add("ダ");
        mJapanGatagana.add("ヂ");
        mJapanGatagana.add("ヅ");
        mJapanGatagana.add("デ");
        mJapanGatagana.add("ド");
        mJapanGatagana.add("バ");
        mJapanGatagana.add("ビ");
        mJapanGatagana.add("ブ");
        mJapanGatagana.add("ベ");
        mJapanGatagana.add("ボ");
        mJapanGatagana.add("パ");
        mJapanGatagana.add("ピ");
        mJapanGatagana.add("プ");
        mJapanGatagana.add("ペ");
        mJapanGatagana.add("ポ");
        mJapanGatagana.add("キャ");
        mJapanGatagana.add("キュ");
        mJapanGatagana.add("キョ");
        mJapanGatagana.add("シャ");
        mJapanGatagana.add("シュ");
        mJapanGatagana.add("ショ");
        mJapanGatagana.add("チャ");
        mJapanGatagana.add("チュ");
        mJapanGatagana.add("チョ");
        mJapanGatagana.add("ニャ");
        mJapanGatagana.add("ニュ");
        mJapanGatagana.add("ニョ");
        mJapanGatagana.add("ヒャ");
        mJapanGatagana.add("ヒュ");
        mJapanGatagana.add("ヒョ");
        mJapanGatagana.add("ミャ");
        mJapanGatagana.add("ミュ");
        mJapanGatagana.add("ミョ");
        mJapanGatagana.add("リャ");
        mJapanGatagana.add("リュ");
        mJapanGatagana.add("リョ");
        mJapanGatagana.add("ギャ");
        mJapanGatagana.add("ギュ");
        mJapanGatagana.add("ギョ");
        mJapanGatagana.add("ジャ");
        mJapanGatagana.add("ジュ");
        mJapanGatagana.add("ジョ");
        mJapanGatagana.add("ビャ");
        mJapanGatagana.add("ビュ");
        mJapanGatagana.add("ビョ");
        mJapanGatagana.add("ピャ");
        mJapanGatagana.add("ピュ");
        mJapanGatagana.add("ピョ");
        
        // 프로그램이 처음 시작될 때 한자를 보이도록 한다.
        init();
        showNextCharactor();
    }
    
    private void init() {
    	assert mPreferences != null;

    	// 프로그램을 초기화합니다.
    	mShowYoum = mPreferences.getBoolean("chk_youm", false);
    	mShowHiragana = mPreferences.getBoolean("chk_hiragana", false);
    	mShowGatakana = mPreferences.getBoolean("chk_gatakana", false);
    }

    private void showNextCharactor() {
    	if (mShowHiragana == false && mShowGatakana == false) {
    		Toast.makeText(this, "암기 대상 문자가 선택되지 않았습니다. 환경설정 페이지에서 선택하여 주세요!", Toast.LENGTH_LONG).show();
    		return;
    	}

    	TextView character = (TextView)findViewById(R.id.character);

    	if (mShowHiragana == true && mShowGatakana == true) {
    		if (mRandom.nextInt(2) == 0) {
    			if (mShowYoum == true) {
    				mCurrentShowIndex = mRandom.nextInt(104);
    			} else {
    				mCurrentShowIndex = mRandom.nextInt(71);
    			}

    			mIsCurrentShowHiragana = true;
    			character.setText(mJapanHiragana.get(mCurrentShowIndex));
    		} else {
    			if (mShowYoum == true) {
    				mCurrentShowIndex = mRandom.nextInt(104);
    			} else {
    				mCurrentShowIndex = mRandom.nextInt(71);
    			}
    			
    			mIsCurrentShowHiragana = false;
    			character.setText(mJapanGatagana.get(mCurrentShowIndex));
    		}
    	} else if (mShowHiragana == true) {
			if (mShowYoum == true) {
				mCurrentShowIndex = mRandom.nextInt(104);
			} else {
				mCurrentShowIndex = mRandom.nextInt(71);
			}
			
			mIsCurrentShowHiragana = true;
			character.setText(mJapanHiragana.get(mCurrentShowIndex));
    	} else if (mShowGatakana == true) {
			if (mShowYoum == true) {
				mCurrentShowIndex = mRandom.nextInt(104);
			} else {
				mCurrentShowIndex = mRandom.nextInt(71);
			}

			mIsCurrentShowHiragana = false;
			character.setText(mJapanGatagana.get(mCurrentShowIndex));
    	}
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem item = menu.add(0, 1, 0, "환경설정");
		item.setIcon(android.R.drawable.ic_menu_preferences);
		return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			// 설정 페이지를 띄운다.
			startActivityForResult(new Intent(this, SetupActivity.class), 0);

			return true;
		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 0) {
			init();
			showNextCharactor();
		}
	}

}
