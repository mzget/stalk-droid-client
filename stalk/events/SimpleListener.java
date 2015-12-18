package com.ahoostudio.stalk.droid.stalk.events;

import java.util.EventListener;

/**
 * Created by nattapon on 7/17/15 AD.
 */
public interface SimpleListener<T> extends EventListener{
    void callback(T t);
}