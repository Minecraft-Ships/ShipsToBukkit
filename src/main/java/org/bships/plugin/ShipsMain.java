package org.bships.plugin;

import org.bships.plugin.command.debug.ShipsTest;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.CorePlugin;
import org.ships.implementation.bukkit.CoreToBukkit;
import org.ships.implementation.paper.CoreToPaper;

public class ShipsMain extends JavaPlugin {

    private ShipsBPlugin shipsPlugin;
    private static ShipsMain plugin;
    public static final boolean INVENTORY_SLOT_INDEX_SHOW = true;

    public void onEnable(){
        plugin = this;
        Bukkit.getPluginManager().registerEvents(new DebugListener(), this);
        try {
            Class.forName("com.destroystokyo.paper.event.block.BlockDestroyEvent");
            new CoreToPaper(this);
        } catch (ClassNotFoundException e) {
            System.err.println("Ships is not running on Paper. For a 'better' experience, use Paper as it runs faster and gives plugins slightly more control if they choose to use it");
            new CoreToBukkit(this);

        }
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
