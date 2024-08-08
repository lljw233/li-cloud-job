package xyz.ljw.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import xyz.ljw.netty.handle.ServerHandler;

@Slf4j
public class NettyServer {

    private static final Integer NETTY_SERVER_PORT = 11000;

    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline channelPipeline = ch.pipeline();
                            channelPipeline.addLast(new StringEncoder());
                            channelPipeline.addLast(new StringDecoder());
//                            channelPipeline.addLast(new WebSocketServerProtocolHandler("/ljw"));
                            // 添加一个处理器
                            channelPipeline.addLast(new ServerHandler());
                        }
                    });
            ChannelFuture future = server.bind(NETTY_SERVER_PORT).sync();
            future.addListener(channelFuture -> {
                if (channelFuture.isSuccess()) {
                    log.info("服务启动成功！");
                } else {
                    log.info("服务启动中..");
                }
            });
            future.channel().closeFuture().sync();
            log.info("===============");

        } catch (Exception e) {
            log.info("服务启动异常：{}", e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
