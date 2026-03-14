package dev.vhoyd.blockworks.api.model

/**
 * Promises some delegate property along with some casting methods.
 */

inline fun <reified V> Wrapper<*>.delegateAs() : V? = delegate as? V

interface Wrapper<out T> {
    val delegate : T

    fun <V> delegateAs(type: Class<V>) = if (type.isInstance(delegate)) type.cast(delegate) else null


}