import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.core.Config
import dev.vhoyd.blockworks.mining.MiningTool
import dev.vhoyd.blockworks.text.TextComponentWrapper
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class SimpleMain {
    companion object {
        val stickItem = TextComponentWrapper.createTextedItem(ItemStack(Material.STICK, 1), "§6§lGod Stick", "§d§oFor testing purposes only)")
        lateinit var testStick : MiningTool

        fun go(plugin : Plugin) {

            val config = Config(
                plugin,
                blockDefinitions = SimpleBlockDefinitionListCreator.getList(),
                properties = mapOf(
                    SimpleConfigProperty.IGNORE_BREAKING_POWER to false,
                    SimpleConfigProperty.IGNORE_MINING_FORTUNE to false,
                    SimpleConfigProperty.DYNAMIC_FORTUNE_SCALING to true,
                    SimpleConfigProperty.ROUNDING_METHOD to SimpleConfigProperty.RoundingMethod.FLOOR,
                    SimpleConfigProperty.MINING_RATE_SCALE to 1.0,
                )
            )
            val blockworks = Blockworks(config)
            testStick = MiningTool(blockworks, stickItem,
                mapOf(
                    SimpleMiningAttribute.MINING_SPEED to 10,
                    SimpleMiningAttribute.MINING_FORTUNE to 158,
                    SimpleMiningAttribute.BREAKING_POWER to 1
                    )
                )
            val simpleListener = SimpleBlockworksListener(blockworks)
            plugin.server.pluginManager.registerEvents(simpleListener, plugin)
        }
    }



}