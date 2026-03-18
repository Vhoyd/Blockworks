package dev.vhoyd.blockworks.impl

import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.api.model.Attribute
import dev.vhoyd.blockworks.api.model.BlockBreaker
import dev.vhoyd.blockworks.api.model.PersistentAttributable
import dev.vhoyd.blockworks.internal.InternalBlockBreaker
import dev.vhoyd.blockworks.internal.InternalPersistentAttributed
import org.bukkit.entity.Player

/**
 * Example implementation of [BlockBreaker] for use with [Player]s.
 *
 * Not required when using the API; other implementations of `Player`-based `BlockBreaker`s are fully allowed.
 * Note: `BlockBreaker`s paired with [PersistentAttributable] will require a method override, and should prefer
 * persistent behavior.
 */
class BlockworksPlayer private constructor(
    override val blockworks: Blockworks,
    defaultParts: Map<Class<out Attributable>, Attributable>,
    defaultAttributes: Map<Attribute<*, *>, Any>,
    private val persistentAttributed: PersistentAttributable,
    private val breaker: BlockBreaker<Player>,
) : PersistentAttributable by persistentAttributed, BlockBreaker<Player> by breaker {

    @JvmOverloads
    @Suppress("unused") // for external use only
    constructor(
        blockworks: Blockworks,
        delegate: Player,
        defaultAttributes: Map<Attribute<*, *>, Any>,
        defaultParts: Map<Class<out Attributable>, Attributable>,
        overwriteAttributes: Boolean = false

    ) : this(
        blockworks = blockworks,
        defaultParts = defaultParts,
        defaultAttributes = defaultAttributes,
        persistentAttributed = InternalPersistentAttributed(
            blockworks.plugin,
            delegate,
            defaultAttributes.toMutableMap(),
            overwriteAttributes
        ),
        breaker = InternalBlockBreaker(blockworks, delegate, defaultAttributes, defaultParts)
    )

    override val attributes: MutableMap<Attribute<*, *>, Any> = defaultAttributes.toMutableMap()
    override val parts: MutableMap<Class<out Attributable>, Attributable> = defaultParts.toMutableMap()
    override var currentBlock: BlockInstance? = null

    override fun <P : Any, C : Any> setAttribute(
        attribute: Attribute<P, C>,
        value: C
    ) {
        persistentAttributed[attribute] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>): C? {
        return persistentAttributed[attribute]
    }

    override operator fun <P : Any, C : Any> get(attribute: Attribute<P, C>): C? {
        return persistentAttributed[attribute]
    }

    override operator fun <P : Any, C : Any> set(attribute: Attribute<P, C>, value: C) {
        persistentAttributed[attribute] = value
    }
}