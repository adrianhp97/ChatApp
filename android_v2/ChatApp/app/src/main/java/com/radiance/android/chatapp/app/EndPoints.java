package com.radiance.android.chatapp.app;

public class EndPoints {

    // localhost url
    // public static final String BASE_URL = "http://192.168.0.101/android_login_api/v1";

    public static final String BASE_URL = "http://ambis.000webhostapp.com/android_login_api/v1";
    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String REGISTER = BASE_URL + "/user/register";
    public static final String VERIFIED = BASE_URL + "/user/verified/_ID_/_PASSWORD_";
    public static final String UPDATEGCM = BASE_URL + "/user/updateGCM";
    public static final String USER = BASE_URL + "/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_ROOMS_BY_ID = BASE_URL + "/chat_rooms_by_id/_ID_";
    public static final String CREATE_ROOM= BASE_URL + "/chat_room/create";
    public static final String JOIN_ROOM = BASE_URL + "/chat_room/join/_ID_";
    public static final String INVITE_ROOM = BASE_URL + "/chat_room/invite/";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
    public static final String EVENTS = BASE_URL + "/events";
    public static final String NEW_EVENT = BASE_URL + "/events/new_event";
    public static final String DELETE_EVENT = BASE_URL + "/events/delete_event";
}