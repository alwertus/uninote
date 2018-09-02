package ru.alwertus.uninote.common;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
    public String fullMsg;
    public boolean isCommand;
    public String commandName;
    public String commandParam;
    public int commandCode;

    public Message(String message) {
        fullMsg = message;

        if (message.charAt(0) == '/') isCommand = true;
        else isCommand = false;

        if (isCommand) {
            Matcher m;

            m = Pattern.compile("\\A(/)[a-zA-Z]+").matcher(message);
            if (m.find()) commandName = m.group().substring(1).toLowerCase();
            else commandName = "noname";

            m = Pattern.compile("[^ ]+$").matcher(message);
            if (m.find()) commandParam = m.group();
            else commandParam = "";

            // проверяем на команды. каждая команда имеет свой код
            commandCode = -1;
            switch (commandName) {
                case "name": commandCode = 1;
                break;
                case "quit": commandCode = 0;
                break;
            }
        }
    }

    public Message(Object obj) throws Exception {
        this(Message.getMsgFromIncommingObject(obj));
    }

    public static String getMsgFromIncommingObject(Object obj) throws Exception {
        String s = "";
        ByteBuf in = (ByteBuf) obj;

        int byteArrLength = in.readableBytes();
        s = in.readCharSequence(byteArrLength, Charset.forName("utf-8")).toString();

        String strResult = "";
        Matcher m = Pattern.compile(".+").matcher(s);
        if (m.find()) strResult = m.group();

        return strResult;
    }
}
