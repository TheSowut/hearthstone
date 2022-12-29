package io.github.thesowut.hearthstone.helpers;

import io.github.thesowut.hearthstone.handler.Hearthstone;
import io.github.thesowut.hearthstone.listeners.MovementListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class HearthstoneHelper {
    public enum TeleportationState {
        STARTED,
        SUCCESS,
        FAIL
    }

    public final ItemStack hearthstoneItem = this.getHearthstone();
    public final Map<UUID, TeleportationState> playersBeingTeleported = new HashMap<>();
    private final MovementListener _movementListener = new MovementListener(this);
    private final FileConfiguration _config;
    private final PluginHelper _pluginHelper;
    private final Hearthstone _main;

    public HearthstoneHelper(FileConfiguration config, PluginHelper pluginHelper, Hearthstone main) {
        this._config = config;
        this._pluginHelper = pluginHelper;
        this._main = main;
    }

    /**
     * Initialize the Hearthstone item.
     *
     * @return Hearthstone Item
     */
    public ItemStack getHearthstone() {
        ItemStack _item = new ItemStack(Material.ECHO_SHARD);
        ItemMeta _meta = _item.getItemMeta();
        _meta.setDisplayName(ChatColor.DARK_GREEN + (ChatColor.BOLD + "Hearthstone"));
        _meta.setLore(Collections.singletonList(ChatColor.RED + "Inscribed with magical runes."));
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
            _pluginHelper.sendNotGroundedMessage(player);
            return;
        }

        if (playerHomeLocation == null && isRightClickPressed) {
            _pluginHelper.sendNullHomeMessage(player);
            return;
        }

        if (event.getItem() != null && event.getItem().equals(this.hearthstoneItem) && isAbleToHearthstone && isRightClickPressed) {
            // Start listening for movement.
            _main.getServer().getPluginManager().registerEvents(_movementListener, _main);

            playersBeingTeleported.put(player.getUniqueId(), TeleportationState.STARTED);
            _pluginHelper.sendTeleportationMessage(player, TeleportationState.STARTED);
            // TODO get teleportation delay from config after moving players userdata
            int delay = 5;
            Bukkit.getScheduler().scheduleSyncDelayedTask(_main, () -> {
                TeleportationState teleportState = playersBeingTeleported.get(player.getUniqueId());
                HandlerList.unregisterAll(_movementListener);
                if (teleportState == TeleportationState.FAIL) {
                    _pluginHelper.sendTeleportationMessage(player, teleportState);
                    return;
                }

                teleportState = TeleportationState.SUCCESS;
                player.teleport(playerHomeLocation);
                _pluginHelper.sendTeleportationMessage(player, teleportState);
            }, 20 * delay);
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
