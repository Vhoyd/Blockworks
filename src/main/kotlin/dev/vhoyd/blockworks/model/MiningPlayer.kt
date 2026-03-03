package dev.vhoyd.blockworks.model

import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.nbt.PersistentDataUtil
import org.bukkit.entity.Player


/**
 * Class for tracking extra API data about any Player object, such as the current block, attributes
 * like mining speed, etc.
 */
class MiningPlayer(
    delegate: Player,
    blockworks: Blockworks,
    data : Map<Attribute<*,*>, Any>,
    elements: MutableMap<Class<AttributedElement<*>>, Any>
    ) : BlockBreaker<Player>(delegate, blockworks, data, elements) {

    override fun <P : Any, C : Any> setAttribute(attribute: Attribute<P, C>, value : C) {
        PersistentDataUtil.setTag(blockworks.plugin, delegate, "blockworks-${attribute.name}", attribute.persistentDataType, value)
    }

    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>) : C {
        return PersistentDataUtil.getTag(blockworks.plugin, delegate, "blockworks-${attribute.name}", attribute.persistentDataType)
    }

}