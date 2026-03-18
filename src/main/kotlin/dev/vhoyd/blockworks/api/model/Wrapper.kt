package dev.vhoyd.blockworks.api.model

import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.internal.InternalWrapper
import java.util.function.BiFunction
import java.util.function.Predicate



/**
 * @return the delegate cast to the type, or null if it cannot be cast.
 */
inline fun <reified V> Wrapper<*>.delegateAs() : V? = delegate as? V

/**
 * Promises some delegate property along with a default casting method.
 */
interface Wrapper<out T> {
    val delegate : T

    /**
     * @return the delegate cast to the type, or null if it cannot be cast.
     */
    fun <V> delegateAs(type: Class<V>) = if (type.isInstance(delegate)) type.cast(delegate) else null


    companion object {

        /**
         * Provided the given information, validates the given delegate,
         * @return null if the delegate source is null, if the constructor function returns null, or
         * if the condition returns false. Otherwise, returns the newly constructed wrapper.
         */
        @JvmStatic
        fun <T : Any, V : Wrapper<T>> validate(
            blockworks: Blockworks,
            source: T?,
            constructor: BiFunction<Blockworks, T, V?>,
            condition: Predicate<V>
        ): V? {
            if (source == null) return null
            val obj = constructor.apply(blockworks, source) ?: return null
            return if (condition.test(obj)) obj else null
        }

        /**
         * Creates a default implementation object.
         */
        operator fun <T> invoke(
            delegate : T
        ) : Wrapper<T> = InternalWrapper(delegate)


        /**
         * Creates a default implementation object.
         */
        @JvmStatic
        fun <T> create(
            delegate : T
        ) : Wrapper<T> = InternalWrapper(delegate)
    }
}