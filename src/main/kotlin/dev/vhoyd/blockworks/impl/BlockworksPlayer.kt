package dev.vhoyd.blockworks.impl

import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.model.Attribute
import dev.vhoyd.blockworks.model.AttributedImplement
import dev.vhoyd.blockworks.model.BlockBreaker
import dev.vhoyd.blockworks.nbt.PersistenceWriter
import org.bukkit.entity.Player

/**
 * Example implementation of [BlockBreaker] for use with [Player]s. Not required when using the API; other implementations
 * of `Player`-based `BlockBreaker`s are fully allowed.
 */
class BlockworksPlayer(
    delegate: Player,
    blockworks: Blockworks,
    data : Map<Attribute<*, *>, Any>,
    elements: MutableMap<Class<AttributedImplement<*>>, Any>
    ) : BlockBreaker<Player>(delegate, blockworks, data, elements) {

    override fun <P : Any, C : Any> setAttribute(attribute: Attribute<P, C>, value : C) {
        PersistenceWriter.setTag(blockworks.plugin, delegate, "blockworks-${attribute.name}", attribute.persistentDataType, value)
    }

    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>) : C {
        return PersistenceWriter.getTag(blockworks.plugin, delegate, "blockworks-${attribute.name}", attribute.persistentDataType)
    }

}