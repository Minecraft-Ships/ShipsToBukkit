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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

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
        List<String> bukkitDetails = new ArrayList<>();
        List<String> coreDetails = new ArrayList<>();

        List<String> itemtypesMaker = new ArrayList<>();

        int totalBlockDetails = 0;
        int compatibleBlockDetails = 0;
        BukkitPlatform platform = ((BukkitPlatform)CorePlugin.getPlatform());
        for(org.bukkit.Material material : org.bukkit.Material.values()){
            if(material.isItem()){
                itemtypesMaker.add("public static final ItemType " + material.name() + " = CorePlugin.getPlatform().get(new ItemTypes1V13(\"" + material.getKey().toString() + "\"));");
            }
            if(material.isBlock()){
                org.bukkit.block.data.BlockData data = material.createBlockData();

                String baseId = data.getAsString();
                if(baseId.contains("[")){
                    baseId = baseId.split(Pattern.quote("["))[0];
                }
                for(String arg : args){
                    if(data.getMaterial().name().equalsIgnoreCase(arg)){
                        System.out.println(data.getMaterial().name() + ": " + data.getClass().getSimpleName());
                    }
                }
                if(data.getClass().getSimpleName().equals("CraftBlockData")){
                    continue;
                }
                totalBlockDetails++;
                if(!bukkitDetails.contains(data.getClass().getSimpleName())){
                    bukkitDetails.add(data.getClass().getSimpleName());
                }
                BlockDetails details = platform.createBlockDetailInstance(data);
                if(!(details instanceof GeneralBlock)){
                    compatibleBlockDetails++;
                    if(!coreDetails.contains(details.getClass().getSimpleName())){
                        coreDetails.add(details.getClass().getSimpleName());
                    }
                }
            }
        }
        System.out.println("BlockDetails: " + compatibleBlockDetails + "/" + totalBlockDetails);
        System.out.println("BlockDetailsTypes: " + coreDetails.size() + "/" + bukkitDetails.size());
        System.out.println("Entities: " + CorePlugin.getPlatform().getEntityTypes().size() + "/" + EntityType.values().length);

        File file = new File("Testing/1.13 Items.txt");
        itemtypesMaker.sort(Comparator.naturalOrder());
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            itemtypesMaker.forEach(f -> {
                try {
                    fileWriter.write(f + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
