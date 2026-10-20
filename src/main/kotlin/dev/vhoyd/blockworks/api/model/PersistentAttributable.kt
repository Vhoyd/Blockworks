package dev.vhoyd.blockworks.api.model

import dev.vhoyd.blockworks.api.pdc.PersistenceWriter
import dev.vhoyd.blockworks.impl.attribute.ProvidedPersistentAttributed
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.plugin.Plugin

/**
 * Extension of [Attributable] that promises a [PersistentDataHolder], and a [Plugin] to credit
 * when reading/writing data with the [persistenceTarget]
 */
interface PersistentAttributable : Attributable {

    val persistenceTarget: PersistentDataHolder
    val plugin: Plugin



    override fun <P : Any, C : Any> setAttribute(
        attribute: Attribute<P, C>,
        value: C
    ) {
        super.setAttribute(attribute, value)
        PersistenceWriter.setValue(plugin, persistenceTarget, attribute, value)
    }


    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>): C? {

        val value = super.getAttribute(attribute)
        value?.let { return it }
        val pValue = PersistenceWriter.getValue(plugin, persistenceTarget, attribute)
        pValue?.let { super.setAttribute(attribute, pValue) }
        return pValue
    }


    companion object {

        @JvmStatic
        @JvmOverloads
        @Suppress("unused") // for external use only
        fun default(
            plugin: Plugin,
            persistenceTarget: PersistentDataHolder,
            owner: Class<*>,
            attributes: Map<Attribute<*, *>, Any> = emptyMap(),
            overwrite: Boolean = false,
        ): PersistentAttributable = ProvidedPersistentAttributed(plugin, persistenceTarget, attributes, overwrite)
    }
}
