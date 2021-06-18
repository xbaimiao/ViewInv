package com.xbaimiao.viewinv;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class ViewInv extends JavaPlugin {
    public static ViewInv instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new MessageEvents(), this);
        regCommand("vi");
        regCommand("viewinv");
    }

    private void regCommand(String command) {
        PluginCommand c = instance.getCommand(command);
        if (c != null) c.setExecutor(new ReloadCommands());
    }

}
