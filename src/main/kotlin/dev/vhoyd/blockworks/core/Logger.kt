package dev.vhoyd.blockworks.core

/**
 * Internal class for debug logging. Uses a source from Blockworks as well as the plugin its spawned from when logging.
 */
internal class Logger(
    private val blockworks : Blockworks,
    source : String?,
    private val level  : Config.LoggingLevel,
) {
    private val sourceText = source ?: ""
    private val logger = blockworks.plugin.logger
    private val name = blockworks.plugin.name

    fun <T> info(thing : T) {
        if (level.level < Config.LoggingLevel.INFO.level) return
        logger.info("~Blockworks-${name}@${sourceText}/INFO: ${thing.toString()}")
    }

    fun <T> error(thing : T) {
        if (level.level < Config.LoggingLevel.ERROR.level) return
        logger.severe("~Blockworks-${name}@${sourceText}/ERR: ${thing.toString()}")
    }

    fun <T> warn(thing : T) {
        if (level.level < Config.LoggingLevel.WARN.level) return
        logger.warning("~Blockworks-${name}@${sourceText}/WARN: ${thing.toString()}")
    }

    fun <T> debug(thing : T) {
        if (level.level < Config.LoggingLevel.DEBUG.level) return
        logger.info("~Blockworks-${name}@${sourceText}/DEBUG: ${thing.toString()}")
    }

    fun context(context: String) : Logger {
        return Logger(blockworks, context, level)
    }

}