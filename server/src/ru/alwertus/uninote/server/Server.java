package ru.alwertus.uninote.server;

import ru.alwertus.uninote.common.WFunc;
import ru.alwertus.uninote.network.TCPConnection;
import ru.alwertus.uninote.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements TCPConnectionListener {

    private final ArrayList<TCPConnection> connections = new ArrayList<>(); // массив соединений
    public static final int PORT = 5188;

    // точка входа
    public static void main(String[] args) {
        new Server();
    }


    // конструктор (вызывается сразу при запуске)
    private Server() {
        WFunc.log("Server running...");
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {   // слушает порт на наличие входящих подключений
            while(true) {                                           // бесконечный цикл
                try {
                    // accept возвращает Socket как только соединение установилось. его мы передаём в TCPConnection и указываем слушателя (себя - this)
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    WFunc.log("TCPConnection exception: " + e);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    // ----- реализация TCPConnectionListener

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        WFunc.log("Client connected (" + tcpConnection + ")");
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        WFunc.log(tcpConnection + " send: " + value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        WFunc.log("Client disconnected (" + tcpConnection + ")");
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        WFunc.log("Error: " + e);
        //tcpConnection.disconnect();
    }
}
