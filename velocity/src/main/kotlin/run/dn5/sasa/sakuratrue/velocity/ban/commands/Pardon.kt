package run.dn5.sasa.sakuratrue.velocity.ban.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import com.velocitypowered.api.command.CommandSource
import run.dn5.sasa.sakuratrue.VelocityPlugin
import run.dn5.sasa.sakuratrue.velocity.Prompt
import java.util.*
import kotlin.jvm.optionals.getOrNull

class Pardon : LiteralArgumentBuilder<CommandSource>("pardon") {
    private val plugin = VelocityPlugin.instance

    override fun build(): LiteralCommandNode<CommandSource> {
        then(
            RequiredArgumentBuilder.argument<CommandSource, String>(
                "player",
                StringArgumentType.string()
            )
                .suggests { _, builder ->
                    plugin.ban.dataStore.list().forEach { builder.suggest(it.username) }
                    builder.buildFuture()
                }
                .executes {
                    val username = StringArgumentType.getString(it, "player")
                    val data =
                        plugin.ban.dataStore.list().find { data -> data.username == username }
                            ?: return@executes Prompt.err(it.source, "Player not found")
                    plugin.ban.dataStore.pardon(data.uuid)
                    Prompt.info(it.source, "Pardoned $username")
                }
        )
        return super.build()
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun ban(source: CommandSource, username: String, reason: String, expires: Long): Int {
        val player =
            plugin.proxy.getPlayer(username).getOrNull() ?: return Prompt.err(source, "Player not found")
        plugin.ban.dataStore.ban(player, reason, expires)
        val date = if (expires == 0L) "never" else Date(expires).toString()
        player.disconnect(
            Prompt.template(
                "You have been banned from the server.\nReason: $reason\nExpires: $date",
                Prompt.Status.ERROR,
                ""
            )
        )
        return 0
    }
}