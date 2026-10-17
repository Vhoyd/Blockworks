package dev.vhoyd.blockworks.api.model

import dev.vhoyd.blockworks.internal.InternalAttribute
import org.bukkit.persistence.PersistentDataType

/**
 * Outlines a certain typed attribute some [Attributable] will have.
 * Uses the same generic typing as [PersistentDataType], for compatibility with [org.bukkit.persistence.PersistentDataContainer]s.
 */
interface Attribute<P : Any, C : Any> {
    val name : String
        get() = "blockworks-${toString().lowercase()}"
    val type : PersistentDataType<P, C>


    companion object {

        /**
         * Creates a default implementation object.
         */
        @JvmStatic
        @Suppress("unused") // for external use only
        fun <P : Any, C : Any> default(
            name : String,
            type : PersistentDataType<P, C>): Attribute<P, C> = InternalAttribute(name, type)
    }

}