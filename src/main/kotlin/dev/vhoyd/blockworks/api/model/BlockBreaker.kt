package dev.vhoyd.blockworks.api.model

import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.internal.InternalBlockBreaker


/**
 * Defines behavior for anything targeting a [BlockInstance]. Can hold various [Attribute]s and various [Attributable]s
 * for parsing and logic.
 * @see dev.vhoyd.blockworks.impl.BlockworksPlayer
 */

inline fun <reified V : Attributable> BlockBreaker<*>.getPart() : V? = getPart(V::class.java)

inline fun <reified V : Attributable> BlockBreaker<*>.setPart(part : V) : Unit = this.setPart(V::class.java, part)

inline fun <reified V: Attributable> BlockBreaker<*>.removePart() : V? = removePart(V::class.java)


@Suppress("unused")
interface BlockBreaker<out T> : Attributable, Wrapper<T>{
    val blockworks: Blockworks
    var currentBlock: BlockInstance?

    /**
     * Modifying this map directly should be avoided; use [getPart] and [setPart].
     */
    val parts : MutableMap<Class<out Attributable>, Attributable>


    /**
     * Calling this method outside of subclass initialization should be done with extreme caution.
     * If overriding, make sure this object gets passed along to `blockworks.registerBlockBreaker()`
     * if it should be acknowledged by Blockworks.
     */
    fun register() = apply { blockworks.registerBlockBreaker(this) }


    fun <V : Attributable> getPart(type: Class<V>) : V? {
        val part = parts[type]
        return if (part != null && type.isInstance(part)) type.cast(part) else null
    }

    fun <V : Attributable> setPart(type: Class<V>, part: V) : Unit = parts.set(type, part)

    fun <V: Attributable> removePart(type: Class<V>) : V? {
        val removed = parts.remove(type)
        return if (removed != null && type.isInstance(removed)) type.cast(removed) else null
    }

    companion object {

        /**
         * Creates a default implementation object.
         */
        operator fun <T> invoke(
            blockworks: Blockworks,
            delegate : T,
            defaultParts: Map<Class<out Attributable>, Attributable> = emptyMap(),
            defaultAttributes: Map<Attribute<*, *>, Any> = emptyMap(),

        ) : BlockBreaker<T> = InternalBlockBreaker(
            blockworks = blockworks,
            delegate = delegate,
            defaultParts = defaultParts,
            defaultAttributes = defaultAttributes
        )


        /**
         * Creates a default implementation object.
         */
        @JvmStatic
        @JvmOverloads
        fun <T> create(
            blockworks: Blockworks,
            delegate : T,
            defaultParts: Map<Class<out Attributable>, Attributable> = emptyMap(),
            defaultAttributes: Map<Attribute<*, *>, Any> = emptyMap(),

            ) : BlockBreaker<T> = InternalBlockBreaker(
            blockworks = blockworks,
            delegate = delegate,
            defaultParts = defaultParts,
            defaultAttributes = defaultAttributes
        )
    }


}