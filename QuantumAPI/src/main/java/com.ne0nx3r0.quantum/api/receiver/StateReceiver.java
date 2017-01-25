package com.ne0nx3r0.quantum.api.receiver;

/**
 * Created by ysl3000 on 23.01.17.
 */
public interface StateReceiver {
    QuantumState getState();

    void setState(QuantumState state);
}
