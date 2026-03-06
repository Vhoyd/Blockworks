package dev.vhoyd.blockworks.model

import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.core.Blockworks


/**
 * Defines behavior for anything targeting a [BlockInstance]. Can hold various [Attribute]s and various [AttributedImplement]s
 * for parsing and logic.
 * @see dev.vhoyd.blockworks.impl.BlockworksPlayer
 */
@Suppress("Unchecked_cast", "unused")
abstract class BlockBreaker<T>(
    delegate : T,
    val blockworks: Blockworks,
    defaultAttributes : Map<Attribute<*,*>, Any>,
    val implements : MutableMap<Class<AttributedImplement<*>>, Any>,
) : Attributable, Wrapper<T>(delegate) {
    var currentBlock: BlockInstance? = null

    init {
        defaultAttributes.forEach { (key, value) -> set(key as Attribute<Any, Any>, value) }
        blockworks.registerBlockBreaker(this)
    }
    inline fun <reified V : AttributedImplement<*>> getImplement() = implements[V::class.java as Class<*>] as V

    inline fun <reified V: AttributedImplement<*>> getImplementOrNull() = implements[V::class.java as Class<*>] as? V

    inline fun <reified V : AttributedImplement<*>> setImplement(element : V) = implements.set(V::class.java as Class<AttributedImplement<*>>, element)

    inline fun <reified V: AttributedImplement<*>> removeImplement() = implements.remove(V::class.java as Class<*>) as? V



    fun <V : AttributedImplement<*>> getImplement(type: Class<V>) = implements[type as Class<*>] as V

    fun <V: AttributedImplement<*>> getImplementOrNull(type: Class<V>) = implements[type as Class<*>] as? V

    fun <V : AttributedImplement<*>> setImplement(type: Class<V>, element: V) = implements.set(type as Class<AttributedImplement<*>>, element)

    fun <V: AttributedImplement<*>> removeImplement(type: Class<V>) = implements.remove(type as Class<*>) as? V

}