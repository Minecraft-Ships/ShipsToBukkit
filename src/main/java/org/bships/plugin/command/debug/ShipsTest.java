package org.bships.plugin.command.debug;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataHolder;
import org.core.CorePlugin;
import org.core.command.CommandLauncher;
import org.core.entity.living.human.player.LivePlayer;
import org.core.platform.Plugin;
import org.core.source.command.CommandSource;
import org.ships.implementation.bukkit.platform.BukkitPlatform;
import org.ships.plugin.ShipsPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class ShipsTest implements CommandLauncher {
    @Override
    public String getName() {
        return "shipstest";
    }

    @Override
    public String getDescription() {
        return "Test compatibility between ShipsCore and Bukkit";
    }

    @Override
    public boolean hasPermission(CommandSource source) {
        return !(source instanceof LivePlayer);
    }

    @Override
    public String getUsage(CommandSource source) {
        return "ShipsTest";
    }

    @Override
    public Plugin getPlugin() {
        return ShipsPlugin.getPlugin();
    }

    @Override
    public boolean run(CommandSource source, String... args) {
        try {
            File bukkitFile = new File(Bukkit.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            JarFile bukkitJar = new JarFile(bukkitFile);
            Enumeration<JarEntry> entries = bukkitJar.entries();
            while(entries.hasMoreElements()){
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()){
                    continue;
                }
                if (!entry.getName().endsWith(".class")){
                    continue;
                }
                if (!entry.getName().startsWith("org/bukkit/block")){
                    continue;
                }
                try {
                    Class target = Class.forName(entry.getName().replaceAll("/", ".").substring(0, entry.getName().length() - 6));
                    if (!org.bukkit.block.BlockState.class.isAssignableFrom(target)){
                        continue;
                    }
                    boolean check = ((BukkitPlatform)CorePlugin.getPlatform()).getBukkitBlockStateToCoreTileEntity().keySet().stream().anyMatch(e -> (e.getName() + ".class").equals(entry.getName().replaceAll("/", ".")));
                    if(check){
                        continue;
                    }
                    if (target.getInterfaces().length == 1 && PersistentDataHolder.class.isAssignableFrom(target.getInterfaces()[0]) && target.getDeclaredMethods().length == 0){
                        continue;
                    }
                }catch (ClassNotFoundException e){
                    continue;
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        if(args.length != 0){
            return true;
        }
        Set<Class<? extends Entity>> doneEntities = ((BukkitPlatform)CorePlugin.getPlatform()).getBukkitEntityToCoreEntityMap().keySet();
        Stream.of(EntityType.values())
                .filter(et -> doneEntities.stream()
                        .noneMatch(de -> {
                            Class<? extends Entity> eClass = et.getEntityClass();
                            if(eClass != null){
                                return eClass.isAssignableFrom(de);
                            }
                            return false;
                        }))
                .forEach(e -> System.out.println("\t- " + e.name()));
        return true;
    }

    @Override
    public List<String> tab(CommandSource source, String... args) {
        List<String> list = new ArrayList<>();
        if(args.length == 0){
            for(org.bukkit.Material material : org.bukkit.Material.values()) {
                if (material.isBlock()) {
                    list.add(material.name().toLowerCase());
                }
            }
        }else{
            String target = args[args.length - 1];
            for(org.bukkit.Material material : org.bukkit.Material.values()) {
                if (material.isBlock()) {
                    if(material.name().toLowerCase().startsWith(target.toLowerCase())) {
                        list.add(material.name().toLowerCase());
                    }
                }
            }
        }
        return list;
    }
}
