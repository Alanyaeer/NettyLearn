package org.example.netty.codec;

import com.google.gson.Gson;
import com.sun.org.slf4j.internal.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.example.netty.Entity.MySerializer;
import org.example.netty.Entity.message.ChatMessage;
import org.example.netty.Entity.message.Message;
import org.example.netty.config.Config;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.List;
/**
 * #################################################################################################
 * ##########                  【自定义】消息 编解码 类   【 支持@Sharable 】                   ########
 * ##########   父类 MessageToMessageCodec 认为是完整的信息 【所以必须保证上一个处理器是 帧解码器】 ########
 * #################################################################################################
 * 相当于两个handler合二为一，【既能入站 也能做出站处理】
 *  <b>魔数     </b>，用来在第一时间判定是否是无效数据包
 *  <b>版本号   </b>，可以支持协议的升级
 *  <b>序列化算法</b>，消息正文到底采用哪种序列化反序列化方式，可以由此扩展，例如：json、protobuf、hessian、jdk
 *  <b>指令类型  </b>，是登录、注册、单聊、群聊... 跟业务相关
 *  <b>请求序号  </b>，为了双工通信，提供异步能力
 *  <b>正文长度  </b>
 *  <b>消息正文  </b>
 */

@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {



    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List out) throws Exception {

        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(new byte[]{1, 2, 3, 4}); // 4个字节 魔数
        buffer.writeByte(1);  //1 个字节 消息协议的版本
        // 1 代表Gson 后续我们可以根据实际情况进行扩展
        buffer.writeByte(1); // 1个字节 序列化方式
        buffer.writeByte(msg.getMessageType()); // 1个字节 消息的类型
        buffer.writeInt(msg.getSequenceId()); // 4个字节 消息的序列号ID
        buffer.writeByte(0xff); // 1个字节
        MySerializer.Algorith mySerializerAlgorithm = Config.getMySerializerAlgorithm();
        byte[] bytes = mySerializerAlgorithm.serializer(msg);
        buffer.writeInt(bytes.length); // 内容长度
        buffer.writeBytes(bytes);
        System.out.println(bytes);
        out.add(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List out) throws Exception {
        int magicNum = msg.readInt();
        byte version = msg.readByte();
        byte serializerType = msg.readByte();
        byte messageType = msg.readByte();
        int sequenceId = msg.readInt();
        byte padding = msg.readByte();
        int contentLength = msg.readInt();

        final byte[] bytes = new byte[contentLength];
        msg.readBytes(bytes, 0, contentLength); // 读取进来，下面再进行 解码
        MySerializer.Algorith mySerializerAlgorithm = Config.getMySerializerAlgorithm();
        Object result =  mySerializerAlgorithm.deserializer(Message.getMessageClass(messageType), bytes);
        out.add(result);
    }


}
