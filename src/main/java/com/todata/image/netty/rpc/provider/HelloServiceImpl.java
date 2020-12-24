package com.todata.image.netty.rpc.provider;

import com.todata.image.netty.rpc.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String mes) {
        System.out.println("receive message is " + mes);
        if (mes != null) {
            return "Hello client, I receive your message [ " + mes + " ]";
        } else {
            return "Hello client";
        }
    }
}
