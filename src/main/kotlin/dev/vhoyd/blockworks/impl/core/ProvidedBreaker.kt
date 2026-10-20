package dev.vhoyd.blockworks.impl.core

import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.api.model.Attribute
import dev.vhoyd.blockworks.api.model.BlockBreaker
import dev.vhoyd.blockworks.api.model.Wrapper
import dev.vhoyd.blockworks.impl.attribute.ProvidedAttributed
import org.bukkit.entity.Player

/**
 * Example implementation of [BlockBreaker] for use with [Player]s. Not required when using the API; other implementations
 * of `Player`-based `BlockBreaker`s are fully allowed.
 */
internal class ProvidedBreaker<T>(
    override val blockworks: Blockworks,
    delegate: T,
    defaultAttributes: Map<Attribute<*, *>, Any>,
    defaultParts: Map<Class<out Attributable>, Attributable>,
    private val attributed: Attributable = ProvidedAttributed(defaultAttributes.toMutableMap()),
    private val wrapper: Wrapper<T> = ProvidedWrapper(delegate)
) : Attributable by attributed, Wrapper<T> by wrapper, BlockBreaker<T> {


    override val parts: MutableMap<Class<out Attributable>, Attributable> = defaultParts.toMutableMap()
    override var currentBlock: BlockInstance? = null

    init {
        blockworks.registerBlockBreaker(this)
    }


}