package kr.co.darkkaiser.jc;

import android.content.Context;
import android.media.SoundPool;

public class JapanCharacterSound {

	private int mSoundId = 0;
	private int mSoundResId = 0;
	private boolean mIsLoad = false;

	public JapanCharacterSound(int soundResId) {
		setSoundResId(soundResId);
	}

	public int getSoundId() {
		if (isLoad() == false) {
			assert false;
		}
		
		return mSoundId;
	}

	public void load(Context context, SoundPool soundPool) {
		assert context != null;
		assert soundPool != null;

		if (isLoad() == false) {
			mSoundId = soundPool.load(context, mSoundResId, 1);
			mIsLoad = true;
		}
	}
	
	public boolean isLoad() {
		return mIsLoad;
	}

	private void setSoundResId(int soundResId) {
		mSoundResId = soundResId;
	}

}
