package org.novau233.nauth.authservice;

public class Start {
    private static final IService service = new NAuthService();
    public static void start(){
        AuthServiceHandler.init();
        service.start(8);
    }
    public static void stop(){
        service.stop();
    }
}
