package io.github.thesowut.hearthstone.listeners;

import io.github.thesowut.hearthstone.helpers.FileHelper;
import io.github.thesowut.hearthstone.helpers.HearthstoneHelper;
import io.github.thesowut.hearthstone.helpers.PluginHelper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class InteractionListener implements Listener {
    private final FileHelper _fileHelper;
    private final HearthstoneHelper _hearthstoneHelper;
    private final PluginHelper _pluginHelper;

    public InteractionListener(FileHelper fileHelper, HearthstoneHelper hearthstoneHelper, PluginHelper pluginHelper) {
        this._fileHelper = fileHelper;
        this._hearthstoneHelper = hearthstoneHelper;
        this._pluginHelper = pluginHelper;
    }

    @EventHandler
    public void onPlayer(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location homeLocation = _fileHelper.getHomes().getLocation(String.valueOf(player.getUniqueId()));
        if (!this.isEventValid(event)) return;
        if (!this.isAbleToHearthstone(player, homeLocation)) return;

        // TODO
        // Add particles when using hearthstone
        // Add channeling
        // make item undroppable

        _hearthstoneHelper.teleportPlayer(event, homeLocation);
    }

    /**
     * Validate the event, before teleporting player.
     *
     * @param event - Player item interaction event.
     * @return Whether the event is valid.
     */
    private boolean isEventValid(PlayerInteractEvent event) {
        // In case the player isn't holding anything.
        if (event.getItem() == null) return false;
        // If the player isn't holding a hearthstone, do nothing.
        if (!event.getItem().equals(_hearthstoneHelper.hearthstoneItem)) return false;
        // If the player isn't right-clicking with a Hearthstone, do nothing.
        return event.getAction().toString().contains("RIGHT_CLICK");
    }

    /**
     * @param player - Player using hearthstone.
     * @return Whether the player can use the hearthstone.
     */
    private boolean isAbleToHearthstone(Player player, Location homeLocation) {
        // If the player isn't grounded, send an error message.
        if (_hearthstoneHelper.isPlayerNotGrounded(player)) {
            _pluginHelper.sendNotGroundedMessage(player);
            return false;
        }
        // If the player hasn't set a hearthstone home, send an error message.
        if (homeLocation == null) {
            _pluginHelper.sendNullHomeMessage(player);
            return false;
        }
        // If the player is already teleporting, stop the channeling.
        if (_hearthstoneHelper.isUsingHearthstone(player)) {
            _hearthstoneHelper.cancelTeleportation(player);
            return false;
        }
        // If the player has used the hearthstone already, send an error message.
        if (_hearthstoneHelper.hasCooldown(player)) {
            long cooldown = _hearthstoneHelper.getHearthstoneCooldown(player);
            _pluginHelper.sendActiveCooldownMessage(player, cooldown);
            return false;
        }
        return true;
    }
}
