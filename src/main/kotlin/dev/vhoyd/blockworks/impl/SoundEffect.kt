package dev.vhoyd.blockworks.impl

import dev.vhoyd.blockworks.api.block.BlockInstance
import org.bukkit.Sound
import java.util.function.Consumer

/**
 * Plays a given `Sound` at a given `Location`. Example implementation of what could be provided
 * to [dev.vhoyd.blockworks.api.block.BlockDefinition.onBreak].
 */
class SoundEffect(val sound: Sound, val volume: Float, val pitch: Float) : Consumer<BlockInstance> {

    override fun accept(target: BlockInstance) {
        target.location.world.playSound(target.location, sound, volume, pitch)
    }

}