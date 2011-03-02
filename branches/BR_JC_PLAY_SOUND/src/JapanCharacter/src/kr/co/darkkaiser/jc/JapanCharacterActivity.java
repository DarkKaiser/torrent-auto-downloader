package kr.co.darkkaiser.jc;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class JapanCharacterActivity extends Activity {

	private boolean mShowYoum = false;
	private boolean mShowHiragana = false;
	private boolean mShowGatakana = false;
	private boolean mVibrateNextCharacter = true;

	private int mCurrentShowIndex = -1;
	private boolean mIsCurrentShowHiragana = false;

	private Random mRandom = new Random();
	private ArrayList<String> mKorea = null;
	private ArrayList<String> mJapanHiragana = null;
	private ArrayList<String> mJapanGatagana = null;
	private ArrayList<JapanCharacterSound> mJapanSoundPool = null;

	private SoundPool mSoundPool = null;
	private SharedPreferences mPreferences = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mPreferences = getSharedPreferences("jc_setup", MODE_PRIVATE);
        
        RelativeLayout characterContainer = (RelativeLayout)findViewById(R.id.character_container);
        characterContainer.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (v.getId() == R.id.character_container) {
					if (event.getAction() == MotionEvent.ACTION_UP && mCurrentShowIndex != -1) {
						Dialog dlg = new Dialog(JapanCharacterActivity.this, R.style.NoTitleDialog);
						dlg.setContentView(R.layout.jc_description);
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
				
				if (mVibrateNextCharacter == true) {
					// 진동을 발생시킨다.
					Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(10);
				}
			}
		});
        
        Button playSoundButton = (Button)findViewById(R.id.playSound);
        playSoundButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				JapanCharacterSound jcSound = mJapanSoundPool.get(mCurrentShowIndex);
				if (jcSound != null) {
					if (jcSound.isLoad() == false) {
						jcSound.load(getApplicationContext(), mSoundPool);
					}
					
					assert jcSound.isLoad() == true;
					
					try {
						int loopCount = 0, result = 0;

						// 사운드가 로드되는 데 시간이 약간 필요하므로 아래와 같이 로드가 완료되어 재생이 올바르게 되었는지 체크한다.
						while (loopCount < 10) {
							result = mSoundPool.play(jcSound.getSoundId(), 1f, 1f, 0, 0, 1f);
							if (result != 0)
								break;
							
							++loopCount;
							Thread.sleep(50);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
        
        // 데이터를 초기화한다.
        mKorea = new ArrayList<String>();
        mJapanHiragana = new ArrayList<String>();
        mJapanGatagana = new ArrayList<String>();
        mJapanSoundPool = new ArrayList<JapanCharacterSound>();

        mKorea.add("아[a]");		mJapanHiragana.add("あ");	mJapanGatagana.add("ア");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.a));
        mKorea.add("이[i]");		mJapanHiragana.add("い");	mJapanGatagana.add("イ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.i));
        mKorea.add("우[u]");		mJapanHiragana.add("う");	mJapanGatagana.add("ウ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.u));
        mKorea.add("에[e]");		mJapanHiragana.add("え");	mJapanGatagana.add("エ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.e));
        mKorea.add("오[o]");		mJapanHiragana.add("お");	mJapanGatagana.add("オ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.o));
        mKorea.add("카[ka]");	mJapanHiragana.add("か");	mJapanGatagana.add("カ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ka));
        mKorea.add("키[ki]");	mJapanHiragana.add("き");	mJapanGatagana.add("キ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ki));
        mKorea.add("쿠[ku]");	mJapanHiragana.add("く");	mJapanGatagana.add("ク");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ku));
        mKorea.add("케[ke]");	mJapanHiragana.add("け");	mJapanGatagana.add("ケ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ke));
        mKorea.add("코[ko]");	mJapanHiragana.add("こ");	mJapanGatagana.add("コ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ko));
        mKorea.add("사[sa]");	mJapanHiragana.add("さ");	mJapanGatagana.add("サ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.sa));
        mKorea.add("시[si]");	mJapanHiragana.add("し");	mJapanGatagana.add("シ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.si));
        mKorea.add("스[su]");	mJapanHiragana.add("す");	mJapanGatagana.add("ス");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.su));
        mKorea.add("세[se]");	mJapanHiragana.add("せ");	mJapanGatagana.add("セ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.se));
        mKorea.add("소[so]");	mJapanHiragana.add("そ");	mJapanGatagana.add("ソ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.so));
        mKorea.add("타[ta]");	mJapanHiragana.add("た");	mJapanGatagana.add("タ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ta));
        mKorea.add("치[chi]");	mJapanHiragana.add("ち");	mJapanGatagana.add("チ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.chi));
        mKorea.add("츠[tsu]");	mJapanHiragana.add("つ");	mJapanGatagana.add("ツ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.tsu));
        mKorea.add("테[te]");	mJapanHiragana.add("て");	mJapanGatagana.add("テ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.te));
        mKorea.add("토[to]");	mJapanHiragana.add("と");	mJapanGatagana.add("ト");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.to));
        mKorea.add("나[na]");	mJapanHiragana.add("な");	mJapanGatagana.add("ナ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.na));
        mKorea.add("니[ni]");	mJapanHiragana.add("に");	mJapanGatagana.add("ニ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ni));
        mKorea.add("누[nu]");	mJapanHiragana.add("ぬ");	mJapanGatagana.add("ヌ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.nu));
        mKorea.add("네[ne]");	mJapanHiragana.add("ね");	mJapanGatagana.add("ネ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ne));
        mKorea.add("노[no]");	mJapanHiragana.add("の");	mJapanGatagana.add("ノ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.no));
        mKorea.add("하[ha]");	mJapanHiragana.add("は");	mJapanGatagana.add("ハ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ha));
        mKorea.add("히[hi]");	mJapanHiragana.add("ひ");	mJapanGatagana.add("ヒ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.hi));
        mKorea.add("후[hu]");	mJapanHiragana.add("ふ");	mJapanGatagana.add("フ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.hu));
        mKorea.add("헤[he]");	mJapanHiragana.add("へ");	mJapanGatagana.add("ヘ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.he));
        mKorea.add("호[ho]");	mJapanHiragana.add("ほ");	mJapanGatagana.add("ホ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ho));
        mKorea.add("마[ma]");	mJapanHiragana.add("ま");	mJapanGatagana.add("マ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ma));
        mKorea.add("미[mi]");	mJapanHiragana.add("み");	mJapanGatagana.add("ミ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.mi));
        mKorea.add("무[mu]");	mJapanHiragana.add("む");	mJapanGatagana.add("ム");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.mu));
        mKorea.add("메[me]");	mJapanHiragana.add("め");	mJapanGatagana.add("メ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.me));
        mKorea.add("모[mo]");	mJapanHiragana.add("も");	mJapanGatagana.add("モ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.mo));
        mKorea.add("야[ya]");	mJapanHiragana.add("や");	mJapanGatagana.add("ヤ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ya));
        mKorea.add("유[yu]");	mJapanHiragana.add("ゆ");	mJapanGatagana.add("ユ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.yu));
        mKorea.add("요[yo]");	mJapanHiragana.add("よ");	mJapanGatagana.add("ヨ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.yo));
        mKorea.add("라[ra]");	mJapanHiragana.add("ら");	mJapanGatagana.add("ラ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ra));
        mKorea.add("리[ri]");	mJapanHiragana.add("り");	mJapanGatagana.add("リ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ri));
        mKorea.add("루[ru]");	mJapanHiragana.add("る");	mJapanGatagana.add("ル");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ru));
        mKorea.add("레[re]");	mJapanHiragana.add("れ");	mJapanGatagana.add("レ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.re));
        mKorea.add("로[ro]");	mJapanHiragana.add("ろ");	mJapanGatagana.add("ロ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ro));
        mKorea.add("와[wa]");	mJapanHiragana.add("わ");	mJapanGatagana.add("ワ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.wa));
        mKorea.add("오[o]");		mJapanHiragana.add("を");	mJapanGatagana.add("ヲ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.o2));
        mKorea.add("응[n]");		mJapanHiragana.add("ん");	mJapanGatagana.add("ン");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.n));
        mKorea.add("가[ga]");	mJapanHiragana.add("が");	mJapanGatagana.add("ガ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ga));
        mKorea.add("기[gi]");	mJapanHiragana.add("ぎ");	mJapanGatagana.add("ギ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.gi));
        mKorea.add("구[gu]");	mJapanHiragana.add("ぐ");	mJapanGatagana.add("グ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.gu));
        mKorea.add("게[ge]");	mJapanHiragana.add("げ");	mJapanGatagana.add("ゲ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ge));
        mKorea.add("고[go]");	mJapanHiragana.add("ご");	mJapanGatagana.add("ゴ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.go));
        mKorea.add("자[za]");	mJapanHiragana.add("ざ");	mJapanGatagana.add("ザ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.za));
        mKorea.add("지[zi]");	mJapanHiragana.add("じ");	mJapanGatagana.add("ジ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.zi));
        mKorea.add("주[zu]");	mJapanHiragana.add("ず");	mJapanGatagana.add("ズ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.zu));
        mKorea.add("제[ze]");	mJapanHiragana.add("ぜ");	mJapanGatagana.add("ゼ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ze));
        mKorea.add("조[zo]");	mJapanHiragana.add("ぞ");	mJapanGatagana.add("ゾ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.zo));
        mKorea.add("다[da]");	mJapanHiragana.add("だ");	mJapanGatagana.add("ダ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.da));
        mKorea.add("디[di]");	mJapanHiragana.add("ぢ");	mJapanGatagana.add("ヂ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.di));
        mKorea.add("두[du]");	mJapanHiragana.add("づ");	mJapanGatagana.add("ヅ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.du));
        mKorea.add("데[de]");	mJapanHiragana.add("で");	mJapanGatagana.add("デ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.de));
        mKorea.add("도[do]");	mJapanHiragana.add("ど");	mJapanGatagana.add("ド");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.do2));
        mKorea.add("바[ba]");	mJapanHiragana.add("ば");	mJapanGatagana.add("バ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ba));
        mKorea.add("비[bi]");	mJapanHiragana.add("び");	mJapanGatagana.add("ビ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.bi));
        mKorea.add("부[bu]");	mJapanHiragana.add("ぶ");	mJapanGatagana.add("ブ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.bu));
        mKorea.add("베[be]");	mJapanHiragana.add("べ");	mJapanGatagana.add("ベ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.be));
        mKorea.add("보[bo]");	mJapanHiragana.add("ぼ");	mJapanGatagana.add("ボ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.bo));
        mKorea.add("파[pa]");	mJapanHiragana.add("ぱ");	mJapanGatagana.add("パ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.pa));
        mKorea.add("피[pi]");	mJapanHiragana.add("ぴ");	mJapanGatagana.add("ピ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.pi));
        mKorea.add("푸[pu]");	mJapanHiragana.add("ぷ");	mJapanGatagana.add("プ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.pu));
        mKorea.add("페[pe]");	mJapanHiragana.add("ぺ");	mJapanGatagana.add("ペ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.pe));
        mKorea.add("포[po]");	mJapanHiragana.add("ぽ");	mJapanGatagana.add("ポ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.po));
        mKorea.add("캬[kya]");	mJapanHiragana.add("きゃ");	mJapanGatagana.add("キャ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.kya));
        mKorea.add("큐[kyu]");	mJapanHiragana.add("きゅ");	mJapanGatagana.add("キュ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.kyu));
        mKorea.add("쿄[kyo]");	mJapanHiragana.add("きょ");	mJapanGatagana.add("キョ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.kyo));
        mKorea.add("샤[sya]");	mJapanHiragana.add("しゃ");	mJapanGatagana.add("シャ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.sya));
        mKorea.add("슈[syu]");	mJapanHiragana.add("しゅ");	mJapanGatagana.add("シュ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.syu));
        mKorea.add("쇼[syo]");	mJapanHiragana.add("しょ");	mJapanGatagana.add("ショ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.syo));
        mKorea.add("챠[cha]");	mJapanHiragana.add("ちゃ");	mJapanGatagana.add("チャ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.cha));
        mKorea.add("츄[chu]");	mJapanHiragana.add("ちゅ");	mJapanGatagana.add("チュ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.chu));
        mKorea.add("쵸[cho]");	mJapanHiragana.add("ちょ");	mJapanGatagana.add("チョ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.cho));
        mKorea.add("냐[nya]");	mJapanHiragana.add("にゃ");	mJapanGatagana.add("ニャ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.nya));
        mKorea.add("뉴[nyu]");	mJapanHiragana.add("にゅ");	mJapanGatagana.add("ニュ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.nyu));
        mKorea.add("뇨[nyo]");	mJapanHiragana.add("にょ");	mJapanGatagana.add("ニョ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.nyo));
        mKorea.add("햐[hya]");	mJapanHiragana.add("ひゃ");	mJapanGatagana.add("ヒャ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.hya));
        mKorea.add("휴[hyu]");	mJapanHiragana.add("ひゅ");	mJapanGatagana.add("ヒュ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.hyu));
        mKorea.add("효[hyo]");	mJapanHiragana.add("ひょ");	mJapanGatagana.add("ヒョ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.hyo));
        mKorea.add("먀[mya]");	mJapanHiragana.add("みゃ");	mJapanGatagana.add("ミャ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.mya));
        mKorea.add("뮤[myu]");	mJapanHiragana.add("みゅ");	mJapanGatagana.add("ミュ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.myu));
        mKorea.add("묘[myo]");	mJapanHiragana.add("みょ");	mJapanGatagana.add("ミョ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.myo));
        mKorea.add("랴[rya]");	mJapanHiragana.add("りゃ");	mJapanGatagana.add("リャ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.rya));
        mKorea.add("류[ryu]");	mJapanHiragana.add("りゅ");	mJapanGatagana.add("リュ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ryu));
        mKorea.add("료[ryo]");	mJapanHiragana.add("りょ");	mJapanGatagana.add("リョ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ryo));
        mKorea.add("갸[gya]");	mJapanHiragana.add("ぎゃ");	mJapanGatagana.add("ギャ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.gya));
        mKorea.add("규[gyu]");	mJapanHiragana.add("ぎゅ");	mJapanGatagana.add("ギュ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.gyu));
        mKorea.add("교[gyo]");	mJapanHiragana.add("ぎょ");	mJapanGatagana.add("ギョ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.gyo));
        mKorea.add("쟈[zya]");	mJapanHiragana.add("じゃ");	mJapanGatagana.add("ジャ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.zya));
        mKorea.add("쥬[zyu]");	mJapanHiragana.add("じゅ");	mJapanGatagana.add("ジュ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.zyu));
        mKorea.add("죠[zyo]");	mJapanHiragana.add("じょ");	mJapanGatagana.add("ジョ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.zyo));
        mKorea.add("뱌[bya]");	mJapanHiragana.add("びゃ");	mJapanGatagana.add("ビャ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.bya));
        mKorea.add("뷰[byu]");	mJapanHiragana.add("びゅ");	mJapanGatagana.add("ビュ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.byu));
        mKorea.add("뵤[byo]");	mJapanHiragana.add("びょ");	mJapanGatagana.add("ビョ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.byo));
        mKorea.add("퍄[pya]");	mJapanHiragana.add("ぴゃ");	mJapanGatagana.add("ピャ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.pya));
        mKorea.add("퓨[pyu]");	mJapanHiragana.add("ぴゅ");	mJapanGatagana.add("ピュ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.pyu));
        mKorea.add("표[pyo]");	mJapanHiragana.add("ぴょ");	mJapanGatagana.add("ピョ");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.pyo));

        // 프로그램이 처음 시작될 때 한자를 보이도록 한다.
        init();
        showNextCharactor();
    }
    
    private void init() {
    	assert mPreferences != null;

    	// 프로그램을 초기화합니다.
    	mShowYoum = mPreferences.getBoolean("chk_youm", true);
    	mShowHiragana = mPreferences.getBoolean("chk_hiragana", true);
    	mShowGatakana = mPreferences.getBoolean("chk_gatakana", true);
    	mVibrateNextCharacter = mPreferences.getBoolean("vibrate_next_character", true);

    	TextView characterMean = (TextView)findViewById(R.id.character_mean);
    	if (mPreferences.getBoolean("show_character_mean", false) == true) {
        	characterMean.setVisibility(View.VISIBLE);
    	} else {
        	characterMean.setVisibility(View.GONE);
    	}
    }

    private void showNextCharactor() {
    	if (mShowHiragana == false && mShowGatakana == false) {
    		Toast.makeText(this, "암기 대상 문자가 선택되지 않았습니다. 환경설정 페이지에서 선택하여 주세요!", Toast.LENGTH_LONG).show();
    		return;
    	}

    	TextView character = (TextView)findViewById(R.id.character);
    	TextView characterMean = (TextView)findViewById(R.id.character_mean);

    	if (mShowHiragana == true && mShowGatakana == true) {
    		if (mRandom.nextInt(2) == 0) {
    			if (mShowYoum == true) {
    				mCurrentShowIndex = mRandom.nextInt(104);
    			} else {
    				mCurrentShowIndex = mRandom.nextInt(71);
    			}

    			mIsCurrentShowHiragana = true;
    			character.setText(mJapanHiragana.get(mCurrentShowIndex));
    			characterMean.setText(mKorea.get(mCurrentShowIndex));
    		} else {
    			if (mShowYoum == true) {
    				mCurrentShowIndex = mRandom.nextInt(104);
    			} else {
    				mCurrentShowIndex = mRandom.nextInt(71);
    			}
    			
    			mIsCurrentShowHiragana = false;
    			character.setText(mJapanGatagana.get(mCurrentShowIndex));
    			characterMean.setText(mKorea.get(mCurrentShowIndex));
    		}
    	} else if (mShowHiragana == true) {
			if (mShowYoum == true) {
				mCurrentShowIndex = mRandom.nextInt(104);
			} else {
				mCurrentShowIndex = mRandom.nextInt(71);
			}
			
			mIsCurrentShowHiragana = true;
			character.setText(mJapanHiragana.get(mCurrentShowIndex));
			characterMean.setText(mKorea.get(mCurrentShowIndex));
    	} else if (mShowGatakana == true) {
			if (mShowYoum == true) {
				mCurrentShowIndex = mRandom.nextInt(104);
			} else {
				mCurrentShowIndex = mRandom.nextInt(71);
			}

			mIsCurrentShowHiragana = false;
			character.setText(mJapanGatagana.get(mCurrentShowIndex));
			characterMean.setText(mKorea.get(mCurrentShowIndex));
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
