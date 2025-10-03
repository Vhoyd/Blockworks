package dev.vhoyd.blockworks.mining

import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.nbt.PersistentDataUtil
import org.bukkit.entity.Player


/**
 * Class for tracking extra API data about any Player object, such as the current block, attributes
 * like mining speed, etc.
 */
class MiningPlayer(
    val minecraftPlayer : Player,
    val blockworks: Blockworks,
    data : Map<MiningAttribute<*,*>, Any> = emptyMap()
) : BlockworksAttributable {
    var currentBlock: BlockInstance? = null
    var heldTool = MiningTool(blockworks, null)


    init {
        @Suppress("UNCHECKED_CAST")
        data.forEach { (key, value) -> set<Any, Any>(key as MiningAttribute<Any, Any>, value) }
    }

    override fun <P : Any, C : Any> setAttribute(miningAttribute: MiningAttribute<P, C>, value : C) {
        PersistentDataUtil.setTag(blockworks.plugin, minecraftPlayer, "blockworks-${miningAttribute.name}", miningAttribute.persistentDataType, value)
    }

    override fun <P : Any, C : Any> getAttribute(miningAttribute: MiningAttribute<P, C>) : C {
        return PersistentDataUtil.getTag(blockworks.plugin, minecraftPlayer, "blockworks-${miningAttribute.name}", miningAttribute.persistentDataType)
    }

}