package dev.vhoyd.blockworks.model

import dev.vhoyd.blockworks.core.Blockworks
import org.bukkit.persistence.PersistentDataType
import java.util.function.BiFunction

/**
 * Models any "implement" that could be utilized by a [BlockBreaker], based on some delegate such as an `ItemStack`.
 * @see dev.vhoyd.blockworks.impl.Tool
 */
abstract class AttributedImplement<out T>(
    delegate: T,
    private val data : Map<Attribute<*,*>, Any>,
    private val overwriteData: Boolean
) : Attributable, Wrapper<T>(delegate) {


    fun applyAttributes() {

        @Suppress("UNCHECKED_CAST")
        if (overwriteData) {
            data.forEach { (key, value) -> setAttribute(key as Attribute<Any, Any>, value) }
            setAttribute(flag, this::class.java.simpleName)
        }
    }



    companion object {
        private val flag = Attribute("class", PersistentDataType.STRING)
        @JvmStatic
        fun <T : Any, V : AttributedImplement<T>> of(
            blockworks: Blockworks,
            source : T?,
            expectedId: String,
            constructor: BiFunction<Blockworks, T, V>
            ) : AttributedImplement<T>? {
            if (source == null) return null
            val obj = constructor.apply(blockworks, source)
            return if (obj[flag] == expectedId) obj else null
        }
    }

}
