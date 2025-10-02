package dev.vhoyd.blockworks.block

import dev.vhoyd.blockworks.mining.MiningPlayer
import org.bukkit.Sound


/**
 * Implementation of the [BlockBreakAction] interface for playing a sound at a location.
 */
class BlockBreakSound : BlockBreakAction {
    val sound: Sound
    val volume : Float
    val pitch : Float

    constructor(sound: Sound, volume: Float, pitch: Float) {
        this.sound = sound
        this.volume = volume
        this.pitch = pitch
    }

    override fun run(tile: BlockInstance, player: MiningPlayer) {
        tile.location.world.playSound(tile.location, sound, volume, pitch)
    }

}