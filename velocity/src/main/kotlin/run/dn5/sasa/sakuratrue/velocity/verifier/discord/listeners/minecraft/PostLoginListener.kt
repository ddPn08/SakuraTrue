package run.dn5.sasa.sakuratrue.velocity.verifier.discord.listeners.minecraft

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PostLoginEvent
import run.dn5.sasa.sakuratrue.velocity.Messages
import run.dn5.sasa.sakuratrue.VelocityPlugin

class PostLoginListener {
    @Subscribe
    fun onServerConnected(e: PostLoginEvent) {
        val plugin = VelocityPlugin.instance
        val discord = plugin.discord
        if (discord.discordAuthStore.verified(e.player.uniqueId)) {
            discord.discordAuthStore.checkUsername(e.player)
            return
        }
        val session =
            discord.codeStore.getSession(e.player.username) ?: discord.codeStore.createCode(e.player)

        e.player.disconnect(
            plugin.messages.getMessageComponent(
                Messages.MessageKey.DISCORD_SHOW_CODE,
                Messages.PlaceHolderData(null, session.code)
            )
        )
    }
}