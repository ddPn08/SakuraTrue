package run.dn5.sasa.sakuratrue.velocity.verifier.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import run.dn5.sasa.sakuratrue.VelocityPlugin
import run.dn5.sasa.sakuratrue.velocity.verifier.discord.listeners.discord.SlashCommandInteractionListener

class DiscordClient : ListenerAdapter() {
    private val plugin = VelocityPlugin.instance
    private var jda: JDA? = null

    fun enable(token: String) {
        jda = JDABuilder.createDefault(token).build()
        jda!!.awaitReady()
        jda!!.addEventListener(this)
        jda!!.addEventListener(SlashCommandInteractionListener())
        registerCommands()
    }

    fun disable() {
        jda?.shutdownNow()
    }

    private fun registerCommands() {
        val config = plugin.getConfig()
        val commands = jda!!.updateCommands()
        commands.addCommands(
            Commands.slash("sakuratrue", config.discord.commandDescriptions.root).addSubcommands(
                SubcommandData("verify", config.discord.commandDescriptions.verify)
                    .addOption(
                        OptionType.STRING,
                        "username",
                        "Username",
                        true
                    )
                    .addOption(OptionType.STRING, "code", "Code", true)
            )
        )
        commands.queue()
    }
}