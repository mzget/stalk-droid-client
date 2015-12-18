package com.ahoostudio.stalk.droid.stalk;

import com.netease.pomelo.DataEvent;
import com.netease.pomelo.DataListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * Created by nattapon on 7/18/15 AD.
 */
public class ServerEventListener {
    private static ServerEventListener instance;
    protected ServerImplemented serverImplemented;
    public synchronized static ServerEventListener createInstance() {
        if(instance == null) {
            instance = new ServerEventListener();
        }

        return  instance;
    }

    public interface IRTCListener extends EventListener {
        void onVideoCall(DataEvent dataEvent);
        void onVoiceCall(DataEvent dataEvent);
        void onHangupCall(DataEvent dataEvent);
        void onTheLineIsBusy(DataEvent dataEvent);
    }
    public interface IServerListener extends EventListener{
        void onGetRoomAccess(DataEvent dataEvent);
        void onUpdatedLastAccessTime(DataEvent dataEvent);
        void onAddRoomAccess(DataEvent dataEvent);

        void onCreateGroupSuccess(DataEvent dataEvent);
        void onEditedGroupMember(DataEvent dataEvent);
        void onEditedGroupName(DataEvent dataEvent);
        void onEditedGroupImage(DataEvent dataEvent);
        void onNewGroupCreated(DataEvent dataEvent);

        void onUpdateMemberInfoInProjectBase(DataEvent dataEvent);

        void onUserUpdateImageProfile(DataEvent dataEvent);
        void onUserUpdateProfile(DataEvent dataEvent);
    }
    public interface IOnChatListener extends EventListener{
        void onChatData(DataEvent data);
        void onLeaveRoom(DataEvent data);
        void onAdd(DataEvent data);
        void onMessageRead(DataEvent dataEvent);
        void onGetMessagesReaders(DataEvent dataEvent);
    }
    public interface IFrontendServerListener extends EventListener{
        void onGetMe(DataEvent dataEvent);
        void onGetCompanyInfo(DataEvent dataEvent);
        void onGetCompanyMemberComplete(DataEvent dataEvent);
        void onGetPrivateGroupsComplete(DataEvent dataEvent);
        void onGetOrganizeGroupsComplete(DataEvent dataEvent);
        void onGetProjectBaseGroupsComplete(DataEvent dataEvent);
    }


    private boolean isAddedListener =false;
    private List<IRTCListener> webRTCEventListeners = new ArrayList<>();
    public void addRTCListener(IRTCListener listener) {
        if(listener instanceof  IRTCListener) {
            webRTCEventListeners.add(listener);
        }
        else {
            System.err.println("Listener is invalid...");
        }
    }
    public void removeRTCListener (IRTCListener listener) {
        webRTCEventListeners.remove(listener);
    }

    private List<IOnChatListener> chatListeners = new ArrayList<>();
    public void addChatListener(IOnChatListener listener) {
        if(listener instanceof IOnChatListener) {
            chatListeners.add((IOnChatListener)listener);
        }
        else {
            System.err.println("Listener is invalid...");
        }
    }
    public void removeChatListener(IOnChatListener listener) {
        chatListeners.remove(listener);
    }

    private List<IServerListener> serverListeners = new ArrayList<>();
    public void addServerListener(IServerListener listener) {
        if(listener instanceof IServerListener) {
            serverListeners.add((IServerListener)listener);
        }
        else {
            System.err.println("Listener is invalid...");
        }
    }
    public void removeServerListener(IServerListener listener) {
        serverListeners.remove(listener);
    }

    public static final String ON_ADD = "onAdd";
    public static final String ON_LEAVE = "onLeave";
    public static final String ON_CHAT = "onChat";

    public static final String ON_VIDEO_CALL = "onVideoCall";
    public static final String ON_VOICE_CALL = "onVoiceCall";
    public static final String ON_HANGUP_CALL = "onHangupCall";
    public static final String ON_THE_LINE_IS_BUSY = "onTheLineIsBusy";

    public static final String ON_ACCESS_ROOMS = "onAccessRooms";
    public static final String ON_ADD_ROOM_ACCESS = "onAddRoomAccess";
    public static final String ON_UPDATED_LASTACCESSTIME = "onUpdatedLastAccessTime";

    public static final String ON_CREATE_GROUP_SUCCESS = "onCreateGroupSuccess";
    public static final String ON_EDITED_GROUP_MEMBER = "onEditGroupMembers";
    public static final String ON_EDITED_GROUP_NAME = "onEditGroupName";
    public static final String ON_EDITED_GROUP_IMAGE = "onEditGroupImage";
    public static final String ON_NEW_GROUP_CREATED = "onNewGroupCreated";
    public static final String ON_UPDATE_MEMBER_INFO_IN_PROJECTBASE = "onUpdateMemberInfoInProjectBase";

    public static final String ON_MESSAGE_READ = "onMessageRead";
    public static final String ON_GET_MESSAGES_READERS = "onGetMessagesReaders";
    public static final String ON_USER_UPDATE_IMAGE_PROFILE = "onUserUpdateImgProfile";
    public static final String ON_USER_UPDATE_PROFILE = "onUserUpdateProfile";

    //<!-- Frontend server --->
    public static final String ON_GET_ME = "onGetMe";
    public static final String ON_GET_COMPANY_INFO = "onGetCompanyInfo";
    public static final String ON_GET_COMPANY_MEMBERS = "onGetCompanyMembers";
    public static final String ON_GET_PRIVATE_GROUPS = "onGetPrivateGroups";
    public static final String ON_GET_ORGANIZE_GROUPS = "onGetOrganizeGroups";
    public static final String ON_GET_PROJECT_BASE_GROUPS = "onGetProjectBaseGroups";

    public ServerEventListener() {
        serverImplemented = ServerImplemented.getInstance();
    }

    public void startListener() {
        if(isAddedListener) return;

        //region -- Chat room --

        serverImplemented.getClient().on(ON_ADD, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(chatListeners !=null){
                    List<IOnChatListener> chats = new ArrayList<IOnChatListener>(chatListeners);
                    for (IOnChatListener listener: chats) {
                        listener.onAdd(dataEvent);
                    }
                }
            }
        });
        serverImplemented.getClient().on(ON_LEAVE, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(chatListeners != null) {
                    List<IOnChatListener> chats = new ArrayList<IOnChatListener>(chatListeners);
                    for (IOnChatListener listener: chats) {
                        listener.onLeaveRoom(dataEvent);
                    }
                }
            }
        });
        serverImplemented.getClient().on(ON_CHAT, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(chatListeners != null){
                    List<IOnChatListener> chats = new ArrayList<IOnChatListener>(chatListeners);
                    for (IOnChatListener listener: chats) {
                        listener.onChatData(dataEvent);
                    }
                }
            }
        });
        serverImplemented.getClient().on(ON_MESSAGE_READ, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(chatListeners != null) {
                    List<IOnChatListener> chats = new ArrayList<IOnChatListener>(chatListeners);
                    for (IOnChatListener listener: chats) {
                        listener.onMessageRead(dataEvent);
                    }
                }
            }
        });
        serverImplemented.getClient().on(ON_GET_MESSAGES_READERS, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(chatListeners != null) {
                    List<IOnChatListener> chats = new ArrayList<IOnChatListener>(chatListeners);
                    for (IOnChatListener listener: chats) {
                        listener.onGetMessagesReaders(dataEvent);
                    }
                }
            }
        });

        //endregion

        //region -- WEBRTC --

        serverImplemented.getClient().on(ON_VIDEO_CALL, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if (webRTCEventListeners != null) {
                    for (IRTCListener listener: webRTCEventListeners) {
                        listener.onVideoCall(dataEvent);
                    }
                }
            }
        });

        serverImplemented.getClient().on(ON_VOICE_CALL, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(webRTCEventListeners != null) {
                    for (IRTCListener listener: webRTCEventListeners) {
                    listener.onVoiceCall(dataEvent);
                }}
            }
        });

        serverImplemented.getClient().on(ON_HANGUP_CALL, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(webRTCEventListeners != null) {
                    for (IRTCListener listener: webRTCEventListeners) {
                        listener.onHangupCall(dataEvent);
                    }
                }
            }
        });

        serverImplemented.getClient().on(ON_THE_LINE_IS_BUSY, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(webRTCEventListeners != null) {
                    for (IRTCListener listener: webRTCEventListeners) {
                        listener.onTheLineIsBusy(dataEvent);
                    }
                }
            }
        });

        //endregion

        //region -- Group and User --

        serverImplemented.getClient().on(ON_CREATE_GROUP_SUCCESS, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(serverListeners != null) {
                    for (IServerListener listener: serverListeners) {
                        listener.onCreateGroupSuccess(dataEvent);
                    }
                }
            }
        });
        serverImplemented.getClient().on(ON_EDITED_GROUP_MEMBER, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(serverListeners != null) {
                    for (IServerListener listener: serverListeners) {
                        listener.onEditedGroupMember(dataEvent);
                    }
                }
            }
        });
        serverImplemented.getClient().on(ON_EDITED_GROUP_NAME, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(serverListeners != null) {
                    for (IServerListener listener: serverListeners) {
                        listener.onEditedGroupName(dataEvent);
                    }
                }
            }
        });
        serverImplemented.getClient().on(ON_EDITED_GROUP_IMAGE, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(serverListeners != null) {
                    for (IServerListener listener: serverListeners) {
                        listener.onEditedGroupImage(dataEvent);
                    }
                }
            }
        });
        serverImplemented.getClient().on(ON_NEW_GROUP_CREATED, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(serverListeners != null) {
                    for (IServerListener listener: serverListeners) {
                        listener.onNewGroupCreated(dataEvent);
                    }
                }
            }
        });
        serverImplemented.getClient().on(ON_UPDATE_MEMBER_INFO_IN_PROJECTBASE, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(serverListeners != null) {
                    for (IServerListener listener: serverListeners) {
                        listener.onUpdateMemberInfoInProjectBase(dataEvent);
                    }
                }
            }
        });

        serverImplemented.getClient().on(ON_USER_UPDATE_IMAGE_PROFILE, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(serverListeners != null) {
                    for (IServerListener listener: serverListeners) {
                        listener.onUserUpdateImageProfile(dataEvent);
                    }
                }
            }
        });
        serverImplemented.getClient().on(ON_USER_UPDATE_PROFILE, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(serverListeners != null) {
                    for (IServerListener listener: serverListeners) {
                        listener.onUserUpdateProfile(dataEvent);
                    }
                }
            }
        });


        serverImplemented.getClient().on(ON_ACCESS_ROOMS, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(serverListeners != null) {
                    for (IServerListener listener: serverListeners) {
                        listener.onGetRoomAccess(dataEvent);
                    }
                }
            }
        });

        serverImplemented.getClient().on(ON_ADD_ROOM_ACCESS, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(serverListeners != null) {
                    for (IServerListener listener: serverListeners) {
                        listener.onAddRoomAccess(dataEvent);
                    }
                }
            }
        });

        serverImplemented.getClient().on(ON_UPDATED_LASTACCESSTIME, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                if(serverListeners != null) {
                    for (IServerListener listener: serverListeners) {
                        listener.onUpdatedLastAccessTime(dataEvent);
                    }
                }
            }
        });

        //endregion

        isAddedListener = true;
    }

    public void listenFrontendEvents(final IFrontendServerListener listener) {
        serverImplemented.getClient().on(ON_GET_COMPANY_MEMBERS, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                listener.onGetCompanyMemberComplete(dataEvent);
            }
        });
        serverImplemented.getClient().on(ON_GET_PRIVATE_GROUPS, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                listener.onGetPrivateGroupsComplete(dataEvent);
            }
        });
        serverImplemented.getClient().on(ON_GET_ORGANIZE_GROUPS, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                listener.onGetOrganizeGroupsComplete(dataEvent);
            }
        });
        serverImplemented.getClient().on(ON_GET_PROJECT_BASE_GROUPS, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                listener.onGetProjectBaseGroupsComplete(dataEvent);
            }
        });
        serverImplemented.getClient().on(ON_GET_ME, new DataListener() {
                    @Override
                    public void receiveData(DataEvent dataEvent) {
                        listener.onGetMe(dataEvent);
                    }
        });
        serverImplemented.getClient().on(ON_GET_COMPANY_INFO, new DataListener() {
            @Override
            public void receiveData(DataEvent dataEvent) {
                listener.onGetCompanyInfo(dataEvent);
            }
        });
    }
}
