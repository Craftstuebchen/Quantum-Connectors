package com.github.ysl3000.quantum.impl.utils;

/**
 * Created by ysl3000 on 19.01.17.
 */
public interface Replacer<I, O> {
    O replace(I in);
}
