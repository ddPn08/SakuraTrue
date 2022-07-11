package run.dn5.sasa.sakuratrue.velocity

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import run.dn5.sasa.sakuratrue.VelocityPlugin
import run.dn5.sasa.sakuratrue.velocity.listeners.discord.SlashCommandInteractionListener

class DiscordClient(
    private val plugin: VelocityPlugin
) : ListenerAdapter() {
    private var jda: JDA? = null

    fun enable(token: String) {
        this.jda = JDABuilder.createDefault(token).build()
        this.jda!!.awaitReady()
        this.jda!!.addEventListener(this)
        this.jda!!.addEventListener(SlashCommandInteractionListener(this.plugin))
        this.registerCommands()
    }

    fun disable() {
        this.jda?.shutdownNow()
    }

    private fun registerCommands() {
        val config = this.plugin.getConfig()
        val commands = this.jda!!.updateCommands()
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