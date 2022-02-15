package org.novau233.nauth.authservice;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.bukkit.Bukkit;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class NAuthService implements IService{
    public static List<Channel> channels = Collections.synchronizedList(new ArrayList<>());
    private String host="0.0.0.0";
    private int port = 1;
    private AtomicBoolean started = new AtomicBoolean(false);
    public ChannelInitializer<SocketChannel> channel = new ChannelInitializer<SocketChannel>(){
        @Override
        public void initChannel(SocketChannel ch) {
            ch.pipeline()
                    .addLast("encoder", new ObjectEncoder())
                    .addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                    .addLast("handler", new AuthServiceHandler());
        }
    };
    private ServerBootstrap serverBootstrap = null;
    private Thread serverThread = null;
    //启动校验服务端
    @Override
    public void start(int bindPort) {
        Thread thread = new Thread(()->{
            try {
                this.port = bindPort;
                this.started.set(true);
                //初始化-1
                EventLoopGroup BaseWorker = new NioEventLoopGroup();
                EventLoopGroup WorkerGroup = new NioEventLoopGroup();
                this.serverBootstrap = new ServerBootstrap();
                //初始化-2
                this.serverBootstrap.group(BaseWorker, WorkerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(channel)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .option(ChannelOption.SO_KEEPALIVE, true);
                Bukkit.getLogger().info("Binding on:" + this.host + ":" + this.port);
                //绑定端口
                this.serverBootstrap.bind(this.host, this.port).sync();
                //Init the event groups
            } catch (Exception e) {
                //处理异常
                if(!(e instanceof InterruptedException)){
                    e.printStackTrace();
                }
                this.started.set(false);
            }
        });
        this.serverThread = thread;
        //设置优先级
        thread.setPriority(5);
        //设置为守护线程
        thread.setDaemon(true);
        thread.setName("NAuth-Thread");
        thread.start();
    }
    //停止验证服务端
    @Override
    public void stop() {
        this.serverThread.interrupt();
        this.started.set(false);
    }

    @Override
    public List<Channel> getChannels(){
        return channels;
    }
    //获取绑定的地址
    @Override
    public InetSocketAddress getBindAddress(){
        if(started.get()){
            return  new InetSocketAddress(this.host,this.port);
        }else{
            throw new IllegalStateException("Service not start!Please get this after starting server");
        }
    }

}
