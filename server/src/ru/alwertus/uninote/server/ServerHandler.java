package ru.alwertus.uninote.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutor;
import ru.alwertus.uninote.common.Message;
import ru.alwertus.uninote.common.Values;
import ru.alwertus.uninote.network.ClientInfo;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
    // список подключённых клиентов
    private static final ChannelGroup CHANNELS = new DefaultChannelGroup(new DefaultEventExecutor());

    // информация о подключённом клиенте
    ClientInfo clientInfo = new ClientInfo(Values.DEFAULT_CLIENT_NAME);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        //System.out.println("--" + s);
        /*Channel talk = ctx.channel();
        System.out.println("channelRead0 [" + talk.remoteAddress() + "] " + s);

        for (Channel channel : CHANNELS) {
            if (channel.remoteAddress() != talk.remoteAddress()){
                channel.writeAndFlush("[" + talk.remoteAddress() + "] " + s + "\r\n");
            } else {
                channel.writeAndFlush("[yuo] " + s + "\r\n");
            }
        }*/
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //Channel talk = ctx.channel();

        Message message = new Message(msg);

        if (message.isCommand) {
            switch (message.commandCode) {
                case 0:
                    log(clientInfo.getName() + " пытается выйти");
                    ctx.close();
                    break;
                case 1:
                    log(clientInfo.getName() + " меняет имя на " + message.commandParam);
                    clientInfo.changeName(message.commandParam);
                    break;
            }
        }

        else
            log(clientInfo.getName() + " говоирт: " + message.fullMsg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        sendStrToChannel(ctx.channel(), "--------------------------------------");
        sendStrToChannel(ctx.channel(), "Велкам, " + clientInfo.getName() + ".");
        sendStrToChannel(ctx.channel(), "--------------------------------------");
        sendStrToChannel(ctx.channel(), "/quit          - выход");
        sendStrToChannel(ctx.channel(), "/name mynick   - сменить имя");
        sendStrToChannel(ctx.channel(), "--------------------------------------");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        log(clientInfo.getName() + " присоеденился");
        CHANNELS.add(incoming);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel removed = ctx.channel();
        log(clientInfo.getName() + " отключился");
        CHANNELS.remove(removed);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log("err", "Хуйня какаято случилась с " + clientInfo.getName() + ": " + cause);
        ctx.close();
    }

    private static void sendStrToChannel(Channel ch, String msg) {
        ch.writeAndFlush(msg + "\r\n");
    }


    void log(String msg) {
        log("info", msg);
    }
    void log(String category, String msg) {
        System.out.println("! " + msg);
        ServerGUI.printLog("info", msg);
    }
}
