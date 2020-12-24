package com.todata.image.netty.rpc.provider;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("msg is " + msg);
        if (msg.toString().startsWith("HelloServer#sayHello#")) {
            String result =
                new HelloServiceImpl()
                    .sayHello(msg.toString()
                            .substring(msg.toString()
                                    .lastIndexOf("#") + 1));
            ctx.writeAndFlush(result);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
