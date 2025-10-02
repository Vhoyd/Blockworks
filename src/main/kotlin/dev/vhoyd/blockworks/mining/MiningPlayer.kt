package dev.vhoyd.blockworks.mining

import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.nbt.PersistentDataUtil
import dev.vhoyd.blockworks.util.EmptyValue.Companion.BLOCKINSTANCE
import org.bukkit.entity.Player


/**
 * Class for tracking extra API data about any Player object, such as the current block, attributes
 * like mining speed, etc.
 */
class MiningPlayer : BlockworksAttributable {
    var currentBlock: BlockInstance = BLOCKINSTANCE
    var heldItem: MiningTool?
    val minecraftPlayer : Player
    val blockworks : Blockworks


    constructor(player: Player, blockworks: Blockworks) : this(player, blockworks, emptyMap())

    constructor(player: Player, blockworks: Blockworks, data : Map<MiningAttribute<*,*>, Any>) {
        this.minecraftPlayer = player
        heldItem = null
        this.blockworks = blockworks
        blockworks.registerPlayer(this)

        @Suppress("UNCHECKED_CAST")
        data.forEach { (key, value) -> set<Any, Any>(key as MiningAttribute<Any, Any>, value) }
    }

    override fun <P : Any, C : Any> setAttribute(miningAttribute: MiningAttribute<P, C>, value : C) {
        PersistentDataUtil.setTag(blockworks.plugin, minecraftPlayer, miningAttribute.name, miningAttribute.persistentDataType, value)
    }

    override fun <P : Any, C : Any> getAttribute(miningAttribute: MiningAttribute<P, C>) : C {
        return PersistentDataUtil.getTag(blockworks.plugin, minecraftPlayer, miningAttribute.name, miningAttribute.persistentDataType)
    }

}