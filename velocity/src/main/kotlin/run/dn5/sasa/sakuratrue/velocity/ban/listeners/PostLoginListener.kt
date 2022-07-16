package run.dn5.sasa.sakuratrue.velocity.ban.listeners

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PostLoginEvent
import run.dn5.sasa.sakuratrue.VelocityPlugin
import run.dn5.sasa.sakuratrue.velocity.Prompt

class PostLoginListener {
    @Subscribe
    fun onPostLogin(e: PostLoginEvent) {
        val plugin = VelocityPlugin.instance
        if (!plugin.ban.dataStore.isBanned(e.player)) return
        val banData = plugin.ban.dataStore.getBanData(e.player) ?: return
        e.player.disconnect(Prompt.template(banData.reason, Prompt.Status.ERROR, ""))
    }
}