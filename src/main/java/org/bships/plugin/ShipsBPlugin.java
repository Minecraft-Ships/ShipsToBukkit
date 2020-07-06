package org.bships.plugin;

import org.ships.plugin.ShipsPlugin;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;

public class ShipsBPlugin extends ShipsPlugin {

    @Override
    public File getShipsConigFolder() {
        return new File("plugins/Ships");
    }

    @Override
    public ShipsMain getLauncher() {
        return ShipsMain.getPlugin();
    }

    @Override
    public Optional<InputStream> getResource(String name) {
        return Optional.ofNullable(this.getLauncher().getResource(name));
    }
}
