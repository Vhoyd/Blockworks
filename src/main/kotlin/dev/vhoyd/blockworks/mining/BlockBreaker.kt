package dev.vhoyd.blockworks.mining

import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.core.Blockworks

@Suppress("Unchecked_cast")
abstract class BlockBreaker<T>(
    delegate : T,
    val blockworks: Blockworks,
    defaultAttributes : Map<Attribute<*,*>, Any>,
    val elementContainer : ElementContainer

) : Attributable, Wrapper<T>(delegate) {
    var currentBlock: BlockInstance? = null

    init {
        defaultAttributes.forEach { (key, value) -> set(key as Attribute<Any, Any>, value) }
    }

    class ElementContainer(
        val map : Map<Class<Element<*>>, Any>
    ) {
        inline fun <reified V : Element<*>> retrieve() : V {
            return map[V::class.java as Class<*>] as V

        }

        inline fun <reified V: Element<*>> retrieveChecked() : V? {
            return map[V::class.java as Class<*>] as? V

        }


    }

}