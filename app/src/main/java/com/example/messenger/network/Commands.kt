package com.example.messenger.network

enum class Commands {
    START_CONNECTION,
    REGISTER_USER,
    DISCONNECT,
    CHANGE_USERNAME,
    CHECK_USER_EXISTENCE,
    CREATE_CHAT,
    ADD_PARTICIPANT,
    SEND_MESSAGE,
    GET_MESSAGES,
    ERROR,
    GET_MESSAGE,
    CHANGE_SERVER,

    // новые для регистрации/аутентификации
    CHECK_PHONE_EXISTENCE,
    VALIDATE_CREDENTIALS,

    // новые для управления аккаунтом
    CHANGE_PHONE_NUMBER,
    CHANGE_PASSWORD,
    DELETE_USER,

    LOGIN_USER,

    GET_OR_CREATE_CHAT,

    GET_ALL_CHATS
}