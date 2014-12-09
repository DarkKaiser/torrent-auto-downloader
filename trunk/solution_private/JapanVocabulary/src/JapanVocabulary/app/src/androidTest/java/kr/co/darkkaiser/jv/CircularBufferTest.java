package kr.co.darkkaiser.jv;

import junit.framework.TestCase;

import kr.co.darkkaiser.jv.util.CircularBuffer;

public class CircularBufferTest extends TestCase {

    private CircularBuffer<Integer> mCircularBuffer = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mCircularBuffer = new CircularBuffer<Integer>();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        mCircularBuffer = null;
    }

    public void testPushPop() {
        assertEquals(mCircularBuffer.empty(), true);

        mCircularBuffer.push(1);
        assertEquals(mCircularBuffer.empty(), false);

        mCircularBuffer.push(2);
        assertEquals((int)mCircularBuffer.pop(), 2);
        assertEquals((int)mCircularBuffer.pop(), 1);
        assertEquals(mCircularBuffer.pop(), null);
        assertEquals(mCircularBuffer.empty(), true);

        for (int index = 0; index <= CircularBuffer.BUFFER_LENGTH; ++index)
            mCircularBuffer.push(index);

        // CircularBuffer.BUFFER_LENGTH-1개가 실 사용할 수 있는 버퍼이다.
        // 1개는 다음 추가를 위한 비어있는 공간이다.
        for (int index = CircularBuffer.BUFFER_LENGTH; index > 1; --index)
            assertEquals((int)mCircularBuffer.pop(), index);

        assertEquals(mCircularBuffer.pop(), null);
    }

    public void testPopNoRemove() {
        assertEquals(mCircularBuffer.pop(), null);
        assertEquals(mCircularBuffer.popNoRemove(), null);

        mCircularBuffer.push(1);
        assertEquals(mCircularBuffer.empty(), false);
        assertEquals((int)mCircularBuffer.popNoRemove(), 1);
        assertEquals(mCircularBuffer.empty(), false);
    }

    public void testClear() {
        mCircularBuffer.push(1);
        mCircularBuffer.push(2);
        mCircularBuffer.push(3);

        mCircularBuffer.clear();
        assertEquals(mCircularBuffer.pop(), null);
    }

}
