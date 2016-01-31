package com.ahoostudio.stalk.stalk.events;

import java.util.EventListener;

/**
 * Created by nattapon on 7/19/15 AD.
 */
public interface AccessTokenListener extends EventListener{
    void onTokenRespones(boolean authorized, String username, String password);
    void onFailToAccess();
}
