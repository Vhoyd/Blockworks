package dev.vhoyd.blockworks.internal

import dev.vhoyd.blockworks.api.model.Attribute
import org.bukkit.persistence.PersistentDataType

internal class InternalAttribute<P : Any, C : Any>(
    override val name: String,
    override val type: PersistentDataType<P, C>
) : Attribute<P, C> {
    override fun toString(): String = name
}
