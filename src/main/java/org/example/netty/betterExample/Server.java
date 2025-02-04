package org.example.netty.betterExample;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class Server {
    // 前面的代码非常好，但是它没有办法对发送的消息进行合适的拆包和粘包处理
    // 该怎么办呢？ 这里有一种自定义协议的方式来处理这个问题
    public static void main(String[] args) throws InterruptedException {
        // 1. 创建 EventLoopGroup（通常分为 bossGroup 和 workerGroup）
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 默认使用的是电脑的线程数乘以二
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    // 这里和下面的NioSocketChannel的区别在于呢？ 下面的用于处理事件的读写而当前这个用于处理事件的连接。
                    .channel(NioServerSocketChannel.class)
                    .childHandler(
                            new ChannelInitializer<NioSocketChannel>() {
                                @Override
                                protected void initChannel(NioSocketChannel ch) throws Exception {
                                    ch.pipeline().addLast(new StringDecoder());
                                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            System.out.println("receive msg:\n" + msg);
                                        }

                                    });
                                }
                            }
                    );
            // 3. 绑定端口并启动服务
            ChannelFuture f = b.bind(8080).sync();
            f.channel().closeFuture().sync();
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
