package dev.vhoyd.blockworks.model

import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.core.Blockworks


/**
 * Defines behavior for anything targeting a [BlockInstance]. Can hold various [Attribute]s and various [Implement]s
 * for parsing and logic.
 * @see dev.vhoyd.blockworks.impl.BlockworksPlayer
 */
@Suppress("Unchecked_cast", "unused")
abstract class BlockBreaker<out T>(
    delegate : T,
    val blockworks: Blockworks,
    defaultAttributes : Map<Attribute<*,*>, Any>,
    defaultImplements : Map<Class<out Implement<*>>, Implement<*>>,
) : Attributable, Wrapper<T>(delegate) {
    var currentBlock: BlockInstance? = null
    val implements : MutableMap<Class<out Implement<*>>, Implement<*>>

    init {
        defaultAttributes.forEach { (key, value) -> set(key as Attribute<Any, Any>, value) }
        implements = defaultImplements.toMutableMap()
        blockworks.registerBlockBreaker(this)
    }
    inline fun <reified V : Implement<*>> getImplement() = implements[V::class.java as Class<*>] as? V

    inline fun <reified V : Implement<*>> setImplement(element : V) = implements.set(V::class.java as Class<Implement<*>>, element)

    inline fun <reified V: Implement<*>> removeImplement() = implements.remove(V::class.java as Class<*>) as? V



    fun <V : Implement<*>> getImplement(type: Class<V>) = implements[type as Class<*>] as? V

    fun <V : Implement<*>> setImplement(type: Class<V>, element: V) = implements.set(type as Class<Implement<*>>, element)

    fun <V: Implement<*>> removeImplement(type: Class<V>) = implements.remove(type as Class<*>) as? V

}