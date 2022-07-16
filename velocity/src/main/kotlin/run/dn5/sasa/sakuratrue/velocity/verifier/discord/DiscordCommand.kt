package run.dn5.sasa.sakuratrue.velocity.verifier.discord

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import com.velocitypowered.api.command.CommandSource
import run.dn5.sasa.sakuratrue.VelocityPlugin
import run.dn5.sasa.sakuratrue.velocity.Messages
import run.dn5.sasa.sakuratrue.velocity.Prompt

class DiscordCommand : LiteralArgumentBuilder<CommandSource>("discord") {
    override fun build(): LiteralCommandNode<CommandSource> {
        val plugin = VelocityPlugin.instance
        then(
            literal<CommandSource>("reauth").then(
                RequiredArgumentBuilder.argument<CommandSource, String>(
                    "player",
                    StringArgumentType.string()
                )
                    .suggests { _, builder ->
                        plugin.proxy.allPlayers.forEach { builder.suggest(it.username) }
                        builder.buildFuture()
                    }
                    .executes {
                        val username = StringArgumentType.getString(it, "player")
                        val auth = plugin.discord.discordAuthStore
                        if (!auth.verified(username)) return@executes Prompt.err(
                            it.source,
                            "Player not verified."
                        )
                        auth.reauth(username)
                        plugin.proxy.getPlayer(username).ifPresent { player ->
                            val reason = arguments.drop(2).joinToString(" ")
                            player.disconnect(
                                Prompt.template(
                                    reason.ifEmpty { plugin.messages.getMessage(Messages.MessageKey.DISCORD_RE_AUTHENTICATE_REQUESTED) },
                                    Prompt.Status.INFO
                                )
                            )
                        }
                        it.source.sendMessage(
                            Prompt.template(
                                "Reauthentication requested.",
                                Prompt.Status.INFO
                            )
                        )
                        0
                    }
            )
        )
        return super.build()
    }
}