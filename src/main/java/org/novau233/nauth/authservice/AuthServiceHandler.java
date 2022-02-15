package org.novau233.nauth.authservice;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.novau233.nauth.authservice.messages.client.ClientListMessage;
import org.novau233.nauth.authservice.messages.client.IClientMessage;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class AuthServiceHandler extends SimpleChannelInboundHandler<IClientMessage> implements ServiceHandler, Listener {
    private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final HashMap<Channel,String> passed = new HashMap<>();
    //检查活跃，每300秒一次
    public static long keepAliveTime = 300*1000;
    //限制活跃包发送，每200秒冷却
    public static long keepAliveTimeLimit = 200*1000;
    private static final ConcurrentHashMap<Channel,Long> lastKeepAliveTime = new ConcurrentHashMap<>();
    //处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (Bukkit.getPlayer(passed.get(ctx.channel())) != null) {
            Bukkit.getPlayer(passed.get(ctx.channel())).kickPlayer("Exception caught!");
        }
        Channel channel = ctx.channel();
        passed.remove(channel);
        lastKeepAliveTime.remove(channel);
        ctx.disconnect();
    }
    //处理客户端的消息
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, IClientMessage msg) {
        Bukkit.getLogger().info("Client message type:"+msg.getHead().toString());
        if(msg.getHead().equals("CLIENTLISTMESSAGE")&&msg.isMessage()){
            ClientListMessage<String> message = (ClientListMessage<String>)msg;
            if(message.getMessage().size()>=2){
                String tag = message.getTag().toString();
                switch (tag){
                    case "LOGIN":
                        //登录消息，文件校验还在写。。。
                        passed.put(channelHandlerContext.channel(),message.getMessage().get(1));
                        Bukkit.getLogger().info("Channel passed!Player:" + message.getMessage().get(1));
                        break;
                    case "KEEPALIVE":
                        //检查是不是大量发送保活包或者初次发包
                        if (!lastKeepAliveTime.containsKey(channelHandlerContext.channel())) {
                            lastKeepAliveTime.put(channelHandlerContext.channel(), System.currentTimeMillis());
                        } else {
                            if ((System.currentTimeMillis() - lastKeepAliveTime.get(channelHandlerContext.channel())) >= keepAliveTimeLimit){
                                Bukkit.getLogger().info("Keepalive packet read");
                                lastKeepAliveTime.replace(channelHandlerContext.channel(), System.currentTimeMillis());
                            } else {
                                //踢出大量发包的玩家和客户端
                                if (Bukkit.getPlayer(passed.get(channelHandlerContext.channel())) != null) {
                                    Bukkit.getPlayer(passed.get(channelHandlerContext.channel())).kickPlayer("Invalid keep alive message!");
                                }
                                passed.remove(channelHandlerContext.channel());
                                lastKeepAliveTime.remove(channelHandlerContext.channel());
                                channelHandlerContext.disconnect();
                            }
                        }
                        break;
                    //断开连接
                    case "DISCONNECT":
                        Bukkit.getLogger().info("Disconnecting channel:"+channelHandlerContext.channel());
                        if (Bukkit.getPlayer(passed.get(channelHandlerContext.channel())) != null) {
                            Bukkit.getPlayer(passed.get(channelHandlerContext.channel())).kickPlayer("Author disconnected");
                        }
                        channels.remove(channelHandlerContext.channel());
                        passed.remove(channelHandlerContext.channel());
                        lastKeepAliveTime.remove(channelHandlerContext.channel());
                        channelHandlerContext.disconnect();
                }
            }
        }
    }
    //初始化Timer
    public static void init(){
        Timer timer = new Timer();
        //Timer检查玩家的客户端是不是机器人或者已经死了（bushi）
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                lastKeepAliveTime.forEach((channel, time)->{
                    if((System.currentTimeMillis()-time)>keepAliveTime&&channel!=null){
                        Bukkit.getLogger().info("Channel was timeout.Disconnecting channel"+channel);
                        channel.disconnect();
                        lastKeepAliveTime.remove(channel);
                        passed.remove(channel);
                        if(passed.get(channel)!=null){
                            Bukkit.getLogger().info("Kicking timeout player:"+passed.get(channel));
                            Bukkit.getPlayer(passed.get(channel)).kickPlayer("Time out!");
                        }
                    }
                });
            }
        },keepAliveTime,keepAliveTime);
    }
    //当客户端连接的时候进行初始化
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        Bukkit.getLogger().info("Channel:"+ctx.channel().remoteAddress()+" connected.");
        channels.add(ctx.channel());
        NAuthService.channels.add(ctx.channel());
    }
    //通过的客户端和玩家
    @Override
    public HashMap<Channel, String> getPassedChannels() {
        return passed;
    }
    //客户端断开连接时删除客户端和玩家的信息
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx){
        NAuthService.channels.remove(ctx.channel());
        channels.remove(ctx.channel());
        lastKeepAliveTime.remove(ctx.channel());
        passed.remove(ctx.channel());
    }
    //获取所以客户端
    @Override
    public ChannelGroup getChannels() {
        return channels;
    }
    //玩家登陆时检查是否合法
    @EventHandler
    public static void onPlayerLogin(PlayerLoginEvent event){
         if(!passed.containsValue(event.getPlayer().getName())) {
             event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
         }
    }
}
