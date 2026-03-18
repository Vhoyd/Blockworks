package dev.vhoyd.blockworks.api.model

import dev.vhoyd.blockworks.internal.InternalAttributed

/**
 * Promises behavior for anything intended to have [Attribute]s.
 *
 * Accessing [attributes] (Kotlin) or calling `getAttributes()` (Java)
 * directly for modification is discouraged and should be left to [getAttribute] and [setAttribute]
 *
 * @see dev.vhoyd.blockworks.impl.Tool
 */
interface Attributable {

    val attributes: MutableMap<Attribute<*, *>, Any>

    fun <P : Any, C : Any> setAttribute(attribute: Attribute<P, C>, value: C)

    /**
     * @return the value of the given [Attribute], or null if it doesn't exist.
     */
    fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>): C?

    operator fun <P : Any, C : Any> set(attribute: Attribute<P, C>, value: C): Unit = setAttribute(attribute, value)
    operator fun <P : Any, C : Any> get(attribute: Attribute<P, C>): C? = getAttribute(attribute)


    companion object {

        /**
         * Creates a default implementation object.
         */
        operator fun invoke(
            attributes: Map<Attribute<*, *>, Any> = emptyMap()
        ): Attributable = InternalAttributed(attributes)

        /**
         * Creates a default implementation object.
         */
        @JvmStatic
        @JvmOverloads
        fun create(
            attributes: Map<Attribute<*, *>, Any> = emptyMap()
        ): Attributable = InternalAttributed(attributes)
    }

}