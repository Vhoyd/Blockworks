package dev.vhoyd.blockworks.api.core

import dev.vhoyd.blockworks.api.block.BlockDefinition
import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.model.BlockBreaker
import org.bukkit.block.Block
import org.bukkit.plugin.Plugin


internal fun StringBuilder.appendIterable(data : Iterable<*>) : StringBuilder {
    append("[\n")
    apply { data.forEach {
        append("  $it,\n")
    } }
    apply { removeSuffix(",\n") }
    append("\n]")
    return this
}

internal fun StringBuilder.appendMap(data : Map<*,*>) : StringBuilder {
    append("{\n")
    apply { data.forEach { (k, v) ->
        append("  $k : $v,\n")
    } }
    apply { removeSuffix(",\n") }
    append("\n}")
    return this
}

/**
 * Entry point class for working with the API.
 * @property plugin the [Plugin] using this Blockworks instance.
 * @param blockDefinitionSource a `java.util.function.Function` that, given this `Blockworks`, produces
 * some `Iterable<`[BlockDefinition]`>` that outlines every condition under which
 * Blockworks should handle custom behavior.
 * @property blockDefinitionBehavior a "configuration" of what behavior to default to if left unspecified.
 * This field itself defaults to [BlockDefinition.EMPTY_BEHAVIOR], which is a `BlockDefinition` with no defined
 * behavior whatsoever. It is recommended to override this with a custom configuration.
 * @property loggingLevel The level of logging that Blockworks should perform, ignoring anything with a lower
 * priority than the provided one.
 * @see BlockDefinition
 * @see Blockworks.LoggingLevel
 */
class Blockworks(
    internal val plugin: Plugin,
    blockDefinitionSource: java.util.function.Function<Blockworks, Iterable<BlockDefinition>>,
    internal val blockDefinitionBehavior: BlockDefinition = BlockDefinition.EMPTY_BEHAVIOR,
    internal val loggingLevel: LoggingLevel = LoggingLevel.WARN

)  {

    internal val definitions = blockDefinitionSource.apply(this)
    internal val logger = Logger(this, source = "Main", level = loggingLevel)
    internal val breakers = mutableSetOf<BlockBreaker<*>>()
    internal val blockInstanceRegistry = BlockInstanceRegistry(this)

    init {
        logger.info("Creating Blockworks object for plugin ${plugin.name}...")
    }


    @Suppress("unused") // for external use only
    fun start() {
        val eventHandler = BukkitEventListener(this)
        blockInstanceRegistry.runTaskTimer(plugin, 0, 0)
        plugin.server.pluginManager.registerEvents(eventHandler, plugin)
        logger.info("Blockworks (via ${plugin.name}) is running!")
    }

    /**
     * @return the corresponding [BlockBreaker] object for a given [BlockBreaker.delegate], or `null` if none exists.
     */
    fun <T> getBlockBreaker(breaker : T) : BlockBreaker<T>? {
        @Suppress("unchecked_cast")
        return breakers.find { it.delegate == breaker } as? BlockBreaker<T>
    }


    /**
     * @return `true` if the given [BlockBreaker] was registered, or `false` if it was already registered.
     */
    fun  registerBlockBreaker(breaker : BlockBreaker<*> ) : Boolean = breakers.add(breaker)

    /**
     * @return the [BlockDefinition] that overrides block behavior of the given [Block], or `null`
     * if no behavior is assigned to it. This method returns a potentially valid [BlockDefinition] based
     * on the behavior of [BlockDefinition.isValidInstance]
     */
    fun getDefinition(block : Block, breaker: BlockBreaker<*>) : BlockDefinition? = definitions.find { it.isValidInstance(block, breaker)}


    override fun toString(): String {
        return "Blockworks (${plugin.name})"
    }

    /**
     * Adds a [BlockInstance] to the set of ones that Blockworks will tick over and dispatch events for.
     */
    fun subscribe(block : BlockInstance) = blockInstanceRegistry.subscribe(block)

    /**
     * Removes a [BlockInstance] from the set of ones being handled.
     */
    fun unsubscribe(block : BlockInstance) = blockInstanceRegistry.unsubscribe(block)


    enum class LoggingLevel(internal val level : Byte) {
        DEBUG(4),
        INFO(3),
        WARN(2),
        ERROR(1),

        @Suppress("unused") // for external use only
        NONE(0)
    }
}