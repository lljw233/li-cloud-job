package xyz.ljw.netty.client;

import com.alibaba.fastjson2.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import xyz.ljw.netty.handle.ServerHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class NettyClient1 {

    public static void main(String[] args) {
        EventLoopGroup work = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(work).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    //对workerGroup的SocketChannel设置处理器
                    ChannelPipeline pipeline = ch.pipeline();
                    // 对于通道加入解码器
                    pipeline.addLast("decoder", new StringDecoder());

                    // 对于通道加入加码器
                    pipeline.addLast("encoder", new StringDecoder());

                    // 加入事件回调处理器
                    pipeline.addLast(new ServerHandler());
                }
            });
            ChannelFuture cf = bootstrap.connect("localhost", 11000).sync();
            Channel channel = cf.channel();
            System.out.println("+++++++" + channel.localAddress() + "=======");
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String next = scanner.next();
                // 发送到服务端
                Map<String, String> message = new HashMap<>();
                message.put("userId", "2");
                message.put("toUser", "1");
                message.put("mssage", next);
                String msg = JSON.toJSONString(message);
                channel.writeAndFlush(Unpooled.buffer().writeBytes(msg.getBytes(CharsetUtil.UTF_8)));
            }

            // 通过sync方法同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成，内部调用的是Object的wait()方法
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
