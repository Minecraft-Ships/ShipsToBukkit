package org.bships.plugin;

import org.ships.plugin.ShipsPlugin;

import java.io.File;
import java.util.Optional;

public class ShipsBPlugin extends ShipsPlugin {

    @Override
    public Optional<String> checkForUpdates() {
        return Optional.empty();
    }

    @Override
    public Object getBukkitLauncher() {
        return ShipsMain.getPlugin();
    }

    @Override
    public Object getSpongeLauncher() {
        return null;
    }

    @Override
    public File getShipsConigFolder() {
        return new File("plugins/Ships");
    }
}
