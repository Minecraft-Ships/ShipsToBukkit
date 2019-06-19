package org.bships.plugin.command.debug;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.core.CorePlugin;
import org.core.command.BaseCommandLauncher;
import org.core.platform.Plugin;
import org.core.source.command.CommandSource;
import org.ships.implementation.bukkit.platform.BukkitPlatform;
import org.ships.implementation.bukkit.world.position.block.details.blocks.BBlockDetails;
import org.ships.plugin.ShipsPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ShipsTest implements BaseCommandLauncher {
    @Override
    public String getName() {
        return "shipstest";
    }

    @Override
    public String getDescription() {
        return "Test compatibility between ShipsCore and Bukkit";
    }

    @Override
    public String getPermission() {
        return "shipscore.cmd.shipstest";
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
        for(String id : args) {
            CorePlugin.getPlatform().getBlockType("minecraft:" + id).ifPresent(bt -> {
                System.out.println("--[" + bt.getName() + "]--");
                BlockData data = ((BBlockDetails)bt.getDefaultBlockDetails()).getBukkitData();
                for (Class<?> inta : data.getClass().getInterfaces()){
                    System.out.println("\t" + inta.getName());
                }
            });
        }
        if(args.length != 0){
            return true;
        }
        System.out.println("Entities: " + ((BukkitPlatform) CorePlugin.getPlatform()).getBukkitEntityToCoreEntityMap().size() + "/" + EntityType.values().length);
        Set<Class<? extends Entity>> doneEntities = ((BukkitPlatform)CorePlugin.getPlatform()).getBukkitEntityToCoreEntityMap().keySet();
        System.out.println("Entities to do:");
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
