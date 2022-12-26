package io.github.thesowut.hearthstone;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class Hearthstone extends JavaPlugin {
    private FileConfiguration _config = this.getConfig();
    private ItemStack _hearthstone;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage("Enabling plugin");
        getServer().getPluginManager().registerEvents(new HearthstoneListener(), this);

        // TODO
        // find a better way to do this
        getCommand("get").setExecutor(new HearthStoneCommands());
        getCommand("sethome").setExecutor(new HearthStoneCommands());

        _hearthstone = this._getHearthStone();
        _config.options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage("Disabling plugin");
    }

    private ItemStack _getHearthStone() {
        ItemStack _item = new ItemStack(Material.ECHO_SHARD);
        ItemMeta _itemMeta = _item.getItemMeta();
        _itemMeta.setDisplayName(ChatColor.DARK_PURPLE + "Hearthstone");
        _itemMeta.setLore(Arrays.asList(ChatColor.GOLD + "Inscribed with magical runes."));
        _item.setItemMeta(_itemMeta);
        return _item;
    }

    public class HearthstoneListener implements Listener {
        @EventHandler
        public void onPlayer(PlayerInteractEvent event) {
            System.out.println("EVENT action");
            System.out.println(event.getAction());

            if (event.getItem().equals(_hearthstone)) {
                // TODO
                // make item undroppable

                // TODO
                // activate only on right click

                // TODO
                // activate only when player is grounded

                Player player = event.getPlayer();
                Location x = _config.getLocation(event.getPlayer().getName().toLowerCase());
                player.teleport(x);
                player.sendMessage(ChatColor.GOLD + "Whoosh.");
            }
        }
    }

    public class HearthStoneCommands implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (!(sender instanceof Player)) {
                return true;
            }

            Player player = ((Player) sender).getPlayer();
            String command = cmd.getName().toLowerCase();
            System.out.println("THE COMMAND");
            System.out.println(command);

            switch (command) {
                case "get":
                    if (player.getInventory().contains(_hearthstone)) {
                        // TODO
                        // Add message
                        break;
                    }

                    player.getInventory().addItem(_hearthstone);
                    // TODO
                    // Add message
                    break;

                case "sethome":
                    // TODO
                    // check if user isn't grounded -> early return
                    _config.set(player.getName().toLowerCase(), player.getLocation());
                    // TODO
                    // Add message
                    saveConfig();
                    break;
            }

            return true;
        }
    }
}