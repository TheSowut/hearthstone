package io.github.thesowut.hearthstone.helpers;

import io.github.thesowut.hearthstone.handler.Hearthstone;
import io.github.thesowut.hearthstone.listeners.MovementListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HearthstoneHelper {
    public enum TeleportationState {
        STARTED,
        SUCCESS,
        CANCELED
    }

    public final ItemStack hearthstoneItem = this.getHearthstone();
    public final Map<UUID, TeleportationState> playersBeingTeleported = new HashMap<>();
    public final Map<UUID, Integer> teleportationTasks = new HashMap<>();
    private final FileHelper _fileHelper;
    private final PluginHelper _pluginHelper;
    private final MovementListener _movementListener = new MovementListener(this);
    private final Hearthstone _main;

    public HearthstoneHelper(PluginHelper pluginHelper, FileHelper fileHelper, Hearthstone main) {
        this._pluginHelper = pluginHelper;
        this._fileHelper = fileHelper;
        this._main = main;
    }

    /**
     * Initialize the Hearthstone item.
     *
     * @return Hearthstone Item.
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
     * If the player has set a hearthstone home, invoke teleportation.
     *
     * @param event - Player Interaction.
     */
    public void teleportPlayer(PlayerInteractEvent event, Location playerHomeLocation) {
        Player player = event.getPlayer();

        if (event.getItem() != null) {
            // Attach a new listener for player movement.
            // Avoid attaching if there is a listener present, use it instead.
            if (playersBeingTeleported.size() < 1) {
                _main.getServer().getPluginManager().registerEvents(_movementListener, _main);
            }
            playersBeingTeleported.put(player.getUniqueId(), TeleportationState.STARTED);
            // TODO get teleportation delay from config
            int teleportDelay = 5;
            // TODO get teleportation cooldown from cooldowns.yml
            final int FIVE_MINUTES_IN_MILLISECONDS = 300000;

            int taskNumber = Bukkit.getScheduler().scheduleSyncDelayedTask(_main, () -> {
                playersBeingTeleported.remove(player.getUniqueId());
                teleportationTasks.remove(player.getUniqueId());

                player.teleport(playerHomeLocation);
                _pluginHelper.sendTeleportationMessage(player, TeleportationState.SUCCESS);
                _fileHelper.getCooldowns().set(String.valueOf(player.getUniqueId()),
                        System.currentTimeMillis() + FIVE_MINUTES_IN_MILLISECONDS);
                _fileHelper.saveCooldowns();

                // If there are no players using the hearthstone, remove the listener.
                if (playersBeingTeleported.size() < 1) {
                    HandlerList.unregisterAll(_movementListener);
                }
            }, 20 * teleportDelay);

            _pluginHelper.sendTeleportationMessage(player, TeleportationState.STARTED);
            teleportationTasks.put(player.getUniqueId(), taskNumber);
        }
    }

    /**
     * Indicate whether the player can invoke hearthstone commands.
     *
     * @param player - Player using Hearthstone.
     * @return - Whether the player is grounded.
     */
    public boolean isPlayerNotGrounded(Player player) {
        return player.isSwimming()
                || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR;
    }

    /**
     * Check whether a player is currently teleporting.
     *
     * @param player - The player checked for teleportation status.
     * @return - Whether the player is teleporting.
     */
    public boolean isUsingHearthstone(Player player) {
        return this.playersBeingTeleported.get(player.getUniqueId()) != null;
    }

    /**
     * Stop hearthstone process of a player.
     *
     * @param player - Player whose teleportation will be canceled.
     */
    public void cancelTeleportation(Player player) {
        if (!isUsingHearthstone(player)) return;

        final int taskId = this.teleportationTasks.get(player.getUniqueId());
        _main.getServer().getScheduler().cancelTask(taskId);
        _pluginHelper.sendTeleportationMessage(player, TeleportationState.CANCELED);

        playersBeingTeleported.remove(player.getUniqueId());
        teleportationTasks.remove(player.getUniqueId());
        if (playersBeingTeleported.size() < 1) {
            HandlerList.unregisterAll(_movementListener);
        }
    }

    /**
     * @param player - Player using Hearthstone.
     * @return - Whether the hearthstone has cooldown.
     */
    public boolean hasCooldown(Player player) {
        return this.getHearthstoneCooldown(player) > System.currentTimeMillis();
    }

    /**
     * @param player - Player using Hearthstone.
     * @return - Cooldown of hearthstone in milliseconds.
     */
    public long getHearthstoneCooldown(Player player) {
        long cooldown = 0;
        if (_fileHelper.getCooldowns().get(String.valueOf(player.getUniqueId())) != null)
            cooldown = (long) _fileHelper.getCooldowns().get(String.valueOf(player.getUniqueId()));
        return cooldown;
    }
}
