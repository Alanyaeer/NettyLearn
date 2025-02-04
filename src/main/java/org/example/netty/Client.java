package org.example.netty;

import com.google.gson.Gson;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.string.StringEncoder;
import org.example.netty.Entity.ChatMessage;
import org.example.netty.Entity.Message;
import org.example.netty.codec.MessageCodecSharable;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Scanner;

// 客户端代码
public class Client {
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
    }
}
