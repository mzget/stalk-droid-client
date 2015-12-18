package com.ahoostudio.stalk.droid.stalk;

import android.os.AsyncTask;
import android.util.Log;

import com.ahoostudio.stalk.droid.stalk.events.SimpleListener;
import com.ahoostudio.stalk.droid.StalkApplication;
import com.ahoostudio.stalk.droid.dataModel.network.GroupMember;
import com.ahoostudio.stalk.droid.dataModel.network.RoomType;
import com.google.gson.Gson;
import com.netease.pomelo.DataCallBack;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nattapon on 7/16/15 AD.
 */
public class ServerAPIProvider implements IServerProvider {
    private static ServerAPIProvider instance;
    public synchronized static ServerAPIProvider getInstance() {
        if(instance == null) {
            instance = new ServerAPIProvider();
        }

        return  instance;
    };

    @Override
    public ServerImplemented getServerImplemented() {
        return StalkApplication.getServerImplemented();
    }

    //region <!- Company data.

    /// <summary>
    /// Gets the company info.
    /// Beware for data loading so mush. please load sender cache before load sender server.
    /// </summary>
    public void getCompanyInfo(final DataCallBack callBack) throws JSONException {
        JSONObject msg = new JSONObject();
        msg.put("token", getServerImplemented().authenData.getString("token"));
        getServerImplemented().getClient().request("connector.entryHandler.getCompanyInfo", msg, new DataCallBack() {
                    @Override
                    public void responseData(JSONObject jsonObject) {
                        Log.println(Log.INFO, "getCompanyInfo", jsonObject.toString());

                        if(callBack != null)
                            callBack.responseData(jsonObject);
                    }
                });
    }

    /// <summary>
    /// Gets the company members.
    /// Beware for data loading so mush. please load sender cache before load sender server.
    /// </summary>
    public void getCompanyMembers(final DataCallBack callBack) throws JSONException {
        JSONObject msg = new JSONObject();
        msg.put("token", getServerImplemented().authenData.getString("token"));
        getServerImplemented().getClient().request("connector.entryHandler.getCompanyMember", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject jsonObject) {
                Log.println(Log.INFO, "getCompanyMembers", jsonObject.toString());

                if(callBack != null)
                    callBack.responseData(jsonObject);
            }
        });
    }

    /// <summary>
    /// Gets the company chat rooms.
    /// Beware for data loading so mush. please load sender cache before load sender server.
    /// </summary>
    public void getOrganizationGroups(final DataCallBack callBack) throws JSONException {
        JSONObject msg = new JSONObject();
        msg.put("token", getServerImplemented().authenData.getString("token"));
        getServerImplemented().getClient().request("connector.entryHandler.getCompanyChatRoom", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject jsonObject) {
                System.out.println("getOrganizationGroups: " + jsonObject.toString());

                if(callBack != null)
                    callBack.responseData(jsonObject);
            }
        });
    }

    //endregion

    //region <!-- Private Group...

    public void getPrivateGroups(final DataCallBack callback) throws JSONException{
        JSONObject msg = new JSONObject();
        msg.put("token", getServerImplemented().authenData.get("token"));
        getServerImplemented().getClient().request("connector.entryHandler.getMyPrivateGroupChat", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject jsonObject) {
                System.out.println("getPrivateGroups: " + jsonObject.toString());
                if(callback != null){
                    callback.responseData(jsonObject);
                }
            }
        });
    }

    public void UserRequestCreateGroupChat(String groupName, String[] memberIds, boolean isPrivate, final DataCallBack callback) throws JSONException{
        JSONObject msg = new JSONObject();
        msg.put("token", getServerImplemented().authenData.getString("token"));
        msg.put("groupName", groupName);
        msg.put("memberIds", new Gson().toJson(memberIds, String[].class));
        msg.put("type", isPrivate);
        getServerImplemented().getClient().request("chat.chatRoomHandler.userCreateGroupChat", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject jsonObject) {
                Log.i("RequestCreateGroupChat", jsonObject.toString());

                if(callback != null)
                    callback.responseData(jsonObject);
            }
        });
    }

    public void UpdatedGroupImage(String groupId, String path, final DataCallBack callback) throws JSONException {
        JSONObject msg = new JSONObject();
        msg.put("token", getServerImplemented().authenData.getString("token"));
        msg.put("groupId", groupId);
        msg.put("path", path);
        getServerImplemented().getClient().request("chat.chatRoomHandler.updateGroupImage", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject jsonObject) {
                Log.i("UpdatedGroupImage", jsonObject.toString());

                if(callback != null){
                    callback.responseData(jsonObject);
                }
            }
        });
    }

    public void editGroupMembers(String editType, String roomId, RoomType roomType, String[] members, final SimpleListener<JSONObject> callback) {
        if(editType == null || editType.isEmpty()) return;
        if(roomId == null || roomId.isEmpty()) return;
        if(String.valueOf(roomType) == null || String.valueOf(roomType).isEmpty()) return;
        if(members == null || members.length == 0) return;

        try {
            JSONObject msg = new JSONObject();
            msg.put("token", getServerImplemented().authenData.getString("token"));
            msg.put("editType", editType);
            msg.put("roomId", roomId);
            msg.put("roomType", String.valueOf(roomType));
            msg.put("members", new Gson().toJson(members, String[].class));
            getServerImplemented().getClient().request("chat.chatRoomHandler.editGroupMembers", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    Log.i(this.toString(), "editGroupMembers response." + jsonObject.toString());

                    if (callback != null) {
                        callback.callback(jsonObject);
                    }
                }
            });
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void editGroupName(String roomId, RoomType roomType, String newGroupName, final SimpleListener<JSONObject> callback) {
        if(roomId == null || roomId.isEmpty()) return;
        if(String.valueOf(roomType) == null || String.valueOf(roomType).isEmpty()) return;
        if(newGroupName == null || newGroupName.isEmpty()) return;
        try {
            JSONObject msg = new JSONObject();
            msg.put("token", getServerImplemented().authenData.getString("token"));
            msg.put("roomId", roomId);
            msg.put("roomType", String.valueOf(roomType));
            msg.put("newGroupName", newGroupName);
            getServerImplemented().getClient().request("chat.chatRoomHandler.editGroupName", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    Log.i(this.toString(), "editGroupName response." + jsonObject.toString());

                    if (callback != null) {
                        callback.callback(jsonObject);
                    }
                }
            });
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /// <summary>
    /// Gets Private Chat Room.
    /// </summary>
    /// <param name="myId">My identifier.</param>
    /// <param name="myRoommateId">My roommate identifier.</param>
    public void getPrivateChatRoomId(String myId, String myRoommateId, final DataCallBack callBack) throws JSONException {
        JSONObject msg = new JSONObject();
        msg.put("token", getServerImplemented().authenData.getString("token"));
        msg.put("ownerId", myId);
        msg.put("roommateId", myRoommateId);
        getServerImplemented().getClient().request("chat.chatRoomHandler.getRoomById", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject jsonObject) {
                Log.i("getPrivateChatRoomId", jsonObject.toString());

                if(callBack != null){
                    callBack.responseData(jsonObject);
                }
            }
        });
    }

    //<!-- Join and leave chat room.
    public void JoinChatRoomRequest (String room_id, final SimpleListener<JSONObject> onFinish, final SimpleListener onFail)
            throws  JSONException {
        JSONObject msg = new JSONObject();
        msg.put("token", getServerImplemented().authenData.getString("token"));
        msg.put("rid", room_id);
        msg.put("username", getServerImplemented().username);
        getServerImplemented().getClient().request("connector.entryHandler.enterRoom", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject jsonObject) {
                Log.i(getClass().getSimpleName(), "JoinChatRequest: " + jsonObject);
                try {
                    if(jsonObject.getInt("code") == 200) {
                        if(onFinish != null) {
                            onFinish.callback(jsonObject);
                        }
                    }
                    else {
                        if(onFail != null) {
                            onFail.callback(null);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void LeaveChatRoomRequest(String roomId, final SimpleListener<JSONObject> onLeaveRoom) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("token", getServerImplemented().authenData.getString("token"));
            msg.put("rid", roomId);
            msg.put("username", getServerImplemented().username);
            getServerImplemented().getClient().request("connector.entryHandler.leaveRoom", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    if (onLeaveRoom != null)
                        onLeaveRoom.callback(jsonObject);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /// <summary>
    /// Gets the room info. For load Room info by room_id.
    /// </summary>
    /// <c> return data</c>
    public void getRoomInfo(String roomId, final SimpleListener<JSONObject> callback) {
        try {
            JSONObject msg = new JSONObject();
            msg.put(ServerImplemented.ACCESS_TOKEN, getServerImplemented().authenData.getString(ServerImplemented.ACCESS_TOKEN));
            msg.put("roomId", roomId);

            getServerImplemented().getClient().request("chat.chatRoomHandler.getRoomInfo", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    if(callback != null)
                        callback.callback(jsonObject);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getUnreadMsgOfRoom(String roomId, String lastAccessTime, final SimpleListener<JSONObject> callback) {
        try {
            JSONObject msg = new JSONObject();
            msg.put(ServerImplemented.ACCESS_TOKEN, getServerImplemented().authenData.getString(ServerImplemented.ACCESS_TOKEN));
            msg.put("roomId", roomId);
            msg.put("lastAccessTime", lastAccessTime);
            getServerImplemented().getClient().request("chat.chatRoomHandler.getUnreadRoomMessage", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    if(callback != null) {
                        callback.callback(jsonObject);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region <!-- Project base.

    /// <summary>
    /// Gets the public group chat rooms.
    /// Beware for data loading so mush. please load sender cache before load sender server.
    /// </summary>
    /// <param name="callback">Callback.</param>
    public void getProjectBaseGroups(final DataCallBack callback) throws JSONException {
        JSONObject msg = new JSONObject();
        msg.put("token", getServerImplemented().authenData.getString("token"));
        getServerImplemented().getClient().request("connector.entryHandler.getProjectBaseGroups", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject jsonObject) {
                System.out.println("getProjectBaseGroups: " + jsonObject.toString());
                if(callback != null)
                    callback.responseData(jsonObject);
            }
        });
    }

    public void requestCreateProjectBaseGroup(String groupName, GroupMember[] members, final DataCallBack callback) throws JSONException{
        JSONObject msg = new JSONObject();
        msg.put("token", getServerImplemented().authenData.getString("token"));
        msg.put("groupName", groupName);
        msg.put("members", new Gson().toJson(members, GroupMember[].class));
        getServerImplemented().getClient().request("chat.chatRoomHandler.requestCreateProjectBase", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject jsonObject) {
//                System.out.println("requestCreateProjectBaseGroup: " + jsonObject.toString());

                if(callback != null)
                    callback.responseData(jsonObject);
            }
        });
    }

    public void editMemberInfoInProjectBase(String roomId, RoomType roomType, GroupMember member, final DataCallBack callback) throws JSONException {
        JSONObject msg = new JSONObject();
        msg.put("token", getServerImplemented().authenData.getString("token"));
        msg.put("roomId", roomId);
        msg.put("roomType", String.valueOf(roomType));
        msg.put("member", new Gson().toJson(member, GroupMember.class));
        getServerImplemented().getClient().request("chat.chatRoomHandler.editMemberInfoInProjectBase", msg, new DataCallBack() {
            @Override
            public void responseData(JSONObject jsonObject) {
                if(callback != null)
                    callback.responseData(jsonObject);
            }
        });
    }

    //endregion

    //region <!-- User Profile...

    public void UpdateUserProfile(String myId, JSONObject profileFields, final SimpleListener<JSONObject> callback) {
        try {
            profileFields.put("token", getServerImplemented().authenData.getString("token"));
            profileFields.put("_id", myId);
            getServerImplemented().getClient().request("auth.profileHandler.profileUpdate", profileFields, new DataCallBack() {
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

    public void ProfileImageChanged(String userId, String path, final SimpleListener<JSONObject> onFinish) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("token", getServerImplemented().authenData.getString("token"));
            msg.put("userId", userId);
            msg.put("path", path);
            getServerImplemented().getClient().request("auth.profileHandler.profileImageChanged", msg, new DataCallBack() {
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

    public void GetLastAccessRoomsInfo(String userId) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("id", userId);
            msg.put("token", getServerImplemented().authenData.getString("token"));
            //<!-- Get user info.
            getServerImplemented().getClient().request("connector.entryHandler.getLastAccessRooms", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    //<!-- No callback response.
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void GetMe (final DataCallBack myInfo) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("username", getServerImplemented().username);
            msg.put("password", getServerImplemented().password);
            msg.put(ServerImplemented.ACCESS_TOKEN, getServerImplemented().authenData.getString(ServerImplemented.ACCESS_TOKEN));
            //<!-- Get user info.
            getServerImplemented().getClient().request("connector.entryHandler.getMe", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject message) {
                    Log.i("getMe result:", message.toString());
                    try {
                        if (message.getInt("code") == 500) {
                            Log.w("GetMe", message.toString());
                            myInfo.responseData(null);
                            //                    if (OnLoginComplete != null)
                            //                        OnLoginComplete (false);
                        } else {
                            myInfo.responseData(message);
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

    public void updateFavoriteMember(String editType, String member, final SimpleListener<JSONObject> callback) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("editType", editType);
            msg.put("member", member);
            msg.put(ServerImplemented.ACCESS_TOKEN, getServerImplemented().authenData.getString(ServerImplemented.ACCESS_TOKEN));
            //<!-- Get user info.
            getServerImplemented().getClient().request("auth.profileHandler.editFavoriteMembers", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject message) {
                    Log.i("updateFavoriteMember: ", message.toString());
                        if(callback != null)
                            callback.callback(message);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateFavoriteGroups(String editType, String group, final SimpleListener<JSONObject> callback) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("editType", editType);
            msg.put("group", group);
            msg.put(ServerImplemented.ACCESS_TOKEN, getServerImplemented().authenData.getString(ServerImplemented.ACCESS_TOKEN));
            //<!-- Get user info.
            getServerImplemented().getClient().request("auth.profileHandler.updateFavoriteGroups", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject message) {
                    Log.i("updateFavoriteGroups: ", message.toString());
                    if(callback != null)
                        callback.callback(message);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateClosedNoticeMemberList(String editType, String member, final SimpleListener<JSONObject> callback) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("editType", editType);
            msg.put("member", member);
            msg.put(ServerImplemented.ACCESS_TOKEN, getServerImplemented().authenData.getString(ServerImplemented.ACCESS_TOKEN));
            //<!-- Get user info.
            getServerImplemented().getClient().request("auth.profileHandler.updateClosedNoticeUsers", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject message) {
                    if(callback != null)
                        callback.callback(message);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateClosedNoticeGroupsList(String editType, String group, final SimpleListener<JSONObject> callback) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("editType", editType);
            msg.put("group", group);
            msg.put(ServerImplemented.ACCESS_TOKEN, getServerImplemented().authenData.getString(ServerImplemented.ACCESS_TOKEN));
            //<!-- Get user info.
            getServerImplemented().getClient().request("auth.profileHandler.updateClosedNoticeGroups", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject message) {
                    if(callback != null)
                        callback.callback(message);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getMemberProfile(String userId, final SimpleListener<JSONObject> callback) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("userId", userId);

            getServerImplemented().getClient().request("auth.profileHandler.getMemberProfile", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    if(callback != null) {
                        callback.callback(jsonObject);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //endregion

    // region <!-- Web RTC Calling...
    //////////////////////////////////////////////////////////////////
    /// <summary>
    /// Videos the call requesting.
    /// - tell target client for your call requesting...
    /// </summary>
    public void videoCallRequest(String targetId, String myRtcId, final SimpleListener<JSONObject> callback) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("token", getServerImplemented().authenData.getString("token"));
            msg.put("targetId", targetId);
            msg.put("myRtcId", myRtcId);
            getServerImplemented().getClient().request("connector.entryHandler.videoCallRequest", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    System.out.println("videoCallRequesting =>: " + jsonObject);
                    if (callback != null)
                        callback.callback(jsonObject);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void voiceCallRequest(String targetId, String myRtcId, final SimpleListener<JSONObject> callback) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("token", getServerImplemented().authenData.getString("token"));
            msg.put("targetId", targetId);
            msg.put("myRtcId", myRtcId);
            getServerImplemented().getClient().request("connector.entryHandler.voiceCallRequest", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    System.out.println("voiceCallRequesting =>: " + jsonObject);

                    if (callback != null)
                        callback.callback(jsonObject);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void hangupCall(String myId, String contactId) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("userId", myId);
            msg.put("contactId", contactId);
            msg.put("token", getServerImplemented().authenData.getString("token"));

            getServerImplemented().getClient().request("connector.entryHandler.hangupCall", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    try {
                        if(jsonObject.getInt("code") == 500) {
                            System.err.println(jsonObject.getString("message"));
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

    public void theLineIsBusy(String contactId) {
        try {
            JSONObject msg = new JSONObject();
            msg.put("contactId", contactId);

            getServerImplemented().getClient().request("connector.entryHandler.theLineIsBusy", msg, new DataCallBack() {
                @Override
                public void responseData(JSONObject jsonObject) {
                    System.out.println("theLineIsBusy response: " + jsonObject.toString());
                }
            });
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //endregion

    private class GetMeTaskAsync extends AsyncTask<Void, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            return null;
        }
    }
}

