package dev.vhoyd.blockworks.model

import dev.vhoyd.blockworks.core.Blockworks
import org.bukkit.persistence.PersistentDataType
import java.util.function.BiFunction

/**
 * Models any "implement" that could be utilized by a [BlockBreaker], based on some delegate such as an `ItemStack`.
 * @see dev.vhoyd.blockworks.impl.Tool
 */
abstract class AttributedImplement<T>(
    delegate: T,
    private val data : Map<Attribute<*,*>, Any>,
    private val overwriteData: Boolean
) : Attributable, Wrapper<T>(delegate) {


    fun applyAttributes() {
        @Suppress("UNCHECKED_CAST")
        if (overwriteData) data.forEach { (key, value) -> setAttribute(key as Attribute<Any, Any>, value) }
        setAttribute(flag, parentClass.simpleName)
    }

    companion object {
        private val flag = Attribute("class", PersistentDataType.STRING)
        private val parentClass = this::class.java.enclosingClass
        @JvmStatic
        fun <T : Any, V : AttributedImplement<T>> of(
            blockworks: Blockworks,
            source : T,
            expectedId: String,
            constructor: BiFunction<Blockworks, T, V>
            ) : AttributedImplement<T>? {

            val obj = constructor.apply(blockworks, source)
            return if (obj.getAttribute(flag) == expectedId) obj else null
        }

        @JvmStatic
        fun <T : Any, V : AttributedImplement<T>> ofOrNew(
            blockworks: Blockworks,
            source : T,
            expectedId: String,
            ofConstructor: BiFunction<Blockworks, T, V>,
            newConstructor: BiFunction<Blockworks, T, V>
        ) : AttributedImplement<T> {
            val obj = ofConstructor.apply(blockworks, source)
            val newObj = newConstructor.apply(blockworks, source)
            newObj.setAttribute(flag, expectedId)
            return if (obj.getAttribute(flag) == expectedId) obj else newObj
        }
    }

}
