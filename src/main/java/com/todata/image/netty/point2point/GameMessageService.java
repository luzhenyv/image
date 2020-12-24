package com.todata.image.netty.point2point;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public interface GameMessageService {
    void onGameMessage(ChannelHandlerContext ctx, BinaryWebSocketFrame frame);
    void removeChannel(String user, String channelId);
}
