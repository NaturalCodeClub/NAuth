package org.novau233.nauth.authservice.messages.server;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerMapMessage<K,V> implements IServerMessage, Serializable {
    private ConcurrentMap<K,V> map = null;
    private String tag = null;
    public ServerMapMessage(String tag){
        this.tag = tag;
        this.map = new ConcurrentHashMap<>();
    }
    @Override
    public Object getHead(){return "MAPMESSAGE";}
    @Override
    public Object getTag(){return this.tag;}
    @Override
    public boolean isMessage() {
        return true;
    }
}
