package dev.vhoyd.blockworks.api.model

import dev.vhoyd.blockworks.internal.InternalPersistentAttributed
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.plugin.Plugin

/**
 * Extension of [Attributable] that promises a [PersistentDataHolder], and a [Plugin] to credit
 * when reading/writing data with the [persistenceTarget]
 */
interface PersistentAttributable : Attributable {

    val persistenceTarget: PersistentDataHolder
    val plugin: Plugin


    companion object {
        operator fun invoke(
            plugin: Plugin,
            persistenceTarget: PersistentDataHolder,
            attributes: Map<Attribute<*, *>, Any> = emptyMap(),
            overwrite: Boolean = false,
        ): PersistentAttributable = InternalPersistentAttributed(plugin, persistenceTarget, attributes, overwrite)

        @JvmStatic
        @JvmOverloads
        @Suppress("unused") // for external use only
        fun create(
            plugin: Plugin,
            persistenceTarget: PersistentDataHolder,
            owner: Class<*>,
            attributes: Map<Attribute<*, *>, Any> = emptyMap(),
            overwrite: Boolean = false,
        ): PersistentAttributable = InternalPersistentAttributed(plugin, persistenceTarget, attributes, overwrite)
    }
}
