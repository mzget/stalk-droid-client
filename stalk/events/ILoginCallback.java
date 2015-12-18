package com.ahoostudio.stalk.droid.stalk.events;

import org.json.JSONObject;

/**
 * Created by nattapon on 7/15/15 AD.
 */
public interface ILoginCallback {
    void loginResponse(boolean success, JSONObject result);
    void onConnectionFail();
}
