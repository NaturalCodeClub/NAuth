package org.novau233.nauth.authservice;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.List;

public interface IService {
    void start(int bindPort);
    void stop();
    List<Channel> getChannels();
    InetSocketAddress getBindAddress();

}
