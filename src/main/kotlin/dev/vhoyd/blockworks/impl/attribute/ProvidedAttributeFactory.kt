package dev.vhoyd.blockworks.impl.attribute

import dev.vhoyd.blockworks.api.model.Attribute
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.concurrent.ConcurrentHashMap

/**
 * Produces named attributes of a selected type. A given `AttributeProducer` remembers named attributes it produces to
 * reduce needless object replication.
 */
@Suppress("unused") // external use only
object ProvidedAttributeFactory {
    val BOOLEAN : AttributeProducer<Byte, Boolean> = AttributeProducerImpl(PersistentDataType.BOOLEAN)
    val BYTE : AttributeProducer<Byte, Byte> = AttributeProducerImpl(PersistentDataType.BYTE)
    val BYTE_ARRAY : AttributeProducer<ByteArray, ByteArray> = AttributeProducerImpl(PersistentDataType.BYTE_ARRAY)
    val DOUBLE : AttributeProducer<Double, Double> = AttributeProducerImpl(PersistentDataType.DOUBLE)
    val FLOAT : AttributeProducer<Float, Float> = AttributeProducerImpl(PersistentDataType.FLOAT)
    val INTEGER : AttributeProducer<Int, Int> = AttributeProducerImpl(PersistentDataType.INTEGER)
    val INTEGER_ARRAY : AttributeProducer<IntArray, IntArray> = AttributeProducerImpl(PersistentDataType.INTEGER_ARRAY)
    val LONG : AttributeProducer<Long, Long> = AttributeProducerImpl(PersistentDataType.LONG)
    val LONG_ARRAY : AttributeProducer<LongArray, LongArray> = AttributeProducerImpl(PersistentDataType.LONG_ARRAY)
    val SHORT : AttributeProducer<Short, Short> = AttributeProducerImpl(PersistentDataType.SHORT)
    val STRING : AttributeProducer<String, String> = AttributeProducerImpl(PersistentDataType.STRING)
    val TAG_CONTAINER : AttributeProducer<PersistentDataContainer, PersistentDataContainer> = AttributeProducerImpl(PersistentDataType.TAG_CONTAINER)

    fun <P : Any,C : Any> custom(type: PersistentDataType<P,C>) : AttributeProducer<P, C> = AttributeProducerImpl(type)

    interface AttributeProducer<P : Any,C : Any> {

        /**
         *  Returns the typed attribute associated with the name, either as an
         *  already-existing object or as a newly created one.
         */
        fun named(name: String) : Attribute<P, C>


        operator fun get(name: String) : Attribute<P,C>
    }

    private class AttributeProducerImpl<P : Any, C : Any>(private val type: PersistentDataType<P,C>) : AttributeProducer<P,C> {

        // Concurrency is probably overkill, but you never know.
        // It took me 18 attempts to spell "know" just then, please help me.
        private val created: ConcurrentHashMap<String, Attribute<P,C>> = ConcurrentHashMap()
        override fun named(name: String): Attribute<P, C> = created.computeIfAbsent(name) { ProvidedAttribute(name, type) }

        override fun get(name: String): Attribute<P, C> = named(name)
    }
}



