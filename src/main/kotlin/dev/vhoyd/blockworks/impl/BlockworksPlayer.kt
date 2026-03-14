package dev.vhoyd.blockworks.impl

import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.api.model.Attribute
import dev.vhoyd.blockworks.api.model.BlockBreaker
import dev.vhoyd.blockworks.api.model.PersistentAttributable
import dev.vhoyd.blockworks.api.model.Wrapper
import dev.vhoyd.blockworks.internal.InternalPersistentAttributed
import dev.vhoyd.blockworks.internal.InternalPlayerWrapper
import org.bukkit.entity.Player

/**
 * Example implementation of [BlockBreaker] for use with [Player]s. Not required when using the API; other implementations
 * of `Player`-based `BlockBreaker`s are fully allowed.
 */
class BlockworksPlayer private constructor(
    override val blockworks: Blockworks,
    defaultParts: Map<Class<out Attributable>, Attributable>,
    private val persistentAttributed: PersistentAttributable,
    private val playerWrapper: Wrapper<Player>
) : PersistentAttributable by persistentAttributed, Wrapper<Player> by playerWrapper, BlockBreaker<Player> {

    @JvmOverloads
    constructor(
        blockworks: Blockworks,
        delegate: Player,
        defaultAttributes: Map<Attribute<*, *>, Any>,
        defaultParts: Map<Class<out Attributable>, Attributable>,
        overwriteAttributes : Boolean = false

    ) : this(
        blockworks,
        defaultParts,
        InternalPersistentAttributed(blockworks, delegate, BlockworksPlayer::class.java, defaultAttributes.toMutableMap(), overwriteAttributes),
        InternalPlayerWrapper(delegate)
        )


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