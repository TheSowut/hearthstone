package io.github.thesowut.hearthstone.handler;

import io.github.thesowut.hearthstone.commands.HearthstoneCommands;
import io.github.thesowut.hearthstone.helpers.HearthstoneHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
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
    private final FileConfiguration _config = this.getConfig();
    private final ItemStack _hearthstone = this._getHearthStone();
    private final HearthstoneHelper _hearthstoneHelper = new HearthstoneHelper();
    private final String _pluginTitle = ChatColor.DARK_GRAY
            + "[" + ChatColor.DARK_GREEN
            + "HearthStone"
            + ChatColor.DARK_GRAY + "] "
            + ChatColor.WHITE;

    private final HearthstoneCommands _hearthstoneCommands =
            new HearthstoneCommands(_config, _hearthstone, _pluginTitle, _hearthstoneHelper);

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage(_pluginTitle + ChatColor.GREEN + "Plugin enabled.");
        getServer().getPluginManager().registerEvents(new HearthstoneListener(), this);

        // TODO
        // find a better way to do this
        getCommand("get").setExecutor(_hearthstoneCommands);
        getCommand("sethome").setExecutor(_hearthstoneCommands);

        // TODO
        // instead of using config for player homes, create separate file inside userdata dir
        // use config to for the following:
        // number of hearthstone usages
        // hearthstone material
        // cooldown between usages length

        _config.options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage(_pluginTitle + ChatColor.RED + "Plugin disabled.");
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

            // TODO
            // Add cooldown to hearthstone usage
            // Add particles when using hearthstone
            // Add channeling
            // Movement or pressing again should cancel channeling

            Player player = event.getPlayer();
            Location playerHomeLocation = _config.getLocation(player.getName().toLowerCase());
            boolean isRightClickPressed = event.getAction().toString().contains("RIGHT_CLICK");
            boolean isPlayerGrounded = !player.isSwimming() && !(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR);

            if (!_hearthstoneHelper.canUseHearthstone(player)) {
                player.sendMessage(_pluginTitle + ChatColor.RED + "Must be grounded to perform that!");
                return;
            }

            if (playerHomeLocation == null && isRightClickPressed) {
                player.sendMessage(_pluginTitle + ChatColor.RED + "The Hearthstone doesn't lead anywhere!");
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
}