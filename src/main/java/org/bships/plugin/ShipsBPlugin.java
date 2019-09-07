package org.bships.plugin;

import org.ships.plugin.ShipsPlugin;

import java.io.File;

public class ShipsBPlugin extends ShipsPlugin {

    @Override
    public File getShipsConigFolder() {
        return new File("plugins/Ships");
    }

    @Override
    public ShipsMain getLauncher() {
        return ShipsMain.getPlugin();
    }
}
