package dev.vhoyd.blockworks.model

import org.bukkit.persistence.PersistentDataType

/**
 * Outlines a certain typed attribute some [Attributable] will have.
 */
data class Attribute<P : Any, C : Any>(
    val name : String,
    val persistentDataType : PersistentDataType<P, C>
)