package dev.vhoyd.blockworks.mining

abstract class Wrapper<T>(val delegate: T) {
    inline fun <reified V> delegateAs(): V = delegate as V

    inline fun <reified V> delegateAsChecked(): V? = delegate as? V
}