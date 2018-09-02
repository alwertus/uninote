package ru.alwertus.uninote.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import ru.alwertus.uninote.common.Values;

import javax.swing.*;

public class Server {
    private int port;

    // constructor
    public Server(int port) {
        this.port = port;
    }

    // main
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ServerGUI sgui = new ServerGUI();
            }
        });
        //new Server(Values.PORT).run();
    }

    private void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // группа событий, используемая при создании каналов между серверами и клиентом
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)      // говорим серверу о том, какой типа канала используется для общения
                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ch.pipeline().addLast(
                                            new ServerHandler(),
                                            new StringDecoder(),
                                            new StringEncoder()
                                    );
                                    //ch.pipeline().addLast("handler", new ServerHandler());  // обработчик входящих сообщений от клиента
                                    //ch.pipeline().addLast("decoder", new StringDecoder());  // декодирует приходящие данные в строку
                                    //ch.pipeline().addLast("encoder", new StringEncoder());  // кодирует строку в байты при отправке
                                }
                            })
            //.option(ChannelOption.SO_BACKLOG, 128)
            //.childOption(ChannelOption.SO_KEEPALIVE, true)
            ;

            ChannelFuture f = b.bind(port).sync();
            ServerGUI.printLog("info", Values.LOG_SERVER_STARTING);

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
