package org.bships.plugin;

import org.core.config.ConfigurationStream;
import org.ships.implementation.bukkit.configuration.YAMLConfigurationFile;
import org.ships.plugin.ShipsPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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
    public Optional<ConfigurationStream.ConfigurationFile> createConfig(String configName, File file) {
        InputStream stream = this.getLauncher().getResource(configName);
        if(stream == null){
            System.err.println("Request for '" + configName + "' could not be found");
            return Optional.empty();
        }
        try {
            file.getParentFile().mkdirs();
            Files.copy(stream, file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.of(new YAMLConfigurationFile(file));
    }

    @Override
    public Optional<InputStream> getResource(String name) {
        return Optional.ofNullable(this.getLauncher().getResource(name));
    }
}
