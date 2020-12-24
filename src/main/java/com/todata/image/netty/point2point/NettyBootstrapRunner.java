package com.todata.image.netty.point2point;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AttributeKey;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@Component
public class NettyBootstrapRunner implements ApplicationRunner,
        ApplicationListener<ContextClosedEvent>, ApplicationContextAware {

    @Value("${netty.websocket.port}")
    private int port;

    @Value("${netty.websocket.ip}")
    private String ip;

    @Value("${netty.websocket.path}")
    private String path;

    @Value("${netty.websocket.max-frame-size}")
    private long maxFrameSize;

    private ApplicationContext applicationContext;

    private Channel serverChannel;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(this.ip, this.port))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = serverChannel.pipeline();
                            pipeline.addLast(new HttpServerCodec()); // 处理http请求响应的编解码器
                            pipeline.addLast(new ChunkedWriteHandler()); // 处理批量写入
                            pipeline.addLast(new HttpObjectAggregator(65536)); // http请求聚合器
                            pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                // 处理query
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    if (msg instanceof FullHttpRequest) {
                                        AttributeKey<Map<String, String>> requestParam = AttributeKey.valueOf("request.params");
                                        FullHttpRequest request = (FullHttpRequest) msg;
                                        String uri = request.uri();
                                        String[] splittedUri = uri.split("\\?");

                                        HashMap<String, String> params = new HashMap<String, String>();
                                        request.setUri(splittedUri[0]);
                                        if(splittedUri.length > 1){
                                            String queryString = splittedUri[1];
                                            for(String param:queryString.split("&")){
                                                String[] keyValue = param.split("=");
                                                if (keyValue.length >= 2){
                                                    System.out.println("uri = " + splittedUri[0]);
                                                    System.out.println("key = " + keyValue[0]);
                                                    System.out.println("value = " + keyValue[1]);
                                                    params.put(keyValue[0], keyValue[1]);
                                                }
                                            }
                                        }
                                        ctx.channel().attr(requestParam).set(params);
                                    }
                                    super.channelRead(ctx, msg);
                                }
                            });
                            pipeline.addLast(new WebSocketServerCompressionHandler());
                            pipeline.addLast(new WebSocketServerProtocolHandler(path)); // 处理websocket消息
                            pipeline.addLast(applicationContext.getBean(WebsocketMessageHandler.class)); // 处理主业务
                        }
                    });
            Channel channel = serverBootstrap.bind().sync().channel();
            this.serverChannel = channel;
            System.out.println("websocket start, ip is [ " + this.ip + "], port is [ " + this.port + "]");
            channel.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {

    }
}
