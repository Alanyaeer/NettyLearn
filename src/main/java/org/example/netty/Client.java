package org.example.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.example.netty.Entity.MySerializer;
import org.example.netty.Entity.message.ChatMessage;
import org.example.netty.Entity.message.PingMessage;
import org.example.netty.codec.MessageCodecSharable;
import org.example.netty.config.Config;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// 客户端代码
public class Client {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static void main(String[] args) throws InterruptedException {
        PingMessage pingMessage = new PingMessage();
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        Channel channel = bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(0, 3, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,12,4,0,0));
                        ch.pipeline().addLast(messageCodecSharable);
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                if(evt instanceof IdleStateEvent){
                                    IdleStateEvent event = (IdleStateEvent) evt;
                                    if(event.state() == IdleState.WRITER_IDLE){
                                        ctx.writeAndFlush(new PingMessage());
                                    }
                                }
                            }
                        });
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()
                .channel();

        Scanner scanner = new Scanner(System.in);
//        while(true){
            String str = "\"Hello can you hear me\n";
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(str);
            channel.writeAndFlush(chatMessage);
//        }
        while(true){
            Thread.sleep(2000);
            System.out.println((channel.isActive() ? "连接存活" : "连接中断"));
        }
    }
}
