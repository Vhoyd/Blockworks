package dev.vhoyd.blockworks.model

import dev.vhoyd.blockworks.core.Blockworks
import org.bukkit.persistence.PersistentDataType
import java.util.function.BiFunction

object ImplementFactory {

    val INTERNAL_CLASS_FLAG = Attribute("class", PersistentDataType.STRING)
    @JvmStatic
    fun <T : Any, V : Implement<T>> of(
        blockworks: Blockworks,
        source : T?,
        expectedType: Class<out BlockBreaker<V>>,
        constructor: BiFunction<Blockworks, T, V>
    ) : Implement<T>? {
        if (source == null) return null
        val obj = constructor.apply(blockworks, source)
        return if (obj[INTERNAL_CLASS_FLAG] == expectedType.simpleName) obj else null
    }

    inline fun <T : Any, reified  V: Implement<T>> of(
        blockworks: Blockworks,
        source : T?,
        constructor: BiFunction<Blockworks, T, V>
    ) : Implement<T>? {
        if (source == null) return null
        val obj = constructor.apply(blockworks, source)
        return if (obj[INTERNAL_CLASS_FLAG] == V::class.java.simpleName) obj else null
    }
}