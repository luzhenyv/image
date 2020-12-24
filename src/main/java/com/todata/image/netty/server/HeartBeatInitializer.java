package com.todata.image.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

public class HeartBeatInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast(new IdleStateHandler(5, 0, 0))
                .addLast(new HeartBeatDecoder())
                .addLast(new HeartBeatSimpleHandle());
    }
}
