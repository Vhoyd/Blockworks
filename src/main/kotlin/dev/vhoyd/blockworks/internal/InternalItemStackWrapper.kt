package dev.vhoyd.blockworks.internal

import dev.vhoyd.blockworks.api.model.Wrapper
import org.bukkit.inventory.ItemStack

internal class InternalItemStackWrapper(
    override val delegate: ItemStack
) : Wrapper<ItemStack>