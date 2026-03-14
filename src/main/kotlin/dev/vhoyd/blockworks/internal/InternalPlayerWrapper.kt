package dev.vhoyd.blockworks.internal

import dev.vhoyd.blockworks.api.model.Wrapper
import org.bukkit.entity.Player

internal class InternalPlayerWrapper(override val delegate: Player) : Wrapper<Player>