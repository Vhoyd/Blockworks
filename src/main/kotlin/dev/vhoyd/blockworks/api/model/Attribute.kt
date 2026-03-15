package dev.vhoyd.blockworks.api.model

import dev.vhoyd.blockworks.internal.InternalAttribute
import org.bukkit.persistence.PersistentDataType

/**
 * Outlines a certain typed attribute some [Attributable] will have.
 */
interface Attribute<P : Any, C : Any> {
    val name : String
        get() = "blockworks-${toString()}"
    val type : PersistentDataType<P, C>


    companion object {
        operator fun <P : Any, C : Any> invoke(
            name : String,
            type : PersistentDataType<P, C>): Attribute<P, C> = InternalAttribute(name, type)

        @JvmStatic
        fun <P : Any, C : Any> create(
            name : String,
            type : PersistentDataType<P, C>): Attribute<P, C> = InternalAttribute(name, type)
    }

}