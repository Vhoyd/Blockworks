package dev.vhoyd.blockworks.api.model

import dev.vhoyd.blockworks.internal.InternalWrapper

/**
 * Promises some delegate property along with some casting methods.
 */

inline fun <reified V> Wrapper<*>.delegateAs() : V? = delegate as? V

interface Wrapper<out T> {
    val delegate : T

    fun <V> delegateAs(type: Class<V>) = if (type.isInstance(delegate)) type.cast(delegate) else null


    companion object {
        operator fun <T> invoke(
            delegate : T
        ) : Wrapper<T> = InternalWrapper(delegate)


        @JvmStatic
        fun <T> create(
            delegate : T
        ) : Wrapper<T> = InternalWrapper(delegate)
    }
}