package ru.alwertus.uninote.network;

public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection tcpConnection);                // соединение готово
    void onReceiveString(TCPConnection tcpConnection, String value);    // внезапно приняли строку
    void onDisconnect(TCPConnection tcpConnection);                     // произошёл дисконнект
    void onException(TCPConnection tcpConnection, Exception e);         // произошла ошибка


}
