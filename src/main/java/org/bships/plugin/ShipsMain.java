package org.bships.plugin;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.command.CommandLauncher;
import org.core.command.CommandRegister;
import org.ships.config.configuration.ShipsConfig;
import org.ships.implementation.bukkit.CoreToBukkit;
import org.ships.implementation.bukkit.command.BCommand;
import org.ships.implementation.paper.CoreToPaper;
import org.ships.plugin.ShipsPlugin;

import java.util.Optional;

public class ShipsMain extends JavaPlugin {

    public static final boolean INVENTORY_SLOT_INDEX_SHOW = true;
    private static ShipsMain plugin;
    private ShipsBPlugin shipsPlugin;

    public static ShipsMain getPlugin() {
        return plugin;
    }

    public void onEnable() {
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
        shipsPlugin.registerPlugin();
        CommandRegister register = new CommandRegister();
        shipsPlugin.registerCommands(register);
        for (CommandLauncher command : register.getCommands()) {
            JavaPlugin plugin = (JavaPlugin) command.getPlugin().getLauncher();
            PluginCommand command2 = plugin.getCommand(command.getName());
            BCommand command3 = new BCommand(command);
            command2.setTabCompleter(command3);
            command2.setExecutor(command3);
        }

        PluginCommand testCMD = Bukkit.getPluginCommand("shipstest");
        shipsPlugin.registerReady();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            this.shipsPlugin.loadCustomShipType();
            this.shipsPlugin.loadVesselTypeFlagData();
            this.shipsPlugin.loadVessels();
            this.shipsPlugin.getLoadedMessages();

            ShipsConfig config = ShipsPlugin.getPlugin().getConfig();
            if (config.isUpdateEnabled()) {
                ShipsUpdater updater = new ShipsUpdater();
                Optional<ShipsUpdater.VersionInfo> opInfo = updater.shouldUpdate();
                if (opInfo.isPresent()) {
                    System.out.println("-----[Ships Update Information][Start]-----");
                    System.out.println("Current Version: " + updater.getLocalVersion().toName());
                    System.out.println("Latest version: " + opInfo.get().toName());
                    System.out.println("-----[Ships Update Information][End]-----");
                }
            }
        });
    }

    public void onDisable() {
        this.shipsPlugin.getDebugFile().writeToDebug();
    }
}
