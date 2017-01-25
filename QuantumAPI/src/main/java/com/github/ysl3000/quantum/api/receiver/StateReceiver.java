package com.github.ysl3000.quantum.api.receiver;

/**
 * Created by ysl3000 on 23.01.17.
 */
public interface StateReceiver {
    ReceiverState getState();

    void setState(ReceiverState state);
}
