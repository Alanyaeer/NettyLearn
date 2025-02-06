package org.example.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.example.netty.codec.MessageCodecSharable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Server {
    // 连接状态管理机
    public static void main(String[] args) throws InterruptedException {



        // 1. 创建 EventLoopGroup（通常分为 bossGroup 和 workerGroup）
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 默认使用的是电脑的线程数乘以二
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        MessageCodecSharable MESSAGE_CODEC_SHARABLE = new MessageCodecSharable();
        try{
            ServerBootstrap b = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    // 这里和下面的NioSocketChannel的区别在于呢？ 下面的用于处理事件的读写而当前这个用于处理事件的连接。
                    .channel(NioServerSocketChannel.class)
                    .childHandler(
                            new ChannelInitializer<NioSocketChannel>() {
                                @Override
                                protected void initChannel(NioSocketChannel ch) throws Exception {
                                    ch.pipeline().addLast(new IdleStateHandler(10, 0, 0));
                                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,12,4,0,0));
                                    ch.pipeline().addLast(MESSAGE_CODEC_SHARABLE);
                                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                        @Override
                                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt){
                                            if(evt instanceof IdleStateEvent){
                                                IdleStateEvent event = (IdleStateEvent) evt;
                                                if(event.state() == IdleState.READER_IDLE){
                                                    System.out.println("触发超时断开的连接");
                                                    ctx.close();
                                                }
                                                else if(event.state() == IdleState.ALL_IDLE){
                                                    System.out.println("触发超时断开的连接");
                                                    ctx.close();
                                                }
                                            }
                                        }
                                    });
                                    ch.pipeline().addLast(new StringDecoder());
                                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
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
