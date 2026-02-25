package dev.vhoyd.blockworks.mining

import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.core.Blockworks

@Suppress("Unchecked_cast")
abstract class BlockBreaker<T>(
    delegate : T,
    val blockworks: Blockworks,
    defaultAttributes : Map<Attribute<*,*>, Any>,
    val elements : MutableMap<Class<AttributedElement<*>>, Any>

) : Attributable, Wrapper<T>(delegate) {
    var currentBlock: BlockInstance? = null

    init {
        defaultAttributes.forEach { (key, value) -> set(key as Attribute<Any, Any>, value) }
    }
    inline fun <reified V : AttributedElement<*>> getElement() : V {
        return elements[V::class.java as Class<*>] as V
    }

    inline fun <reified V: AttributedElement<*>> getElementChecked() : V? {
        return elements[V::class.java as Class<*>] as? V
    }

    inline fun <reified V : AttributedElement<*>> setElement(element : V) {
        elements[V::class.java as Class<AttributedElement<*>>] = element
    }

    inline fun <reified V: AttributedElement<*>> removeElement() : V? = elements.remove(V::class.java as Class<*>) as? V

}