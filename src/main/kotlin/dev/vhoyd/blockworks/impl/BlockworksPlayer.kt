package dev.vhoyd.blockworks.impl

import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.api.model.Attribute
import dev.vhoyd.blockworks.api.model.Implement
import dev.vhoyd.blockworks.api.model.BlockBreaker
import org.bukkit.entity.Player

/**
 * Example implementation of [BlockBreaker] for use with [Player]s. Not required when using the API; other implementations
 * of `Player`-based `BlockBreaker`s are fully allowed.
 */
class BlockworksPlayer(
    delegate: Player,
    blockworks: Blockworks,
    data : Map<Attribute<*, *>, Any>,
    defaultImplements: Map<Class<out Implement<*>>, Implement<*>>
    ) : BlockBreaker<Player>(delegate, blockworks, data, defaultImplements) {

    override fun <P : Any, C : Any> setAttribute(attribute: Attribute<P, C>, value : C) {
        PersistenceWriter.setValue(blockworks.plugin, delegate, "blockworks-${attribute.name}", attribute.type, value)
    }

    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>): C? {
        return PersistenceWriter.getValue(blockworks.plugin, delegate, "blockworks-${attribute.name}", attribute.type)
    }


}