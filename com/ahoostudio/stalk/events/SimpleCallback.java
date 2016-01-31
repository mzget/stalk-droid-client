package com.ahoostudio.stalk.stalk.events;

import java.util.EventListener;

/**
 * Created by nattapon.r on 1/27/16.
 */
public interface SimpleCallback<T, U> extends EventListener {
    void error(T t);
    void result(U u);
}
