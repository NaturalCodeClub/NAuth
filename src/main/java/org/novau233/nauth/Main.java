package org.novau233.nauth;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.novau233.nauth.authservice.AuthServiceHandler;
import org.novau233.nauth.authservice.Start;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Start.start();
        Bukkit.getPluginManager().registerEvents(new AuthServiceHandler(),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Start.stop();
    }
}
