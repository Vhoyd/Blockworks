package dev.vhoyd.blockworks.api.model

import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.impl.PersistenceWriter
import dev.vhoyd.blockworks.internal.InternalPersistentAttributed
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.plugin.Plugin

interface PersistentAttributable : Attributable {

    val persistenceTarget: PersistentDataHolder
    val blockworks: Blockworks
    val overwrite: Boolean


    fun <P : Any, C : Any> write(
        plugin: Plugin,
        attribute: Attribute<P, C>,
        value: C
    ): Unit = PersistenceWriter.setValue(

        plugin = plugin,
        target = persistenceTarget,
        key = attribute.name,
        type = attribute.type,
        value = value
    )


    fun <P : Any, C : Any> read(
        plugin: Plugin,
        attribute: Attribute<P, C>
    ): C? = PersistenceWriter.getValue(

        plugin = plugin,
        target = persistenceTarget,
        key = attribute.name,
        type = attribute.type,
    )

    companion object {
        operator fun invoke(
            blockworks : Blockworks,
            persistenceTarget: PersistentDataHolder,
            owner: Class<*>,
            attributes: Map<Attribute<*, *>, Any> = emptyMap(),
            overwrite: Boolean = false,
        ): PersistentAttributable = InternalPersistentAttributed(blockworks, persistenceTarget, owner, attributes, overwrite)

        @JvmStatic
        @JvmOverloads
        fun create(
            blockworks : Blockworks,
            persistenceTarget: PersistentDataHolder,
            owner: Class<*>,
            attributes: Map<Attribute<*, *>, Any> = emptyMap(),
            overwrite: Boolean = false,
        ): PersistentAttributable = InternalPersistentAttributed(blockworks, persistenceTarget, owner, attributes, overwrite)
    }
}
