package dev.vhoyd.blockworks.model

interface Attributable {
    /**
     * Assigns the value of the given [Attribute].
     */
    fun <P : Any, C : Any> setAttribute(attribute: Attribute<P, C>, value : C)

    /**
     * @return the value of the given [Attribute]
     */
    fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>) : C?

    operator fun <P : Any, C : Any> set(attribute: Attribute<P, C>, value : C) = setAttribute(attribute, value)
    operator fun <P : Any, C : Any> get(attribute: Attribute<P, C>) = getAttribute(attribute)
}