package dev.vhoyd.blockworks.api.model

import org.bukkit.persistence.PersistentDataType

/**
 * Outlines a certain typed attribute some [Attributable] will have.
 */
data class Attribute<P : Any, C : Any>(
    internal val name : String,
    internal val persistentDataType : PersistentDataType<P, C>
)