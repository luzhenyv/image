package com.todata.image.netty.point2point;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameMessageServiceImpl implements GameMessageService {

    private static Map<String, Channel> appChannel = new ConcurrentHashMap<>();
    private static Map<String, Channel> webChannel = new ConcurrentHashMap<>();

    @Override
    public void onGameMessage(ChannelHandlerContext ctx, BinaryWebSocketFrame frame) {
        byte[] bytes = null;
        if (frame.content().hasArray()) {
            bytes = frame.content().array();
        } else {
            bytes = new byte[frame.content().readableBytes()];
            frame.content().getBytes(0, bytes);
        }
        ByteBuf buf = Unpooled.buffer(bytes.length);
        buf.writeBytes(bytes);

        AttributeKey<Map<String, String>> requestParam = AttributeKey.valueOf("request.params");
        Map<String, String> param = ctx.channel().attr(requestParam).get();
        String channelId = param.get("channelId");
        String user = param.get("user");

        switch (user){
            case "app":
                appChannel.putIfAbsent(channelId, ctx.channel());
                Channel web = webChannel.getOrDefault(channelId, null);
                if (web != null)
                    web.writeAndFlush(new BinaryWebSocketFrame(buf));
                break;
            case "web":
                webChannel.putIfAbsent(channelId, ctx.channel());
                Channel app = appChannel.getOrDefault(channelId, null);
                if (app != null)
                    app.writeAndFlush(new BinaryWebSocketFrame(buf));
                break;
        }
    }

    @Override
    public void removeChannel(String user, String channelId) {
        switch (user) {
            case "app":
                appChannel.remove(channelId);
                break;
            case "web":
                webChannel.remove(channelId);
                break;
        }
    }
}
