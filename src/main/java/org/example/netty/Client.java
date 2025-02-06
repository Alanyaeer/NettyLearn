package org.example.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.example.netty.Entity.message.ChatMessage;
import org.example.netty.Entity.message.PingMessage;
import org.example.netty.codec.MessageCodecSharable;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// 客户端代码
public class Client {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        Channel channel = bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,12,4,0,0));
                        ch.pipeline().addLast(messageCodecSharable);
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            //在连接建立之初 我们就设置好一个定时任务
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) {
                                Channel channelInner = ctx.channel();
                                scheduler.scheduleAtFixedRate(()->{
                                    if (channelInner.isActive()) {
                                        PingMessage pingMessage = new PingMessage();
                                        channelInner.writeAndFlush(pingMessage);
                                    }
                                }, 0, 4, TimeUnit.SECONDS);
                            }
                        });
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()
                .channel();

        Scanner scanner = new Scanner(System.in);
//        while(true){
            String str = "\"fajfeiafjeijfiaewf f eiajfieajfieajfaefeafj feajfiejfiea fea \nfaiefjiefjia ef\naefef\n";
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
