package xyz.ljw.netty.handle;

import com.alibaba.fastjson2.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    ThreadLocal<String> threadLocal = new ThreadLocal<>();


    private static volatile Map<String, Channel> clients = new ConcurrentHashMap<>();
    private String currentId;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        threadLocal.get();
        log.info("Client Address ====== {}，读取的信息：{}", ctx.channel().remoteAddress(),msg);
//        ctx.channel().writeAndFlush("我是炒股天才V我50,一年后反你100");
        String s = String.valueOf(msg);
        Map<String,String> map = JSON.parseObject(s, Map.class);
        String userId = map.get("userId");
        String mssage = map.get("mssage");
        String toUser = map.get("toUser");
        if (!clients.containsKey(userId)) {
            this.currentId = userId;
            log.info("新增用户：{}",userId);
            clients.put(userId, ctx.channel());
        }
        log.info("用户对象{}", clients);
        if (toUser != null && clients.containsKey(toUser)) {
            log.info("发送消息=={}", mssage);
            // 如何处理
            Map<String, String> temp = new HashMap<>();
            temp.put("userId", userId);
//            temp.put("toUser", "2");
            temp.put("mssage", mssage);
            clients.get(toUser).writeAndFlush(Unpooled.buffer().writeBytes(JSON.toJSONString(temp).getBytes(CharsetUtil.UTF_8)));
        } else {
            log.info("用户已下线或者没有消息接收者");
        }
        ctx.fireChannelActive();
        TimeUnit.SECONDS.sleep(1);
//        ctx.writeAndFlush("我收到消息".getBytes(CharsetUtil.UTF_8));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        log.info("一个客户端移除：{}", ctx.channel().remoteAddress());
        if (this.currentId != null) {
            clients.remove(currentId);
        }
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("一个客户端新增：{}", ctx.channel().remoteAddress());
        super.handlerAdded(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
