package ru.alwertus.uninote.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import ru.alwertus.uninote.common.Values;

import javax.swing.*;

public class Client {
    private String host = "localhost";
    private int port;

    // constructor
    public Client(int port) {
        this.port = port;
    }

    // enter point
    public static void main(String[] args) throws Exception {
        //new Client(Values.PORT).run();
        startGUI();

    }

    private static void startGUI() {
        JFrame frame = new JFrame("clientwindow");
        frame.setContentPane(new FormMain().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // start client
    private void run() throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline().addLast(new ClientHandler());
                }
            });

            // start the client
            ChannelFuture f = b.connect(host, port).sync();

            // wait until the connection is closed
            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}
