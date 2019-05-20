package org.bships.plugin.command.debug;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.core.CorePlugin;
import org.core.command.BaseCommandLauncher;
import org.core.platform.Plugin;
import org.core.source.command.CommandSource;
import org.core.world.position.block.details.BlockDetails;
import org.core.world.position.block.details.blocks.GeneralBlock;
import org.ships.implementation.bukkit.platform.BukkitPlatform;
import org.ships.implementation.bukkit.world.position.block.details.blocks.AbstractBlockDetails;
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
        List<Class<? extends org.bukkit.block.data.BlockData>> bukkitDetails = new ArrayList<>();
        List<AbstractBlockDetails> coreDetails = new ArrayList<>();

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
                if(!bukkitDetails.stream().anyMatch(c -> c.isAssignableFrom(data.getClass()))){
                    bukkitDetails.add(data.getClass());
                }
                BlockDetails details = platform.createBlockDetailInstance(data);
                if(!(details instanceof GeneralBlock)){
                    compatibleBlockDetails++;
                    if(!coreDetails.stream().anyMatch(d -> d.getClass().getSimpleName().equals(details.getClass().getSimpleName()))){
                        coreDetails.add((AbstractBlockDetails) details);
                    }
                }
            }
        }
        System.out.println("BlockDetails: " + compatibleBlockDetails + "/" + totalBlockDetails);
        System.out.println("BlockDetailsTypes: " + coreDetails.size() + "/" + bukkitDetails.size());
        System.out.println("Entities: " + ((BukkitPlatform) CorePlugin.getPlatform()).getBukkitEntityToCoreEntityMap().size() + "/" + EntityType.values().length);

        System.out.println("Blocks to do: ");
        bukkitDetails.stream().filter(bd -> coreDetails.stream().noneMatch(cd -> cd.getBukkitData().getClass().isAssignableFrom(bd))).forEach(d -> System.out.println("\t- " + d.getSimpleName()));

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
