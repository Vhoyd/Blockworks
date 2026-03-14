package dev.vhoyd.blockworks.api.model

/**
 * Promises some delegate property along with some casting methods.
 */
abstract class Wrapper<out T>(val delegate: T) {
    inline fun <reified V> delegateAs() = delegate as? V


    fun <V> delegateAs(type: Class<V>) = if (type.isInstance(delegate)) type.cast(delegate) else null

    override fun toString(): String {
        return "${this::class.java.simpleName}(delegate: $delegate)"
    }

}