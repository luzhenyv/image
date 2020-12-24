package com.todata.image.netty.server;

import java.io.Serializable;

public class CustomProtocol implements Serializable {

    private static final long uuid = 123456L;
    private long id;
    private String content;

    public CustomProtocol() {
    }

    public CustomProtocol(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public static long getUuid() {
        return uuid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CustomProtocol{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
