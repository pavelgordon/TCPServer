package com.interra.server;

/**
 * Created by pgordon on 05.07.2017.
 */
public enum ClientThreadState {
    WAITING_FOR_COMMAND,
    WAITING_FOR_NAME,
    WAITING_FOR_SURNAME,
    WAITING_FOR_PATRONYMIC,
    WAITING_FOR_POSITION,
    WAITING_FOR_SEARCH
}
