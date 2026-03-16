package dev.vhoyd.blockworks.api.model

import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.internal.InternalAttribute
import dev.vhoyd.blockworks.internal.InternalAttributed
import org.bukkit.persistence.PersistentDataType
import java.util.function.BiFunction
import java.util.function.Predicate

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
            constructor: BiFunction<Blockworks, T, V?>,
            condition: Predicate<V>
        ) : Attributable? {
            if (source == null) return null
            val obj = constructor.apply(blockworks, source) ?: return null
            return if (condition.test(obj)) obj else null
        }

        operator fun invoke(
            owner : Class<*>,
            attributes: Map<Attribute<*, *>, Any> = emptyMap()
        ) : Attributable = InternalAttributed(owner, attributes)

        @JvmStatic
        @JvmOverloads
        fun create(
            owner : Class<*>,
            attributes: Map<Attribute<*, *>, Any> = emptyMap()
        ) : Attributable = InternalAttributed(owner, attributes)
    }

}