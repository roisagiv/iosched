package com.google.samples.apps.iosched.test;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.Queue;

public class MockHandler extends Handler {

    private final Queue<MessageWrapper> messages;

    public MockHandler(Looper looper) {
        super(looper);
        messages = new LinkedList<>();
    }

    @Override
    public boolean sendMessageAtTime(@NonNull Message msg, long uptimeMillis) {
        messages.add(new MessageWrapper(msg, uptimeMillis));

        // Put this message in the super. This way we can track calls to removeMessages,
        // since we cannot override removeMessages method (it's final).
        // By using Integer.MAX_VALUE as 'when' we make sure super will never dispatch this message
        super.sendMessageAtTime(Message.obtain(msg), Integer.MAX_VALUE);

        return true;
    }

    public void advanceBy(long interval) {
        Queue<MessageWrapper> futureMessages = new LinkedList<>();

        while (!messages.isEmpty()) {
            MessageWrapper messageWrapper = messages.poll();

            if (!super.hasMessages(messageWrapper.message.what)) {
                // do nothing, super does not have this message, it has been removed
                continue;
            }

            long virtualNow = interval + SystemClock.uptimeMillis();

            if (messageWrapper.when <= virtualNow) {

                super.removeMessages(messageWrapper.message.what);
                super.dispatchMessage(messageWrapper.message);

            } else {
                futureMessages.add(new MessageWrapper(
                        messageWrapper.message,
                        messageWrapper.when - virtualNow - 1));
            }
        }

        messages.addAll(futureMessages);
    }

    private static class MessageWrapper {
        private final Message message;
        private final long when;

        private MessageWrapper(Message message, long when) {
            this.message = message;
            this.when = when;
        }
    }
}
