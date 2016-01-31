package com.ahoostudio.stalk.stalk;

import android.util.Log;

import com.ahoostudio.stalk.stalk.dataModel.TokenDecodedModel;
import com.ahoostudio.stalk.stalk.events.AccessTokenListener;
import com.ahoostudio.stalk.stalk.events.ILoginCallback;
import com.ahoostudio.stalk.stalk.events.SimpleListener;
import com.ahoostudio.stalk.StalkApplication;
import com.google.gson.Gson;
import com.netease.pomelo.DataCallBack;
import com.netease.pomelo.DataEvent;
import com.netease.pomelo.DataListener;
import com.netease.pomelo.PomeloClient;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.EventListener;

/**
 * Created by nattapon on 7/15/15 AD.
 */
public class ServerImplemented {
    private String TAG = "ServerImplemented";

    public interface IConnectionListen extends EventListener{
        void connectionEvent(String event);
    }

    private IConnectionListen connectionListen;
    public void setConnectionListen(IConnectionListen listen) {
        this.connectionListen = listen;
    }

    private  static ServerImplemented instance;
    private boolean isInit = false;
    public static boolean IsConnected = false;
    public JSONObject authenData;

    private PomeloClient client;
    public PomeloClient getClient() {
        if(StalkApplication.getConnectivityDetector().isConnectingToInternet()) {
            if(client != null) {
                return client;
            }
            else  {
                Log.e(TAG, "getClient: " + client);
                System.err.println("disconnect Event");
                if(connectionListen != null) {
                    connectionListen.connectionEvent("disconnect");
                }

                return  null;
            }
        }
        else {
            return null;
        }
    }
    public boolean isClientDead(){
        if(client == null) return true;
        else return  false;
    }
    String host = "";
    int port = 3014;
    protected String username;
    protected String password;
    public static final String ACCESS_TOKEN = "token";
    public static final String USER_ID = "uid";
    public static final String REGISTRATION_ID = "registrationId";

    public synchronized static ServerImplemented CreateInstance(String host, String port){
        if(instance == null) {
            instance = new ServerImplemented();
            instance.host = host;
            instance.port = Integer.parseInt(port);
            instance.connectSocketServer(instance.host, instance.port);
        }

        return instance;
    }

    private void connectSocketServer(String _host, int _port) {
        client = new PomeloClient(_host,_port);
        client.on("disconnect", new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                System.err.println("disconnect Event: pomelo client will destroy.");
                client = null;
                if(connectionListen != null) {
                    connectionListen.connectionEvent("disconnect");
                }
            }
        });
        client.init();

        isInit = true;
    }

    public void disconnectSocketServer() {
        if(client != null) {
            client.disconnect();
            client = null;
        }

        authenData = null;
        IsConnected = false;
        instance = null;
    }


    // region <!-- Authentication...
    /// <summary>
    /// Connect to gate server then get query of connector server.
    /// </summary>
    public void logIn(final String username , final String passwordHash, final ILoginCallback callback) {
        this.username = username;
        this.password = passwordHash;

        if(client == null) {
            this.connectSocketServer(host, port);
        }

        if (!IsConnected) {
            try {
                JSONObject msg = new JSONObject();
                msg.put("uid", this.username);

                client.request("gate.gateHandler.queryEntry", msg, new DataCallBack() {
                    public void responseData(JSONObject result) {
                        Log.d(TAG, "QueryConnectorServ: " + result.toString());

                        try {
                            if (result.getInt("code") == 200) {
                                client.disconnect();
                                client = null;
                                IsConnected = true;
                                int port = result.getInt("port");
                                connectSocketServer(host, port);
                                ConnectConnectorServer(callback);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
                callback.onConnectionFail();
            }
        }
        else {
             ConnectConnectorServer(callback);
        }
    }

    //<!-- Authentication. request for token sign.
    void ConnectConnectorServer(final ILoginCallback callback) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("username", this.username);
            msg.put("password", this.password);
            if(StalkApplication.getSharedAppData().contains(REGISTRATION_ID)) {
                msg.put(REGISTRATION_ID, StalkApplication.getSharedAppData().getString(REGISTRATION_ID, ""));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //<!-- Authentication.
        client.request("connector.entryHandler.login", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject message) {
                try {
                    if (message.getInt("code") == 500) {
                        if (callback != null) {
                            callback.loginResponse(false, message);
                        }
                    }
                    else  if(message.getInt("code") == 1004) {
                        if (callback != null) {
                            callback.loginResponse(false, message);
                        }
//                        client.disconnect();
//                        client = null;
                    }
                    else if(message.getInt("code") == 200){
                        authenData = new JSONObject();
                        authenData.put(USER_ID, message.getString(USER_ID));
                        authenData.put(ACCESS_TOKEN, message.getString(ACCESS_TOKEN));

                        if (callback != null) {
                            callback.loginResponse(true, message);
                        }
                    }
                    else if(message.getInt("code") == 408) {
                        if(callback != null) {
                            callback.loginResponse(false, message);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void TokenAuthen(String tokenBearer, final AccessTokenListener onSuccessCheckToken) {
        JSONObject msg = new JSONObject();
        try {
            if(client != null) {
                msg.put(ACCESS_TOKEN, tokenBearer);
                client.request("gate.gateHandler.authenGateway", msg, new DataCallBack() {
                    @Override
                    public void responseData(JSONObject jsonObject) {
                        OnTokenAuthenticate(jsonObject, onSuccessCheckToken);
                    }
                });
            }
            else {
                onSuccessCheckToken.onFailToAccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
            onSuccessCheckToken.onFailToAccess();
        }
    }

    private void OnTokenAuthenticate (JSONObject obj, AccessTokenListener onSuccessCheckToken) {
        try {
            if(obj.getInt("code") == 200) {
                JSONObject data = obj.getJSONObject("data");
                JSONObject decode = data.getJSONObject("decoded"); //["decoded"];
                TokenDecodedModel decodedModel = new Gson().fromJson(decode.toString(), TokenDecodedModel.class);
                if(onSuccessCheckToken!=null)
                    onSuccessCheckToken.onTokenRespones( true, decodedModel.username, decodedModel.password );
            }
            else {
                if(onSuccessCheckToken != null)
                    onSuccessCheckToken.onTokenRespones(false, null, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Logout (String userName)
    {
        JSONObject msg = new JSONObject ();
        try {
            msg.put("username", userName);
            if(client != null && IsConnected) {
                client.inform("connector.entryHandler.logout", msg);
                IsConnected = false;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void kickMeAllSession(String uid, final SimpleListener kicked) {
        if(client != null) {
            JSONObject msg = new JSONObject();
            try {
                msg.put("uid", uid);
                client.request("connector.entryHandler.kickMe", msg, new DataCallBack() {
                    @Override
                    public void responseData(JSONObject jsonObject) {
                        Log.i(this.toString(), "kickMe " + jsonObject.toString());
                        if(kicked != null) {
                            kicked.callback(null);
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //endregion
}
