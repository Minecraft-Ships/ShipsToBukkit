package org.bships.plugin.command.debug;

import org.bukkit.entity.EntityType;
import org.core.CorePlugin;
import org.core.command.BaseCommandLauncher;
import org.core.platform.Plugin;
import org.core.source.command.CommandSource;
import org.core.world.position.block.details.BlockDetails;
import org.core.world.position.block.details.blocks.GeneralBlock;
import org.ships.implementation.bukkit.platform.BukkitPlatform;
import org.ships.plugin.ShipsPlugin;

import java.util.ArrayList;
import java.util.List;

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
        int totalBlockDetails = 0;
        int compatibleBlockDetails = 0;
        BukkitPlatform platform = ((BukkitPlatform)CorePlugin.getPlatform());
        for(org.bukkit.Material material : org.bukkit.Material.values()){
            if(material.isBlock()){
                org.bukkit.block.data.BlockData data = material.createBlockData();
                for(String arg : args){
                    if(data.getMaterial().name().equalsIgnoreCase(arg)){
                        System.out.println(data.getMaterial().name() + ": " + data.getClass().getSimpleName());
                    }
                }
                if(data.getClass().getSimpleName().equals("CraftBlockData")){
                    continue;
                }
                totalBlockDetails++;
                BlockDetails details = platform.createBlockDetailInstance(data);
                if(!(details instanceof GeneralBlock)){
                    compatibleBlockDetails++;
                }
            }
        }
        System.out.println("BlockDetails: " + compatibleBlockDetails + "/" + totalBlockDetails);
        System.out.println("Entities: " + ((BukkitPlatform)CorePlugin.getPlatform()).entityToEntity.size() + "/" + EntityType.values().length);
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
