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
							descriptionTv.setText(String.format("���� : %s\n��Ÿī�� : %s", mKorea.get(mCurrentShowIndex), mJapanGatagana.get(mCurrentShowIndex)));
						else
							descriptionTv.setText(String.format("���� : %s\n���󰡳� : %s", mKorea.get(mCurrentShowIndex), mJapanHiragana.get(mCurrentShowIndex)));

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
					// ������ �߻���Ų��.
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

						// ���尡 �ε�Ǵ� �� �ð��� �ణ �ʿ��ϹǷ� �Ʒ��� ���� �ε尡 �Ϸ�Ǿ� ����� �ùٸ��� �Ǿ����� üũ�Ѵ�.
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
        
        // �����͸� �ʱ�ȭ�Ѵ�.
        mKorea = new ArrayList<String>();
        mJapanHiragana = new ArrayList<String>();
        mJapanGatagana = new ArrayList<String>();
        mJapanSoundPool = new ArrayList<JapanCharacterSound>();

        mKorea.add("��[a]");		mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.a));
        mKorea.add("��[i]");		mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.i));
        mKorea.add("��[u]");		mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.u));
        mKorea.add("��[e]");		mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.e));
        mKorea.add("��[o]");		mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.o));
        mKorea.add("ī[ka]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ka));
        mKorea.add("Ű[ki]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ki));
        mKorea.add("��[ku]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ku));
        mKorea.add("��[ke]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ke));
        mKorea.add("��[ko]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ko));
        mKorea.add("��[sa]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.sa));
        mKorea.add("��[si]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.si));
        mKorea.add("��[su]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.su));
        mKorea.add("��[se]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.se));
        mKorea.add("��[so]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.so));
        mKorea.add("Ÿ[ta]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ta));
        mKorea.add("ġ[chi]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.chi));
        mKorea.add("��[tsu]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.tsu));
        mKorea.add("��[te]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.te));
        mKorea.add("��[to]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.to));
        mKorea.add("��[na]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.na));
        mKorea.add("��[ni]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ni));
        mKorea.add("��[nu]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.nu));
        mKorea.add("��[ne]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ne));
        mKorea.add("��[no]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.no));
        mKorea.add("��[ha]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ha));
        mKorea.add("��[hi]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.hi));
        mKorea.add("��[hu]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.hu));
        mKorea.add("��[he]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.he));
        mKorea.add("ȣ[ho]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ho));
        mKorea.add("��[ma]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ma));
        mKorea.add("��[mi]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.mi));
        mKorea.add("��[mu]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.mu));
        mKorea.add("��[me]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.me));
        mKorea.add("��[mo]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.mo));
        mKorea.add("��[ya]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ya));
        mKorea.add("��[yu]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.yu));
        mKorea.add("��[yo]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.yo));
        mKorea.add("��[ra]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ra));
        mKorea.add("��[ri]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ri));
        mKorea.add("��[ru]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ru));
        mKorea.add("��[re]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.re));
        mKorea.add("��[ro]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ro));
        mKorea.add("��[wa]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.wa));
        mKorea.add("��[o]");		mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.o2));
        mKorea.add("��[n]");		mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.n));
        mKorea.add("��[ga]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ga));
        mKorea.add("��[gi]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.gi));
        mKorea.add("��[gu]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.gu));
        mKorea.add("��[ge]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ge));
        mKorea.add("��[go]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.go));
        mKorea.add("��[za]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.za));
        mKorea.add("��[zi]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.zi));
        mKorea.add("��[zu]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.zu));
        mKorea.add("��[ze]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ze));
        mKorea.add("��[zo]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.zo));
        mKorea.add("��[da]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.da));
        mKorea.add("��[di]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.di));
        mKorea.add("��[du]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.du));
        mKorea.add("��[de]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.de));
        mKorea.add("��[do]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.do2));
        mKorea.add("��[ba]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ba));
        mKorea.add("��[bi]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.bi));
        mKorea.add("��[bu]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.bu));
        mKorea.add("��[be]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.be));
        mKorea.add("��[bo]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.bo));
        mKorea.add("��[pa]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.pa));
        mKorea.add("��[pi]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.pi));
        mKorea.add("Ǫ[pu]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.pu));
        mKorea.add("��[pe]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.pe));
        mKorea.add("��[po]");	mJapanHiragana.add("��");	mJapanGatagana.add("��");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.po));
        mKorea.add("ļ[kya]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.kya));
        mKorea.add("ť[kyu]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.kyu));
        mKorea.add("��[kyo]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.kyo));
        mKorea.add("��[sya]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.sya));
        mKorea.add("��[syu]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.syu));
        mKorea.add("��[syo]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.syo));
        mKorea.add("í[cha]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.cha));
        mKorea.add("��[chu]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.chu));
        mKorea.add("��[cho]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.cho));
        mKorea.add("��[nya]");	mJapanHiragana.add("�˪�");	mJapanGatagana.add("�˫�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.nya));
        mKorea.add("��[nyu]");	mJapanHiragana.add("�˪�");	mJapanGatagana.add("�˫�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.nyu));
        mKorea.add("��[nyo]");	mJapanHiragana.add("�˪�");	mJapanGatagana.add("�˫�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.nyo));
        mKorea.add("��[hya]");	mJapanHiragana.add("�Ҫ�");	mJapanGatagana.add("�ҫ�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.hya));
        mKorea.add("��[hyu]");	mJapanHiragana.add("�Ҫ�");	mJapanGatagana.add("�ҫ�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.hyu));
        mKorea.add("ȿ[hyo]");	mJapanHiragana.add("�Ҫ�");	mJapanGatagana.add("�ҫ�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.hyo));
        mKorea.add("��[mya]");	mJapanHiragana.add("�ߪ�");	mJapanGatagana.add("�߫�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.mya));
        mKorea.add("��[myu]");	mJapanHiragana.add("�ߪ�");	mJapanGatagana.add("�߫�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.myu));
        mKorea.add("��[myo]");	mJapanHiragana.add("�ߪ�");	mJapanGatagana.add("�߫�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.myo));
        mKorea.add("��[rya]");	mJapanHiragana.add("���");	mJapanGatagana.add("���");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.rya));
        mKorea.add("��[ryu]");	mJapanHiragana.add("���");	mJapanGatagana.add("���");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ryu));
        mKorea.add("��[ryo]");	mJapanHiragana.add("���");	mJapanGatagana.add("���");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.ryo));
        mKorea.add("��[gya]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.gya));
        mKorea.add("��[gyu]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.gyu));
        mKorea.add("��[gyo]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.gyo));
        mKorea.add("��[zya]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.zya));
        mKorea.add("��[zyu]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.zyu));
        mKorea.add("��[zyo]");	mJapanHiragana.add("����");	mJapanGatagana.add("����");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.zyo));
        mKorea.add("��[bya]");	mJapanHiragana.add("�Ӫ�");	mJapanGatagana.add("�ӫ�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.bya));
        mKorea.add("��[byu]");	mJapanHiragana.add("�Ӫ�");	mJapanGatagana.add("�ӫ�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.byu));
        mKorea.add("��[byo]");	mJapanHiragana.add("�Ӫ�");	mJapanGatagana.add("�ӫ�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.byo));
        mKorea.add("��[pya]");	mJapanHiragana.add("�Ԫ�");	mJapanGatagana.add("�ԫ�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.pya));
        mKorea.add("ǻ[pyu]");	mJapanHiragana.add("�Ԫ�");	mJapanGatagana.add("�ԫ�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.pyu));
        mKorea.add("ǥ[pyo]");	mJapanHiragana.add("�Ԫ�");	mJapanGatagana.add("�ԫ�");	mJapanSoundPool.add(new JapanCharacterSound(R.raw.pyo));

        // ���α׷��� ó�� ���۵� �� ���ڸ� ���̵��� �Ѵ�.
        init();
        showNextCharactor();
    }
    
    private void init() {
    	assert mPreferences != null;

    	// ���α׷��� �ʱ�ȭ�մϴ�.
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
    		Toast.makeText(this, "�ϱ� ��� ���ڰ� ���õ��� �ʾҽ��ϴ�. ȯ�漳�� ���������� �����Ͽ� �ּ���!", Toast.LENGTH_LONG).show();
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

		MenuItem item = menu.add(0, 1, 0, "ȯ�漳��");
		item.setIcon(android.R.drawable.ic_menu_preferences);
		return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			// ���� �������� ����.
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
