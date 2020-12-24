package com.todata.image.netty.point2point;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@ChannelHandler.Sharable
@Component
public class WebsocketMessageHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    @Autowired
    private GameMessageService gameMessageService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BinaryWebSocketFrame binaryWebSocketFrame) throws Exception {
        this.gameMessageService.onGameMessage(channelHandlerContext, binaryWebSocketFrame);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " starts connecting!");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " stops connecting!");

        AttributeKey<Map<String, String>> requestParam = AttributeKey.valueOf("request.params");
        Map<String, String> param = ctx.channel().attr(requestParam).get();
        String channelId = param.get("channelId");
        String user = param.get("user");
        gameMessageService.removeChannel(user, channelId);
    }
}
