package dev.splityosis.roam;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getCommand("roam").setExecutor(new RoamCMD());
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        Util.sendConsoleMessage("&aPlugin enabled");
    }

    @Override
    public void onDisable() {
        Util.sendConsoleMessage("&cPlugin disabled");
    }
}
