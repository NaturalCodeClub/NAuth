package org.novau233.nauth.authservice.messages.client;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
//客户端列表数据包，泛型了
public class ClientListMessage<T> implements IClientMessage, Serializable {
    private List<T> message = null;
    public ClientListMessage(){
        this.message = new LinkedList<>();
    }
    public int size(){return message.size();}
    @Override
    public Object getHead(){return "CLIENTLISTMESSAGE";}
    @Override
    public Object getTag(){return this.message.get(0);}
    public List<T> getMessage(){return this.message;}
    @Override
    public boolean isMessage() {
        return true;
    }
}
