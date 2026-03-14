package dev.vhoyd.blockworks.api.model

import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.impl.PersistenceWriter
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.plugin.Plugin

interface PersistentAttributable : Attributable {

    val persistenceTarget : PersistentDataHolder
    val blockworks : Blockworks
    val overwrite: Boolean


    fun <P : Any, C : Any> write(
        plugin : Plugin,
         attribute : Attribute<P, C>,
         value : C
         ) : Unit = PersistenceWriter.setValue(

        plugin = plugin,
        target = persistenceTarget,
        key = attribute.name,
        type = attribute.type,
        value = value
    )


    fun <P : Any, C : Any> read(
        plugin : Plugin,
        attribute : Attribute<P, C>
    ) : C? = PersistenceWriter.getValue(

        plugin = plugin,
        target = persistenceTarget,
        key = attribute.name,
        type = attribute.type,
    )
}