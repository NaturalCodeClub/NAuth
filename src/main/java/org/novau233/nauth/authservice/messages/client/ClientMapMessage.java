package org.novau233.nauth.authservice.messages.client;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
//客户端映射表数据包（用不着。。。）
public class ClientMapMessage<K,V> implements IClientMessage, Serializable {
    private ConcurrentMap<K,V> map = null;
    private String tag = null;
    public ClientMapMessage(String tag){
        this.tag = tag;
        this.map = new ConcurrentHashMap<>();
    }
    @Override
    public Object getHead(){return "CLIENTMAPMESSAGE";}
    @Override
    public Object getTag(){return this.tag;}
    @Override
    public boolean isMessage() {
        return true;
    }
}
