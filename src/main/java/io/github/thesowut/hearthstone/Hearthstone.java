package io.github.thesowut.hearthstone;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
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
    private ItemStack _hearthstone = this._getHearthStone();

    private final String pluginTitle = ChatColor.DARK_GRAY
            + "[" + ChatColor.DARK_GREEN
            + "HearthStone"
            + ChatColor.DARK_GRAY + "] "
            + ChatColor.WHITE;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage(pluginTitle + ChatColor.GREEN + "Plugin enabled.");
        getServer().getPluginManager().registerEvents(new HearthstoneListener(), this);

        // TODO
        // find a better way to do this
        getCommand("get").setExecutor(new HearthStoneCommands());
        getCommand("sethome").setExecutor(new HearthStoneCommands());

        _config.options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage(pluginTitle + ChatColor.RED + "Plugin disabled.");
    }

    private ItemStack _getHearthStone() {
        ItemStack _item = new ItemStack(Material.ECHO_SHARD);
        ItemMeta _meta = _item.getItemMeta();
        _meta.setDisplayName(ChatColor.DARK_PURPLE + "Hearthstone");
        _meta.setLore(Arrays.asList(ChatColor.GOLD + "Inscribed with magical runes."));
        _item.setItemMeta(_meta);
        return _item;
    }

    public class HearthstoneListener implements Listener {
        @EventHandler
        public void onPlayer(PlayerInteractEvent event) {
            System.out.println("EVENT action");
            System.out.println(event.getAction());

            Player player = event.getPlayer();
            Location playerHomeLocation = _config.getLocation(player.getName().toLowerCase());
            boolean isRightClickPressed = event.getAction().toString().contains("RIGHT_CLICK");
            boolean isPlayerGrounded = !player.isSwimming() && !(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR);

            if (!_canUseHearthstone(player)) {
                player.sendMessage(pluginTitle + ChatColor.RED + "Must be grounded to perform that!");
                return;
            }

            if (playerHomeLocation == null && isRightClickPressed) {
                player.sendMessage(pluginTitle + ChatColor.RED + "The Hearthstone doesn't lead anywhere!");
                return;
            }

            if (event.getItem().equals(_hearthstone) && isPlayerGrounded && isRightClickPressed) {
                // TODO
                // make item undroppable

                player.teleport(playerHomeLocation);
                player.sendMessage(ChatColor.GOLD + (ChatColor.ITALIC + "Whoosh."));
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

            switch (command) {
                case "get":
                    if (player.getInventory().contains(_hearthstone)) {
                        player.sendMessage(pluginTitle + ChatColor.RED + "You can only carry a single Hearthstone!");
                        break;
                    }

                    player.getInventory().addItem(_hearthstone);
                    player.sendMessage(pluginTitle + ChatColor.GREEN + "A Hearthstone appears in your pocket!");
                    break;

                case "sethome":
                    if (player.isFlying()) {
                        player.sendMessage(pluginTitle + ChatColor.RED + "Must be grounded to perform that!");
                        break;
                    }

                    _config.set(player.getName().toLowerCase(), player.getLocation());
                    player.sendMessage(pluginTitle + ChatColor.GREEN + "You new home has been set.");
                    saveConfig();
                    break;
            }

            return true;
        }
    }

    private boolean _canUseHearthstone(Player player) {
        return !player.isSwimming() && !(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR);
    }
}