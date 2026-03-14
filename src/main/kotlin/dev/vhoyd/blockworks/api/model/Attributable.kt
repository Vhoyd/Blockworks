package dev.vhoyd.blockworks.api.model

import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.internal.InternalAttribute
import org.bukkit.persistence.PersistentDataType
import java.util.function.BiFunction

interface Attributable {

    val attributes: MutableMap<Attribute<*, *>, Any>


    /**
     * Assigns the value of the given [Attribute].
     */
    fun <P : Any, C : Any> setAttribute(attribute: Attribute<P, C>, value : C)

    /**
     * @return the value of the given [Attribute]
     */
    fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>) : C?

    operator fun <P : Any, C : Any> set(attribute: Attribute<P, C>, value : C) : Unit = setAttribute(attribute, value)
    operator fun <P : Any, C : Any> get(attribute: Attribute<P, C>) : C? = getAttribute(attribute)



    companion object {
        @JvmStatic
        val INTERNAL_CLASS_FLAG : Attribute<String, String> = InternalAttribute("internal-class", PersistentDataType.STRING)

        @JvmStatic
        fun <T : Any, V : Attributable> of(
            blockworks: Blockworks,
            source : T?,
            expectedType: Class<out V>,
            constructor: BiFunction<Blockworks, T, V>
        ) : Attributable? {
            if (source == null) return null
            val obj = constructor.apply(blockworks, source)
            return if (obj[INTERNAL_CLASS_FLAG] == expectedType.simpleName) obj else null
        }

        inline fun <T : Any, reified  V: Attributable> of(
            blockworks: Blockworks,
            source : T?,
            constructor: BiFunction<Blockworks, T, V>
        ) : Attributable? {
            if (source == null) return null
            val obj = constructor.apply(blockworks, source)
            return if (obj[INTERNAL_CLASS_FLAG] == V::class.java.simpleName) obj else null
        }
    }

}