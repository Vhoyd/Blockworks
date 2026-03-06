package dev.vhoyd.blockworks.model

/**
 * Promises some delegate property along with some casting methods.
 */
abstract class Wrapper<T>(val delegate: T) {
    inline fun <reified V> delegateAs(): V = delegate as V

    inline fun <reified V> delegateAsOrNull(): V? = delegate as? V

    @Suppress("UNCHECKED_CAST")
    fun <V> getDelegateAs(type : Class<V>) = delegate as
            V
    @Suppress("UNCHECKED_CAST")
    fun <V> getDelegateAsOrNull(type : Class<V>) = delegate as? V
}