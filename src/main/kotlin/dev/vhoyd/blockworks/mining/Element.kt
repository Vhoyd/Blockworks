package dev.vhoyd.blockworks.mining

abstract class Element<T>(
    delegate: T,
    data : Map<Attribute<*,*>, Any>
) : Attributable, Wrapper<T>(delegate) {

    init {
        @Suppress("UNCHECKED_CAST")
        data.forEach { (key, value) -> setAttribute(key as Attribute<Any, Any>, value) }
    }



}
