package org.novau233.nauth.authservice.messages.client;

import java.io.Serializable;
//客户端数据包
public interface IClientMessage extends Serializable{
    public boolean isMessage();
    public Object getHead();
    public Object getTag();
}
