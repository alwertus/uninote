package ru.alwertus.uninote.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {                                    // одно соединение
    private final Socket socket;                                // сокет
    private final Thread rxThread;                              // поток слушает входящие сообщения
    private final TCPConnectionListener eventListener;          // интерфейс для использования в разных ситуациях
    private final BufferedReader in;                            //
    private final BufferedWriter out;                           //

    private String login = "";                            // имя пользователя
    private boolean isAutorized = false;                  // авторизован?
    private boolean isHasName = false;                    // имеет имя?


    // конструктор создание изнутри
    public TCPConnection(TCPConnectionListener eventListerer, String ipAddr, int port) throws IOException {
        this(eventListerer, new Socket(ipAddr, port));
    }

    // конструктор принимает сокет, устанавливает соединение (кто-то снаружи создаст сокет)
    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {                                 // слушаем входящие соединения
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while(!rxThread.isInterrupted()) {          // пока поток не прерван
                        String msg = in.readLine();
                        if (in==null) {
                            System.out.println("PUK");
                            disconnect();
                        }
                        eventListener.onReceiveString(TCPConnection.this, in.readLine());
                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();                                       // запускаем поток
    }

    // отправить сообщение
    public synchronized void sendString(String value) {
        try {
            out.write(value + "\r\n");                          // записать в буфер строку
            out.flush();                                        // отправить

        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    // разорвать соединение
    public synchronized void disconnect() {
        rxThread.interrupt();
        try {
            socket.close();
            System.out.println("JOPA");
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            System.out.println("PIZDA");
        }
    }

    @Override
    public String toString() {
        return login;
    }

    public String getInfo() {
        return "TCPConnection" + socket.getInetAddress() + ": " + socket.getPort();
    }


}
