package io.github.thesowut.hearthstone.listeners;

import io.github.thesowut.hearthstone.helpers.HearthstoneHelper;
import org.bukkit.entity.Player;
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
        Player player = event.getPlayer();

        if (_hearthstoneHelper.playersBeingTeleported.containsKey(player.getUniqueId())) {
            // If the player moves, mark it as failed.
            if (event.getTo() != null && (event.getTo().getX() != event.getFrom().getX()
                    || event.getTo().getY() != event.getFrom().getY())) {
                _hearthstoneHelper.cancelTeleportation(player);
            }
        }
    }
}
