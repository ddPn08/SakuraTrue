package run.dn5.sasa.sakuratrue.velocity.ban.commands

import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import com.velocitypowered.api.command.CommandSource
import run.dn5.sasa.sakuratrue.VelocityPlugin
import run.dn5.sasa.sakuratrue.velocity.Prompt
import java.util.*
import kotlin.jvm.optionals.getOrNull

class Ban : LiteralArgumentBuilder<CommandSource>("ban") {
    private val plugin = VelocityPlugin.instance

    override fun build(): LiteralCommandNode<CommandSource> {
        then(
            RequiredArgumentBuilder.argument<CommandSource, String>(
                "player",
                StringArgumentType.string()
            )
                .suggests { _, builder ->
                    plugin.proxy.allPlayers.forEach { builder.suggest(it.username) }
                    builder.buildFuture()
                }
                .executes {
                    ban(
                        it.source,
                        StringArgumentType.getString(it, "player"),
                        "Banned by operator",
                        0
                    )
                }.then(
                    RequiredArgumentBuilder.argument<CommandSource, String>("reason", StringArgumentType.string())
                        .executes {
                            ban(
                                it.source,
                                StringArgumentType.getString(it, "player"),
                                StringArgumentType.getString(it, "reason"),
                                0
                            )
                        }.then(
                            RequiredArgumentBuilder.argument<CommandSource, Long>("expires", LongArgumentType.longArg())
                                .executes {
                                    val days = LongArgumentType.getLong(it, "expires")
                                    val time = System.currentTimeMillis() + days * 24 * 60 * 60 * 1000
                                    ban(
                                        it.source,
                                        StringArgumentType.getString(it, "player"),
                                        StringArgumentType.getString(it, "reason"),
                                        time
                                    )
                                }
                        )
                )
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
        return Prompt.info(source, "Banned $username")
    }
}