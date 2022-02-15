package org.novau233.nauth.authservice;

import org.novau233.nauth.Utils;

public class Start {
    private static final IService service = new NAuthService();
    public static void start(){
        int port = Utils.config.getInt("AuthService.port");
        AuthServiceHandler.init();
        service.start(port);
    }
    public static void stop(){
        service.stop();
    }
}
