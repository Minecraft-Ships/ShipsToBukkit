package org.bships.plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DebugListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(!ShipsMain.INVENTORY_SLOT_INDEX_SHOW){
            return;
        }
    }
}
