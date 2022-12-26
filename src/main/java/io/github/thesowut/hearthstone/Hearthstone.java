package io.github.thesowut.hearthstone;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;

public final class Hearthstone extends JavaPlugin {
    private FileConfiguration _config = this.getConfig();
    private ItemStack _hearthstoneItem;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage("Enabling plugin");
        getServer().getPluginManager().registerEvents(new HearthstoneListener(), this);

        _hearthstoneItem = this._getHearthStone();
        _config.addDefault("homes", new HashMap<String, Object>());
        _config.options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage("Disabling plugin");
    }

    public class HearthstoneListener implements Listener {

        /**
         * TESTING PURPOSES
         * Get a hearthstone item.
         * @param event
         */
        @EventHandler
        public void onPlayerChat(AsyncPlayerChatEvent event) {
            if (event.getMessage().equals("123")) {
                event.getPlayer().getInventory().addItem(_hearthstoneItem);
            }
        }
    }

    private ItemStack _getHearthStone() {
        ItemStack _item = new ItemStack(Material.ECHO_SHARD);
        ItemMeta _itemMeta = _item.getItemMeta();
        _itemMeta.setDisplayName(ChatColor.DARK_PURPLE + "Hearthstone");
        _itemMeta.setLore(Arrays.asList(ChatColor.GOLD + "Inscribed with magical runes."));
        _item.setItemMeta(_itemMeta);
        return _item;
    }
}
