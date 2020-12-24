package com.todata.image.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeatSimpleHandle extends SimpleChannelInboundHandler<CustomProtocol> {

    private final static Logger log = LoggerFactory.getLogger(HeartBeatSimpleHandle.class);

    private static final ByteBuf HEART_BEAT = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(new CustomProtocol(123456L, "pong").toString(), CharsetUtil.UTF_8));

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettySocketHolder.remove((NioSocketChannel) ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.info("without info for up 5 sec...");
                ctx.writeAndFlush(HEART_BEAT).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CustomProtocol customProtocol) throws Exception {
        log.info("receive new is {}", customProtocol);

        // 保存客户端与channel之间的关系
        NettySocketHolder.put(customProtocol.getId(), (NioSocketChannel) channelHandlerContext.channel());
    }
}
