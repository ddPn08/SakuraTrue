package run.dn5.sasa.sakuratrue.velocity.ban

import run.dn5.sasa.sakuratrue.VelocityPlugin
import run.dn5.sasa.sakuratrue.velocity.ban.commands.Ban
import run.dn5.sasa.sakuratrue.velocity.ban.commands.Pardon
import run.dn5.sasa.sakuratrue.velocity.ban.listeners.PostLoginListener

class Ban {
    val plugin = VelocityPlugin.instance
    val dataStore = BanDataStore()

    fun enable() {
        registerListeners()
        listOf(
            Ban(),
            Pardon()
        ).forEach { plugin.command.addSubCommand(it) }
    }

    private fun registerListeners() {
        listOf(
            PostLoginListener()
        ).forEach { plugin.proxy.eventManager.register(plugin, it) }
    }
}