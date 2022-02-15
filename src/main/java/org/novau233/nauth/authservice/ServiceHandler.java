package org.novau233.nauth.authservice;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public interface ServiceHandler {
    HashMap<Channel,String> getPassedChannels();
    ChannelGroup getChannels();
}
