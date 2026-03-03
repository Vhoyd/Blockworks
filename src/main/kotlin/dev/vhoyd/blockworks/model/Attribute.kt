package dev.vhoyd.blockworks.model

import org.bukkit.persistence.PersistentDataType

/**
 * Outlines a certain attribute some [Attributable] will have. It is recommended to create a list of
 * constants of this class for clarity. For an example, see the [SimpleMiningAttribute] class in the test folder.
 * @see SimpleMiningAttribute
 */
data class Attribute<P : Any, C : Any>(
    val name : String,
    val persistentDataType : PersistentDataType<P, C>
)