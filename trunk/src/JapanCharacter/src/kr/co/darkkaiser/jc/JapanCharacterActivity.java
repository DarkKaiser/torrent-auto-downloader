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
						dlg.setTitle("<������>");
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
				
				// ������ �߻���Ų��.
				Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(10);
			}
		});
        
        // �����͸� �ʱ�ȭ�Ѵ�.
        mKorea = new ArrayList<String>();
        mJapanHiragana = new ArrayList<String>();
        mJapanGatagana = new ArrayList<String>();
        
        mKorea.add("��[a]");
        mKorea.add("��[i]");
        mKorea.add("��[u]");
        mKorea.add("��[e]");
        mKorea.add("��[o]");
        mKorea.add("ī[ka]");
        mKorea.add("Ű[ki]");
        mKorea.add("��[ku]");
        mKorea.add("��[ke]");
        mKorea.add("��[ko]");
        mKorea.add("��[sa]");
        mKorea.add("��[si]");
        mKorea.add("��[su]");
        mKorea.add("��[se]");
        mKorea.add("��[so]");
        mKorea.add("Ÿ[ta]");
        mKorea.add("ġ[chi]");
        mKorea.add("��[tsu]");
        mKorea.add("��[te]");
        mKorea.add("��[to]");
        mKorea.add("��[na]");
        mKorea.add("��[ni]");
        mKorea.add("��[nu]");
        mKorea.add("��[ne]");
        mKorea.add("��[no]");
        mKorea.add("��[ha]");
        mKorea.add("��[hi]");
        mKorea.add("��[hu]");
        mKorea.add("��[he]");
        mKorea.add("ȣ[ho]");
        mKorea.add("��[ma]");
        mKorea.add("��[mi]");
        mKorea.add("��[mu]");
        mKorea.add("��[me]");
        mKorea.add("��[mo]");
        mKorea.add("��[ya]");
        mKorea.add("��[yu]");
        mKorea.add("��[yo]");
        mKorea.add("��[ra]");
        mKorea.add("��[ri]");
        mKorea.add("��[ru]");
        mKorea.add("��[re]");
        mKorea.add("��[ro]");
        mKorea.add("��[wa]");
        mKorea.add("��[o]");
        mKorea.add("��[n]");
        mKorea.add("��[ga]");
        mKorea.add("��[gi]");
        mKorea.add("��[gu]");
        mKorea.add("��[ge]");
        mKorea.add("��[go]");
        mKorea.add("��[za]");
        mKorea.add("��[zi]");
        mKorea.add("��[zu]");
        mKorea.add("��[ze]");
        mKorea.add("��[zo]");
        mKorea.add("��[da]");
        mKorea.add("��[di]");
        mKorea.add("��[du]");
        mKorea.add("��[de]");
        mKorea.add("��[do]");
        mKorea.add("��[ba]");
        mKorea.add("��[bi]");
        mKorea.add("��[bu]");
        mKorea.add("��[be]");
        mKorea.add("��[bo]");
        mKorea.add("��[pa]");
        mKorea.add("��[pi]");
        mKorea.add("Ǫ[pu]");
        mKorea.add("��[pe]");
        mKorea.add("��[po]");
        mKorea.add("ļ[kya]");
        mKorea.add("ť[kyu]");
        mKorea.add("��[kyo]");
        mKorea.add("��[sya]");
        mKorea.add("��[syu]");
        mKorea.add("��[syo]");
        mKorea.add("í[cha]");
        mKorea.add("��[chu]");
        mKorea.add("��[cho]");
        mKorea.add("��[nya]");
        mKorea.add("��[nyu]");
        mKorea.add("��[nyo]");
        mKorea.add("��[hya]");
        mKorea.add("��[hyu]");
        mKorea.add("ȿ[hyo]");
        mKorea.add("��[mya]");
        mKorea.add("��[myu]");
        mKorea.add("��[myo]");
        mKorea.add("��[rya]");
        mKorea.add("��[ryu]");
        mKorea.add("��[ryo]");
        mKorea.add("��[gya]");
        mKorea.add("��[gyu]");
        mKorea.add("��[gyo]");
        mKorea.add("��[zya]");
        mKorea.add("��[zyu]");
        mKorea.add("��[zyo]");
        mKorea.add("��[bya]");
        mKorea.add("��[byu]");
        mKorea.add("��[byo]");
        mKorea.add("��[pya]");
        mKorea.add("ǻ[pyu]");
        mKorea.add("ǥ[pyo]");        

        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("��");
        mJapanHiragana.add("����");
        mJapanHiragana.add("����");
        mJapanHiragana.add("����");
        mJapanHiragana.add("����");
        mJapanHiragana.add("����");
        mJapanHiragana.add("����");
        mJapanHiragana.add("����");
        mJapanHiragana.add("����");
        mJapanHiragana.add("����");
        mJapanHiragana.add("�˪�");
        mJapanHiragana.add("�˪�");
        mJapanHiragana.add("�˪�");
        mJapanHiragana.add("�Ҫ�");
        mJapanHiragana.add("�Ҫ�");
        mJapanHiragana.add("�Ҫ�");
        mJapanHiragana.add("�ߪ�");
        mJapanHiragana.add("�ߪ�");
        mJapanHiragana.add("�ߪ�");
        mJapanHiragana.add("���");
        mJapanHiragana.add("���");
        mJapanHiragana.add("���");
        mJapanHiragana.add("����");
        mJapanHiragana.add("����");
        mJapanHiragana.add("����");
        mJapanHiragana.add("����");
        mJapanHiragana.add("����");
        mJapanHiragana.add("����");
        mJapanHiragana.add("�Ӫ�");
        mJapanHiragana.add("�Ӫ�");
        mJapanHiragana.add("�Ӫ�");
        mJapanHiragana.add("�Ԫ�");
        mJapanHiragana.add("�Ԫ�");
        mJapanHiragana.add("�Ԫ�");

        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("��");
        mJapanGatagana.add("����");
        mJapanGatagana.add("����");
        mJapanGatagana.add("����");
        mJapanGatagana.add("����");
        mJapanGatagana.add("����");
        mJapanGatagana.add("����");
        mJapanGatagana.add("����");
        mJapanGatagana.add("����");
        mJapanGatagana.add("����");
        mJapanGatagana.add("�˫�");
        mJapanGatagana.add("�˫�");
        mJapanGatagana.add("�˫�");
        mJapanGatagana.add("�ҫ�");
        mJapanGatagana.add("�ҫ�");
        mJapanGatagana.add("�ҫ�");
        mJapanGatagana.add("�߫�");
        mJapanGatagana.add("�߫�");
        mJapanGatagana.add("�߫�");
        mJapanGatagana.add("���");
        mJapanGatagana.add("���");
        mJapanGatagana.add("���");
        mJapanGatagana.add("����");
        mJapanGatagana.add("����");
        mJapanGatagana.add("����");
        mJapanGatagana.add("����");
        mJapanGatagana.add("����");
        mJapanGatagana.add("����");
        mJapanGatagana.add("�ӫ�");
        mJapanGatagana.add("�ӫ�");
        mJapanGatagana.add("�ӫ�");
        mJapanGatagana.add("�ԫ�");
        mJapanGatagana.add("�ԫ�");
        mJapanGatagana.add("�ԫ�");
        
        // ���α׷��� ó�� ���۵� �� ���ڸ� ���̵��� �Ѵ�.
        init();
        showNextCharactor();
    }
    
    private void init() {
    	assert mPreferences != null;

    	// ���α׷��� �ʱ�ȭ�մϴ�.
    	mShowYoum = mPreferences.getBoolean("chk_youm", false);
    	mShowHiragana = mPreferences.getBoolean("chk_hiragana", false);
    	mShowGatakana = mPreferences.getBoolean("chk_gatakana", false);
    }

    private void showNextCharactor() {
    	if (mShowHiragana == false && mShowGatakana == false) {
    		Toast.makeText(this, "�ϱ� ��� ���ڰ� ���õ��� �ʾҽ��ϴ�. ȯ�漳�� ���������� �����Ͽ� �ּ���!", Toast.LENGTH_LONG).show();
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
