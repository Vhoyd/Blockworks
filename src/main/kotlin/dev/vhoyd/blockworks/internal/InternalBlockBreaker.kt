package dev.vhoyd.blockworks.internal

import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.api.model.Attribute
import dev.vhoyd.blockworks.api.model.BlockBreaker
import dev.vhoyd.blockworks.api.model.Wrapper
import org.bukkit.entity.Player

/**
 * Example implementation of [BlockBreaker] for use with [Player]s. Not required when using the API; other implementations
 * of `Player`-based `BlockBreaker`s are fully allowed.
 */
internal class InternalBlockBreaker<T>(
    override val blockworks: Blockworks,
    delegate: T,
    defaultAttributes: Map<Attribute<*, *>, Any>,
    defaultParts: Map<Class<out Attributable>, Attributable>,
    private val attributed: Attributable = InternalAttributed(InternalBlockBreaker::class.java, defaultAttributes.toMutableMap()),
    private val wrapper: Wrapper<T> = InternalWrapper(delegate)
) : Attributable by attributed, Wrapper<T> by wrapper, BlockBreaker<T> {



    override val parts: MutableMap<Class<out Attributable>, Attributable> = defaultParts.toMutableMap()
    override var currentBlock: BlockInstance? = null

    override fun <P : Any, C : Any> setAttribute(
        attribute: Attribute<P, C>,
        value: C
    ) {
        attributes[attribute] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>): C? {
        return attributes[attribute] as? C
    }
}