package run.dn5.sasa.sakuratrue.velocity

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import com.velocitypowered.api.command.CommandSource
import net.kyori.adventure.text.minimessage.MiniMessage
import run.dn5.sasa.sakuratrue.VelocityPlugin

class SakuraTrueCommand : LiteralArgumentBuilder<CommandSource>("sakuratrue") {
    private val plugin = VelocityPlugin.instance

    private val usage = MiniMessage.miniMessage().deserialize(
        """
        <aqua>><green> Usage:
        <aqua>><green>     /sakuratrue <command>
        <aqua>><green> 
        <aqua>><green> Base:
        <aqua>><green>     /sakuratrue help
        <aqua>><green>     /sakuratrue reload
        <aqua>><green>     /sakuratrue ban <player> [<reason>]
        <aqua>><green>     /sakuratrue pardon <player>
        <aqua>><green> 
        <aqua>><green> Discord:
        <aqua>><green>     /sakuratrue discord reauth <player>
        """.trimIndent()
    )

    private val subCommands = mutableListOf<LiteralArgumentBuilder<CommandSource>>()

    override fun build(): LiteralCommandNode<CommandSource> {
        requires {
            it.hasPermission("sakuratrue.command.sakuratrue")
        }
        subCommands.addAll(listOf(
            literal<CommandSource?>("help").executes { context ->
                context.source.sendMessage(usage)
                1
            },
            literal<CommandSource>("reload").executes {
                plugin.unload()
                plugin.load()
                Prompt.info(it.source, "Reloaded")
            }
        ))
        subCommands.forEach {
            then(it.build())
        }
        return super.build()
    }

    fun addSubCommand(command: LiteralArgumentBuilder<CommandSource>) {
        subCommands.add(command)
    }
}