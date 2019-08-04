package org.bships.plugin;

import org.bships.plugin.command.debug.ShipsTest;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.CorePlugin;
import org.ships.implementation.bukkit.CoreToBukkit;

public class ShipsMain extends JavaPlugin {

    private ShipsBPlugin shipsPlugin;
    private static ShipsMain plugin;

    public void onEnable(){
        plugin = this;
        new CoreToBukkit(this);
        shipsPlugin = new ShipsBPlugin();
        CorePlugin.getServer().registerCommands(new ShipsTest());
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            this.shipsPlugin.loadCustomShipType();
            this.shipsPlugin.loadVessels();
            this.shipsPlugin.getLoadedMessages();
        });
    }

    public void onDisable(){
        this.shipsPlugin.getDebugFile().writeToDebug();
    }

    public static ShipsMain getPlugin(){
        return plugin;
    }
}
