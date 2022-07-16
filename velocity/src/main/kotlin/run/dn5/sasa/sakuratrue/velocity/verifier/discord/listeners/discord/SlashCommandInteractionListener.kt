package run.dn5.sasa.sakuratrue.velocity.verifier.discord.listeners.discord

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import run.dn5.sasa.sakuratrue.velocity.Messages
import run.dn5.sasa.sakuratrue.VelocityPlugin

class SlashCommandInteractionListener : ListenerAdapter() {
    private val plugin = VelocityPlugin.instance

    override fun onSlashCommandInteraction(e: SlashCommandInteractionEvent) {
        when (e.subcommandName) {
            "verify" -> verify(e)
        }
    }

    private fun verify(e: SlashCommandInteractionEvent) {
        val config = plugin.getConfig()
        val guild = e.guild
        val member = e.member

        if (guild == null || member == null) return
        if (e.name != "sakuratrue") return

        val username = e.options.find { it.name == "username" }?.asString ?: return
        val code = e.options.find { it.name == "code" }?.asString ?: return


        val messages = plugin.messages

        val session = plugin.discord.codeStore.getSession(username)
        if (session?.code != code) {
            e.reply(messages.getMessage(Messages.MessageKey.DISCORD_WRONG_CODE)).setEphemeral(true).queue()
            return
        }

        plugin.discord.discordAuthStore.verify(session.uuid, session.username, member)

        if (config.discord.changeNickName) {
            try {
                member.modifyNickname(session.username).queue()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (config.discord.addRole.enabled) {
            config.discord.addRole.roles.forEach {
                try {
                    val role = guild.getRoleById(it)
                    if (role != null) {
                        guild.addRoleToMember(member, role).queue()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        }
        e.reply(messages.getMessage(Messages.MessageKey.DISCORD_SUCCESS_VERIFY)).setEphemeral(true).queue()
    }
}