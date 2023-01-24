package io.github.thesowut.hearthstone.helpers;

import io.github.thesowut.hearthstone.handler.Hearthstone;
import io.github.thesowut.hearthstone.listeners.MovementListener;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
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
    public final Map<UUID, Integer> particleTasks = new HashMap<>();
    public final Map<UUID, Integer> barTasks = new HashMap<>();
    private final FileHelper _fileHelper;
    private final PluginHelper _pluginHelper;
    private final MovementListener _movementListener = new MovementListener(this);
    private final Hearthstone _main;
    private BossBar castingBar;

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
            createPlayerTeleportationTask(playerHomeLocation, player, castTime, cooldown, playerWorld);
            createTeleportationParticlesTask(player, playerLocation, playerWorld);
            createCastingBarTask(player, castTime);
        }
    }

    /**
     * FIXME - https://github.com/TheSowut/hearthstone/issues/2
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
        _pluginHelper.sendTeleportationMessage(player, TeleportationState.CANCELED);

        this.removePlayerFromTasks(player);
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
     * @param world         - World of Player
     * @param location      - Location of Player
     * @param particles     - List of Particles to spawn
     * @param particleCount - Number of Particles to be Spawned
     * @param isRotating    - Indicates whether Particles should surround the Player
     */
    public void spawnParticle(World world, Location location, Particle[] particles, int particleCount, boolean isRotating) {
        if (isRotating) {
            for (int i = 0; i < 360; i += 20) {
                double angle = i * Math.PI / 180;
                double x = Math.cos(angle);
                double z = Math.sin(angle);
                for (Particle particle : particles) {
                    world.spawnParticle(particle, location.getX() + x, location.getY() + 1, location.getZ() + z,
                            0, 0, 0, 0, particleCount
                    );
                }
            }
            return;
        }

        for (Particle p : particles) {
            world.spawnParticle(p, location, particleCount);
        }
    }

    /**
     * Create a list of tasks that contain the player UUID and the task number.
     * Used during hearthstone process.
     *
     * @param player         - Player casting Hearthstone
     * @param playerLocation - Current player location
     * @param playerWorld    - World in which the player is located
     */
    private void createTeleportationParticlesTask(Player player, Location playerLocation, World playerWorld) {
        int taskNumber = Bukkit.getScheduler().scheduleSyncRepeatingTask(_main, () -> {
            // Spawn particles around player during the cast and play a casting sound.
            this.spawnParticle(
                    playerWorld,
                    playerLocation,
                    new Particle[]{Particle.VILLAGER_HAPPY},
                    1,
                    true
            );
            playerWorld.playSound(playerLocation, Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
        }, 0, 20 * 5);

        particleTasks.put(player.getUniqueId(), taskNumber);
    }

    /**
     * Create a list of tasks containing Player UUID & task number.
     *
     * @param playerHomeLocation - Location of player home
     * @param player             - Player casting Hearthstone
     * @param castTime           - Cast time of Hearthstone
     * @param cooldown           - Cooldown of Hearthstone
     * @param playerWorld        - World in which the player is located
     */
    private void createPlayerTeleportationTask(Location playerHomeLocation, Player player, long castTime, long cooldown, World playerWorld) {
        playersBeingTeleported.put(player.getUniqueId(), TeleportationState.STARTED);

        int taskNumber = Bukkit.getScheduler().scheduleSyncDelayedTask(_main, () -> {
            this.removePlayerFromTasks(player);
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
    }

    /**
     * Create a list of tasks that contain the player UUID and the task number.
     *
     * @param player    - Player casting Hearthstone
     * @param castTime  - Cast time of Hearthstone
     */
    private void createCastingBarTask(Player player, long castTime) {
        castingBar = Bukkit.createBossBar("Teleporting", BarColor.GREEN, BarStyle.SEGMENTED_10);
        castingBar.setProgress(0);
        castingBar.addPlayer(player);

        int taskNumber = Bukkit.getScheduler().scheduleSyncRepeatingTask(_main, () -> {
            if (castingBar.getProgress() < 1.0) castingBar.setProgress(castingBar.getProgress() + 0.099);
        }, 0, 20 * castTime / 10);

        barTasks.put(player.getUniqueId(), taskNumber);
    }

    /**
     * When teleport is canceled or finished, remove the player from the tasks.
     *
     * @param player - Teleported player or player who cancelled teleportation
     */
    private void removePlayerFromTasks(Player player) {
        final int tpTaskId = this.teleportationTasks.get(player.getUniqueId());
        final int particleTaskId = this.particleTasks.get(player.getUniqueId());
        final int barTaskId = this.barTasks.get(player.getUniqueId());

        _main.getServer().getScheduler().cancelTask(tpTaskId);
        _main.getServer().getScheduler().cancelTask(particleTaskId);
        _main.getServer().getScheduler().cancelTask(barTaskId);

        playersBeingTeleported.remove(player.getUniqueId());
        teleportationTasks.remove(player.getUniqueId());
        particleTasks.remove(player.getUniqueId());
        barTasks.remove(player.getUniqueId());
        castingBar.removePlayer(player);
    }
}
