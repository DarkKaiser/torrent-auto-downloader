package kr.co.darkkaiser.jv.util;

public class CircularBuffer<T> {

    private static final int BUFFER_LENGTH = 50;

    private int head = 0;
	private int tail = 0;

    @SuppressWarnings (value="unchecked")
	private T[] bufferData = (T[]) new Object[BUFFER_LENGTH];

	public CircularBuffer() {
    }

	public synchronized void push(T value) {
		this.bufferData[this.tail++] = value;

		// TAIL 값이 배열의 인덱스를 넘었다면 처음으로 되돌린다.
		if (this.tail == this.bufferData.length) {
            this.tail = 0;
		}

		// HEAD 값과 TAIL 값이 같다면 HEAD 값을 다음으로 이동한다.
		if (this.tail == this.head) {
			++this.head;
			if (this.head == this.bufferData.length)
                this.head = 0;
		}
	}

	public synchronized T pop() {
		if (this.head != this.tail) {
			--this.tail;
			if (this.tail < 0)
                this.tail = this.bufferData.length - 1;

			return this.bufferData[this.tail];
		}

		return null;
	}
	
	public T popNoRemove() {
		if (this.head != this.tail) {
			int pos = this.tail - 1;
			if (pos < 0) {
				pos = this.bufferData.length - 1;
			}

			return this.bufferData[pos];
		}

		return null;
	}

	@SuppressWarnings("unused")
	public boolean empty() {
        return this.head == this.tail;
    }

	@SuppressWarnings("unused")
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("HEAD:").append(this.head).append(", TAIL:").append(this.tail).append(", DATA: ");
		
		boolean bFind = false;
		for (int index = this.head; index < this.bufferData.length; ++index) {
			if (index == this.tail) {
				bFind = true;
				break;
			}

			sb.append(this.bufferData[index]).append(", ");
		}

		if (bFind == false) {
			for (int index = 0; index < this.bufferData.length; ++index) {
				if (index == this.tail) {
					break;
				}

				sb.append(this.bufferData[index]).append(", ");
			}
		}

		return sb.toString();
	}

	public void clear() {
        this.head = this.tail = 0;
	}

}
