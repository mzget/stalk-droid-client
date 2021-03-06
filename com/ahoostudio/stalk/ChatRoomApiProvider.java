package com.ahoostudio.stalk.stalk;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.ahoostudio.stalk.StalkApplication;
import com.ahoostudio.stalk.stalk.events.SimpleCallback;
import com.ahoostudio.stalk.stalk.events.SimpleListener;
import com.ahoostudio.stalk.gui.chat.ChatContentType;
import com.google.gson.Gson;
import com.netease.pomelo.DataCallBack;
import com.netease.pomelo.PomeloClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by nattapon on 7/17/15 AD.
 */
public class ChatRoomApiProvider extends ServerAPIProvider {

    public void Chat(String room_id, String target, String sender_id, String content, ChatContentType type, final SimpleCallback<String, JSONObject> callback) throws JSONException
    {
        JSONObject message = new JSONObject();
        message.put("rid", room_id);
        message.put("content", content);
        message.put("sender", sender_id);
        message.put("target", target);
        message.put( "type",type.toString());

        PomeloClient pomeloClient = getServerImplemented().getClient();
        if(pomeloClient != null) {
            pomeloClient.request("chat.chatHandler.send", message, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    Log.i("Chat msg response: ", jsonObject.toString());

                    if (callback != null) {
                        callback.result(jsonObject);
                    }
                }
            });
        }
        else {
            if(callback != null)
                callback.error("");
        }
    }

    /// <summary>
    /// Chats the image.
    /// </summary>
    /// <param name="room_id">Room_id.</param>
    /// <param name="target">Target.</param>
    /// <param name="sender_id">Sender_id.</param>
    /// <param name="thumbnailBytes">Thumbnail bytes.</param>
    /// <param name="bytes">Bytes.</param>
    /// <param name="type">Type.</param>
    /// <param name="setStreamMessageID">Set stream message I.</param>
    public void ChatFile(String room_id,
                         String target,
                         final String sender_id,
                         String fileUrl,
                         ChatContentType type,
                         JSONObject meta,
                         final SimpleCallback<String, String> callback) throws JSONException
    {
        JSONObject message = new JSONObject();
        message.put("rid", room_id);
        message.put("content", fileUrl);
        message.put("sender", sender_id);
        message.put("target", target);
        message.put("meta", meta.toString());
        message.put("type", type.toString());

        PomeloClient client = getServerImplemented().getClient();
        if(client != null) {
            client.request("chat.chatHandler.send", message, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    Log.i("chat message callback: ", jsonObject.toString());
                    try {
                        if(jsonObject.getInt("code") == 200) {
                            String messageid = jsonObject.getJSONObject("data").getString("messageId");
                            //String messagetype = jsonObject.getString("type");
                            if(callback != null) {
                                callback.result(messageid);
                            }
                        }
                        else {
                            Log.i("WTF", "WTF god only know.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            if(callback != null) {
                callback.error("");
            }
        }
    }

    public void ChatVoice(String room_id,
                          String target,
                          String sender_id,
                          String fileUrl,
                          ChatContentType type,
                          final SimpleListener<String> setMessageID ) throws JSONException
    {
        JSONObject message = new JSONObject();
        message.put("rid", room_id);
        message.put("content", fileUrl);
        message.put("from", sender_id);
        message.put("target", target);
        message.put("type", type.toString());
        getServerImplemented().getClient().request("chat.chatHandler.send", message, new DataCallBack() {
            @Override
            public void responseData(JSONObject jsonObject) {
                Log.i("chat message callback", jsonObject.toString());

                try {
                    if(jsonObject.getInt("code") == 200) {
                        String messageid = jsonObject.getString("messageId");
                        String messagetype = jsonObject.getString("type");
                        if(setMessageID!=null)
                            setMessageID.callback( messageid );
                    }
                    else {
                        Log.w("WTF", "WTF god only know.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getSyncDateTime(final SimpleListener<JSONObject> callback) {
        JSONObject message = new JSONObject();
        try {
            message.put(ServerImplemented.ACCESS_TOKEN, getServerImplemented().authenData.getString(ServerImplemented.ACCESS_TOKEN));
            getServerImplemented().getClient().request("chat.chatHandler.getSyncDateTime", message, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    if (callback != null) {
                        callback.callback(jsonObject);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * getChatHistory function used for pull history chat record...
     * Beware!!! please call before JoinChatRoom.
     * @param room_id
     * @param lastMessageTime
     * @param callback
     */
    public void getChatHistory(String room_id, String lastMessageTime, final SimpleCallback<String, JSONArray> callback)
    {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//        String dateFormateInUTC = simpleDateFormat.format(lastAccessTime);

        try {
            JSONObject message = new JSONObject();
            message.put("rid", room_id);
            if(lastMessageTime != null) {
                //<!-- Only first communication is has a problem.
                message.put("lastAccessTime", lastMessageTime);
            }

            PomeloClient client = getServerImplemented().getClient();
            if(client != null) {
                client.request("chat.chatHandler.getChatHistory", message, new DataCallBack() {
                    @Override
                    public void responseData(JSONObject jsonObject) {
                        try {
                            if (jsonObject.getInt("code") == 200) {
                                JSONArray chatrecord = jsonObject.getJSONArray("data");
                                if (callback != null) {
                                    callback.result(chatrecord);
                                }
                            } else {
                                System.err.println("WTF god only know.");
                                if (callback != null) {
                                    callback.result(null);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else {
                if(callback != null) {
                    callback.error("");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getChatContent(String messageId, final SimpleListener<JSONObject> onLoaded, final SimpleListener onFail )
    {
        JSONObject message = new JSONObject();
        try {
            message.put("messageId", messageId);
            getServerImplemented().getClient().request("chat.chatHandler.getMessageContent", message, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    try {
                        if (jsonObject.getInt("code") == 200) {
                            JSONObject content = jsonObject.getJSONObject("data");
                            if (onLoaded != null) {
                                onLoaded.callback(content);
                            }
                        } else {
                            System.err.println("WTF god only know.");
                            if (onFail != null) {
                                onFail.callback(null);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void UploadContentFinished(String contentUrl, String ownerMessageId, final SimpleListener<JSONObject> onFinish)
    {
        JSONObject message = new JSONObject();
        try {
            message.put("ownerMessageId", ownerMessageId);
            message.put("contentUrl", contentUrl);
            getServerImplemented().getClient().request("chat.chatHandler.uploadImageFinished", message, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    if (onFinish != null) {
                        onFinish.callback(jsonObject);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateMessageReader(String messageId, String roomId) {
        try {
            JSONObject message = new JSONObject();
            message.put("messageId", messageId);
            message.put("roomId", roomId);
            getServerImplemented().getClient().request("chat.chatHandler.updateWhoReadMessage", message, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateMessageReaders(String[] messageIds, String roomId) {
        try {
            JSONObject message = new JSONObject();
            message.put("messageIds", new Gson().toJson(messageIds, String[].class));
            message.put("roomId", roomId);
            getServerImplemented().getClient().request("chat.chatHandler.updateWhoReadMessages", message, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getMessagesReaders() {
        try {
            JSONObject message = new JSONObject();
            message.put(getServerImplemented().ACCESS_TOKEN, getServerImplemented().authenData.getString(getServerImplemented().ACCESS_TOKEN));
            getServerImplemented().getClient().request("chat.chatHandler.getMessagesReaders", message, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
