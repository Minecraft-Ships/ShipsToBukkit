package org.bships.plugin;

import org.ships.plugin.ShipsPlugin;

import java.io.File;
import java.util.Optional;

public class ShipsBPlugin extends ShipsPlugin {

    @Override
    public Optional<Object> getBukkitLauncher() {
        return Optional.of(ShipsMain.getPlugin());
    }

    @Override
    public Optional<Object> getSpongeLauncher() {
        return Optional.empty();
    }

    @Override
    public File getShipsConigFolder() {
        return new File("plugins/Ships");
    }
}
