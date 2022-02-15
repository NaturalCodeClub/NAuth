package org.novau233.nauth;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.novau233.nauth.authservice.AuthServiceHandler;
import org.novau233.nauth.authservice.Start;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            this.saveDefaultConfig();
            Thread.sleep(3000);
            Utils.config = this.getConfig();
            Start.start();
            Bukkit.getPluginManager().registerEvents(new AuthServiceHandler(), this);
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onDisable() {
        Start.stop();
    }
}
