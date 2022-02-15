package org.novau233.nauth.authservice.messages.server;

import java.io.Serializable;
//服务端的数据包。后面封禁玩家的时候会用到
public interface IServerMessage extends Serializable {
    public boolean isMessage();
    public Object getHead();
    public Object getTag();
}
