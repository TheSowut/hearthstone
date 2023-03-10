package io.github.thesowut.hearthstone.listeners;

import io.github.thesowut.hearthstone.helpers.HearthstoneHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementListener implements Listener {
    private final HearthstoneHelper _hearthstoneHelper;

    public MovementListener(HearthstoneHelper hearthstoneHelper) {
        this._hearthstoneHelper = hearthstoneHelper;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!_hearthstoneHelper.isUsingHearthstone(event.getPlayer())) return;
        // Disable player movement during teleportation.
        event.setCancelled(true);
    }
}
