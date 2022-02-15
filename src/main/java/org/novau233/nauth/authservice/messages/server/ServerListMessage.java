package org.novau233.nauth.authservice.messages.server;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ServerListMessage<T> implements IServerMessage,Serializable {
    private List<T> message = null;
    public ServerListMessage(){
        this.message = new LinkedList<>();
    }
    public int size(){return message.size();}
    @Override
    public Object getHead(){return "LISTMESSAGE";}
    @Override
    public Object getTag(){return this.message.get(0);}
    public List<T> getMessage(){return this.message;}
    @Override
    public boolean isMessage() {
        return true;
    }
}
