package com.todata.image.netty.rpc.consumer;

import com.todata.image.netty.rpc.HelloService;

public class ClientBootstrap {
    private static final String providerName = "HelloServer#sayHello#";
    public static void main(String[] args){
        NettyClient client = new NettyClient();
        HelloService service = (HelloService) client.getBean(HelloService.class, providerName);
        String res = service.sayHello("Hi, DUBBO~");
        System.out.println("result is :" + res);
    }
}
