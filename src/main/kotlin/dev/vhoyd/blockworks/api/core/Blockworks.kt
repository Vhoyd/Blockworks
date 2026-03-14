package dev.vhoyd.blockworks.api.core

import dev.vhoyd.blockworks.api.block.BlockDefinition
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
 * @property config a [Config] object created by the user of this plugin.
 * @property plugin the [Plugin] using this Blockworks instance.
 * @property logger a [Logger] used for a pass-around debug output, not very necessary in this class itself.
 * This is internal, please use your own logging object.
 * @property breakers a list of [BlockBreaker]s that keeps track of what [BlockBreaker.delegate]s will experience custom behavior.
 */
class Blockworks(val config: Config)  {

    internal val plugin : Plugin = config.plugin
    internal val logger = Logger(this, source = "Main", level = config.loggingLevel)
    internal val breakers = mutableSetOf<BlockBreaker<*>>()
    val blockInstanceManager = BlockInstanceManager(this)

    init {
        logger.info("Creating Blockworks object for plugin ${plugin.name}...")
    }


    fun start() {
        val eventHandler = BukkitEventListener(this)
        blockInstanceManager.runTaskTimer(plugin, 0, 0)
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
     * if no behavior is assigned to it. \nThis method returns a potentially valid [BlockDefinition] based
     * on the behavior of [BlockDefinition.isValidInstance]
     */
    fun getDefinition(block : Block, breaker: BlockBreaker<*>) : BlockDefinition? = config.blockDefinitions.find { it.isValidInstance(block, breaker)}


    override fun toString(): String {
        return "Blockworks(${plugin.name})"
    }
}