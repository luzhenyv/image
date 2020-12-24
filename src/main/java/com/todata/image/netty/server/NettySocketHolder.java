package com.todata.image.netty.server;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettySocketHolder {
    private static final Map<Long, NioSocketChannel> MAP = new ConcurrentHashMap<>(16);

    public static void put(long id, NioSocketChannel socketChannel) {
        MAP.put(id, socketChannel);
    }

    public static NioSocketChannel get(long id) {
        return MAP.get(id);
    }

    public static Map<Long, NioSocketChannel> getMAP() {
        return MAP;
    }

    public static void remove(NioSocketChannel nioSocketChannel) {
        MAP.entrySet().stream()
                .filter(entry -> entry.getValue() == nioSocketChannel)
                .forEach(entry -> MAP.remove(entry.getKey()));
    }
}
