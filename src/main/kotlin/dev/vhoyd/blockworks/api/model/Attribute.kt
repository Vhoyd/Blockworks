package dev.vhoyd.blockworks.api.model

import org.bukkit.persistence.PersistentDataType

/**
 * Outlines a certain typed attribute some [Attributable] will have.
 */
interface Attribute<P : Any, C : Any> {
    val name : String
        get() = "blockworks-${toString()}"
    val type : PersistentDataType<P, C>


}