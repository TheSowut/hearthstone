package io.github.thesowut.hearthstone.helpers;

import io.github.thesowut.hearthstone.handler.Hearthstone;
import io.github.thesowut.hearthstone.listeners.MovementListener;
import org.bukkit.*;
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
import java.util.concurrent.TimeUnit;

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
        long castTime = this.getCastTime();
        long cooldown = this.getCooldown();
        Location playerLocation = player.getLocation();
        World playerWorld = player.getWorld();

        if (event.getItem() != null) {
            // Attach a new listener for player movement.
            // Avoid attaching if there is a listener present, use it instead.
            if (playersBeingTeleported.size() < 1) {
                _main.getServer().getPluginManager().registerEvents(_movementListener, _main);
            }
            playersBeingTeleported.put(player.getUniqueId(), TeleportationState.STARTED);

            int taskNumber = Bukkit.getScheduler().scheduleSyncDelayedTask(_main, () -> {
                playersBeingTeleported.remove(player.getUniqueId());
                teleportationTasks.remove(player.getUniqueId());

                player.teleport(playerHomeLocation);
                // Spawn particles when the player has successfully teleported and play a sound.
                this.spawnParticle(
                        playerWorld,
                        playerHomeLocation,
                        new Particle[]{Particle.SPELL_WITCH},
                        1,
                        true
                );

                playerWorld.playSound(playerHomeLocation, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                _pluginHelper.sendTeleportationMessage(player, TeleportationState.SUCCESS);

                // Save the cooldown delay for the user.
                _fileHelper.getCooldowns().set(String.valueOf(player.getUniqueId()),
                        System.currentTimeMillis() + cooldown);
                _fileHelper.saveCooldowns();

                // If there are no players using the hearthstone, remove the listener.
                if (playersBeingTeleported.size() < 1) {
                    HandlerList.unregisterAll(_movementListener);
                }
            }, 20 * castTime);

            _pluginHelper.sendTeleportationMessage(player, TeleportationState.STARTED);
            teleportationTasks.put(player.getUniqueId(), taskNumber);

            // Spawn particles around player during the cast and play a casting sound.
            this.spawnParticle(
                    playerWorld,
                    playerLocation,
                    new Particle[]{Particle.VILLAGER_HAPPY},
                    1,
                    true
            );
            playerWorld.playSound(playerLocation, Sound.BLOCK_BREWING_STAND_BREW, 1.0f, 1.0f);
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

    /**
     * Get "cast_time" value from config.yml.
     *
     * @return - cast_time in seconds
     */
    public int getCastTime() {
        return (int) _main.getConfig().get("cast_time");
    }

    /**
     * Get "cooldown" value from config.yml in milliseconds.
     *
     * @return - cooldown in milliseconds
     */
    public long getCooldown() {
        int cooldown = (int) _main.getConfig().get("cooldown");
        return TimeUnit.SECONDS.toMillis(cooldown);
    }

    /**
     *
     * @param world - World of Player
     * @param location - Location of Player
     * @param particle - List of Particles to be spawn
     * @param particleCount - Number of Particles to be Spawned
     * @param isRotating - Indicates whether Particles should surround the Player
     */
    public void spawnParticle(World world, Location location, Particle[] particle, int particleCount, boolean isRotating) {
        if (isRotating) {
            for (int i = 0; i < 360; i += 20) {
                double angle = i * Math.PI / 180;
                double x = Math.cos(angle);
                double z = Math.sin(angle);
                for (Particle p : particle) {
                    world.spawnParticle(p, location.getX() + x, location.getY() + 1, location.getZ() + z,
                            0, 0, 0, 0, particleCount
                    );
                }
            }
            return;
        }

        for (Particle p : particle) {
            world.spawnParticle(p, location, particleCount);
        }
    }
}
