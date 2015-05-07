package com.google.samples.apps.iosched.util;

import com.google.samples.apps.iosched.test.MockHandler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class ThrottledContentObserverTest {

    @Test
    public void onChangeShouldTriggerCallbacksAfterDelay() {
        // arrange
        Looper looper = InstrumentationRegistry.getTargetContext().getMainLooper();
        ThrottledContentObserver.Callbacks mockedCallbacks = Mockito.mock(ThrottledContentObserver.Callbacks.class);
        TestableThrottledContentObserver contentObserver =
                new TestableThrottledContentObserver(mockedCallbacks, looper);

        // act
        contentObserver.onChange(true);
        contentObserver.mMockHandler.advanceBy(ThrottledContentObserver.THROTTLE_DELAY);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        // assert
        Mockito.verify(mockedCallbacks).onThrottledContentObserverFired();
    }

    @Test
    public void onChangeShouldNotTriggerCallbacksBeforeDelay() {
        // arrange
        Looper looper = InstrumentationRegistry.getTargetContext().getMainLooper();
        ThrottledContentObserver.Callbacks mockedCallbacks = Mockito.mock(ThrottledContentObserver.Callbacks.class);
        TestableThrottledContentObserver contentObserver =
                new TestableThrottledContentObserver(mockedCallbacks, looper);

        // act
        contentObserver.onChange(true);
        contentObserver.mMockHandler.advanceBy(ThrottledContentObserver.THROTTLE_DELAY - 1);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        // assert
        Mockito.verify(mockedCallbacks, Mockito.never()).onThrottledContentObserverFired();
    }

    @Test
    public void cancelPendingCallbackShouldRemoveFutureCallbackTriggers() {
        // arrange
        Looper looper = InstrumentationRegistry.getTargetContext().getMainLooper();
        ThrottledContentObserver.Callbacks mockedCallbacks = Mockito.mock(ThrottledContentObserver.Callbacks.class);
        TestableThrottledContentObserver contentObserver =
                new TestableThrottledContentObserver(mockedCallbacks, looper);

        // act
        contentObserver.onChange(true, Uri.EMPTY);
        contentObserver.cancelPendingCallback();
        contentObserver.mMockHandler.advanceBy(ThrottledContentObserver.THROTTLE_DELAY);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        // assert
        Mockito.verify(mockedCallbacks, Mockito.never()).onThrottledContentObserverFired();
    }

    private static class TestableThrottledContentObserver extends ThrottledContentObserver {

        private MockHandler mMockHandler;

        public TestableThrottledContentObserver(Callbacks callback, Looper looper) {
            super(callback, looper);
        }

        @Override
        protected Handler createHandler(Looper looper) {
            mMockHandler = new MockHandler(looper);
            return mMockHandler;
        }

    }
}
