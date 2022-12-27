package io.github.thesowut.hearthstone.helpers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class HearthstoneHelper {
    public final ItemStack hearthstoneItem = this.getHearthstone();
    private final FileConfiguration _config;
    private final String _pluginTitle;

    public HearthstoneHelper(FileConfiguration config, String pluginTitle) {
        this._config = config;
        this._pluginTitle = pluginTitle;
    }

    /**
     * Initialize the Hearthstone item.
     *
     * @return Hearthstone Item
     */
    public ItemStack getHearthstone() {
        ItemStack _item = new ItemStack(Material.ECHO_SHARD);
        ItemMeta _meta = _item.getItemMeta();
        _meta.setDisplayName(ChatColor.DARK_PURPLE + "Hearthstone");
        _meta.setLore(Arrays.asList(ChatColor.GOLD + "Inscribed with magical runes."));
        _item.setItemMeta(_meta);
        return _item;
    }

    /**
     * If the player has set a hearthstone home, invoke teleportation
     *
     * @param event - Player Interaction
     */
    public void teleportPlayer(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location playerHomeLocation = this._config.getLocation(player.getName().toLowerCase());
        boolean isRightClickPressed = event.getAction().toString().contains("RIGHT_CLICK");
        boolean isAbleToHearthstone = this.canUseHearthstone(player);

        if (!this.canUseHearthstone(player)) {
            player.sendMessage(_pluginTitle + ChatColor.RED + "Must be grounded to perform that!");
            return;
        }

        if (playerHomeLocation == null && isRightClickPressed) {
            player.sendMessage(_pluginTitle + ChatColor.RED + "The Hearthstone doesn't lead anywhere!");
            return;
        }

        if (event.getItem().equals(this.hearthstoneItem) && isAbleToHearthstone && isRightClickPressed) {
            // TODO
            // make item undroppable
            player.teleport(playerHomeLocation);
            player.sendMessage(ChatColor.GOLD + (ChatColor.ITALIC + "Whoosh."));
        }
    }

    /**
     * Indicate whether the player can invoke hearthstone commands.
     *
     * @param player - Player using Hearthstone
     * @return - Whether the player is grounded
     */
    public boolean canUseHearthstone(Player player) {
        return !player.isSwimming()
                && !(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR);
    }
}
