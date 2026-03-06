package dev.vhoyd.blockworks.impl

import dev.vhoyd.blockworks.block.BlockInstance
import org.bukkit.Sound
import java.util.function.Consumer

/**
 * Plays a given `Sound` at a given `Location`. Example implementation of what could be provided
 * to [dev.vhoyd.blockworks.block.BlockDefinition.breakBehavior].
 */
class SoundEffect(val sound: Sound, val volume: Float, val pitch: Float)  : Consumer<BlockInstance> {

    override fun accept(p1: BlockInstance) {
        p1.location.world.playSound(p1.location, sound, volume, pitch)
    }

}