package run.dn5.sasa.sakuratrue.velocity.listeners.minecraft

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PostLoginEvent
import run.dn5.sasa.sakuratrue.velocity.Messages
import run.dn5.sasa.sakuratrue.VelocityPlugin

class PostLoginListener(
    private val plugin: VelocityPlugin
) {
    @Subscribe
    fun onServerConnected(e: PostLoginEvent) {
        if (this.plugin.authStore.verified(e.player.uniqueId)) {
            this.plugin.authStore.checkUsername(e.player)
            return
        }
        val session =
            this.plugin.codeStore.getSession(e.player.username) ?: this.plugin.codeStore.createCode(e.player)

        e.player.disconnect(
            this.plugin.messages.getMessageComponent(
                Messages.MessageKey.SHOW_CODE,
                Messages.PlaceHolderData(null, session.code)
            )
        )
    }
}