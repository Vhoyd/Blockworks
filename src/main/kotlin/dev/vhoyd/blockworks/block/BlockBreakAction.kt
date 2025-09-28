package dev.vhoyd.blockworks.block

import dev.vhoyd.blockworks.mining.MiningPlayer


/**
 * SAM interface for tacking on any code to run when a block is broken.
 */
fun interface BlockBreakAction {

    /**
     * INTERNAL: called automatically. Call externally at your own risk.
     */
    fun run(tile: BlockInstance, player: MiningPlayer)
}