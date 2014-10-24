package kr.co.darkkaiser.jv.helper;

//@@@@@
@SuppressWarnings("unchecked")
public class CircularBuffer<T> {

	private int mHead = 0;
	private int mTail = 0;
	private T[] mBufferData = (T[])new Object[50];
	
	public CircularBuffer() {

	}

	public synchronized void push(T value) {
		mBufferData[mTail++] = value;

		// TAIL ���� �迭�� �ε����� �Ѿ��ٸ� ó������ �ǵ�����.
		if (mTail == mBufferData.length) {
			mTail = 0;
		}

		// HEAD ���� TAIL ���� ���ٸ� HEAD ���� �������� �̵��Ѵ�.
		if (mTail == mHead) {
			++mHead;
			if (mHead == mBufferData.length)
				mHead = 0;
		}
	}

	public synchronized T pop() {
		if (mHead != mTail) {
			--mTail;
			if (mTail < 0)
				mTail = mBufferData.length - 1;

			return mBufferData[mTail];
		}

		return null;
	}
	
	public T popNoRemove() {
		if (mHead != mTail) {
			int pos = mTail - 1;
			if (pos < 0) {
				pos = mBufferData.length - 1;
			}

			return mBufferData[pos];
		}

		return null;
	}

	public boolean empty() {
		return true;
	}

	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("HEAD:").append(mHead).append(", TAIL:").append(mTail).append(", DATA: ");
		
		boolean bfind = false;
		for (int index = mHead; index < mBufferData.length; ++index) {
			if (index == mTail) {
				bfind = true;
				break;
			}

			sb.append(mBufferData[index]).append(", ");
		}
		
		if (bfind == false) {
			for (int index = 0; index < mBufferData.length; ++index) {
				if (index == mTail) {
					break;
				}

				sb.append(mBufferData[index]).append(", ");
			}			
		}

		return sb.toString();
	}

	public void clear() {
		mHead = mTail = 0;
	}

}
