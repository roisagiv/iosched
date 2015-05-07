package com.google.samples.apps.iosched.util;

import com.google.samples.apps.iosched.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import android.net.Uri;
import android.os.Looper;

/**
 *
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 16)
public class ThrottledContentObserverTest {

    @Test
    public void onChangeShouldTriggerCallbacksAfterDelay() {
        // arrange
        Looper looper = RuntimeEnvironment.application.getMainLooper();
        ThrottledContentObserver.Callbacks mockedCallbacks = Mockito.mock(ThrottledContentObserver.Callbacks.class);
        ThrottledContentObserver contentObserver =
                new ThrottledContentObserver(mockedCallbacks, looper);

        // act
        contentObserver.onChange(true);
        Robolectric.getForegroundThreadScheduler().advanceBy(ThrottledContentObserver.THROTTLE_DELAY);

        // assert
        Mockito.verify(mockedCallbacks).onThrottledContentObserverFired();
    }

    @Test
    public void onChangeShouldNotTriggerCallbacksBeforeDelay() {
        // arrange
        Looper looper = RuntimeEnvironment.application.getMainLooper();
        ThrottledContentObserver.Callbacks mockedCallbacks = Mockito.mock(ThrottledContentObserver.Callbacks.class);
        ThrottledContentObserver contentObserver =
                new ThrottledContentObserver(mockedCallbacks, looper);

        // act
        contentObserver.onChange(true);
        Robolectric.getForegroundThreadScheduler().advanceBy(ThrottledContentObserver.THROTTLE_DELAY - 1);

        // assert
        Mockito.verify(mockedCallbacks, Mockito.never()).onThrottledContentObserverFired();
    }

    @Test
    public void cancelPendingCallbackShouldRemoveFutureCallbackTriggers() {
        // arrange
        Looper looper = RuntimeEnvironment.application.getMainLooper();
        ThrottledContentObserver.Callbacks mockedCallbacks = Mockito.mock(ThrottledContentObserver.Callbacks.class);
        ThrottledContentObserver contentObserver = new ThrottledContentObserver(mockedCallbacks, looper);

        // act
        contentObserver.onChange(true, Uri.EMPTY);
        contentObserver.cancelPendingCallback();
        Robolectric.getForegroundThreadScheduler().advanceBy(ThrottledContentObserver.THROTTLE_DELAY);

        // assert
        Mockito.verify(mockedCallbacks, Mockito.never()).onThrottledContentObserverFired();
    }
}
