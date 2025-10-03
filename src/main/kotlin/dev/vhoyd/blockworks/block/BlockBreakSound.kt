package dev.vhoyd.blockworks.block

import dev.vhoyd.blockworks.core.BlockBreakAction
import org.bukkit.Sound


/**
 * Implementation of [BlockBreakAction] for playing a sound at a location.
 */
class BlockBreakSound(val sound: Sound, val volume: Float, val pitch: Float)  : BlockBreakAction {

    override fun invoke(p1: BlockInstance) {
        p1.location.world.playSound(p1.location, sound, volume, pitch)
    }

}