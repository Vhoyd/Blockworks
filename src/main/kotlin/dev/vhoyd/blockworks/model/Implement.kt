package dev.vhoyd.blockworks.model

/**
 * Models any "implement" that could be utilized by a [BlockBreaker], based on some delegate such as an `ItemStack`.
 * If creating a new Implement, make sure to call [applyAttributes].
 * @see applyAttributes
 * @see dev.vhoyd.blockworks.impl.Tool
 */
abstract class Implement<out T>(
    delegate: T,
    private val data : Map<Attribute<*,*>, Any>,
    private var overwriteData: Boolean
) : Attributable, Wrapper<T>(delegate) {

    /**
     * Loads attributues into this Implement. Since the Attributable methods are abstract, this cannot be called
     * during init.
     */
    fun applyAttributes() {
        @Suppress("UNCHECKED_CAST")
        if (overwriteData) {
            overwriteData = false
            data.forEach { (key, value) -> setAttribute(key as Attribute<Any, Any>, value) }
            setAttribute(ImplementFactory.INTERNAL_CLASS_FLAG, this::class.java.simpleName)
        }
    }

}
