package run.dn5.sasa.sakuratrue.velocity.verifier.discord

import run.dn5.sasa.sakuratrue.VelocityPlugin
import run.dn5.sasa.sakuratrue.velocity.verifier.discord.listeners.minecraft.PostLoginListener

class Discord {
    val plugin = VelocityPlugin.instance

    private val discord = DiscordClient()

    val discordAuthStore = DiscordAuthStore()
    val codeStore = DiscordSessionStore()


    fun enable() {
        plugin.command.addSubCommand(DiscordCommand())
        registerListeners()
        setupDiscordClient()
        discordAuthStore.load()
    }

    fun disable() {
        discord.disable()
    }

    private fun registerListeners() {
        listOf(
            PostLoginListener()
        ).forEach { plugin.proxy.eventManager.register(plugin, it) }
    }

    private fun setupDiscordClient() {
        val config = plugin.getConfig()
        discord.enable(config.discord.token)
    }

}