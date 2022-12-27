package io.github.thesowut.hearthstone.helpers;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class HearthstoneHelper {
    /**
     * Indicate whether the player can invoke hearthstone commands.
     * @param player
     * @return
     */
    public boolean canUseHearthstone(Player player) {
        return !player.isSwimming()
                && !(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR);
    }
}
