package com.todata.image.netty.rpc.consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context; //上下文
    private String result; //返回结果
    private String para; //客户端调用方法传入的参数

    // 被代理对象调用，发送给服务器，等待唤醒，返回结果
    @Override
    public synchronized Object call() throws Exception {
        context.writeAndFlush(para);
        // waiting
        wait();
        return result;
    }

    // 连接时调用,第一次调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }

    //收到数据时调用
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        result = msg.toString();
        notify(); // 唤醒等待的线程
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    void setPara(String para) {
        this.para = para;
    }
}
