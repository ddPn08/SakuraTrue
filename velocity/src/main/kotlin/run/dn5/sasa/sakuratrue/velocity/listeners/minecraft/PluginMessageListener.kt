package run.dn5.sasa.sakuratrue.velocity.listeners.minecraft

import com.google.common.io.ByteStreams
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import run.dn5.sasa.sakuratrue.Constants
import run.dn5.sasa.sakuratrue.VelocityPlugin

class PluginMessageListener(
    private val plugin: VelocityPlugin
) {
    @Subscribe
    fun onPluginMessage(e: PluginMessageEvent) {
        val input = ByteStreams.newDataInput(e.data)
        val sub = input.readUTF()
        val connection = e.source as ServerConnection

        when (sub) {
            Constants.SUB_S_TO_P.CHECK_AUTH.channel -> this.onCheckAuth(connection)
        }
    }

    private fun onCheckAuth(connection: ServerConnection) {
        val verified = this.plugin.authStore.verified(connection.player.uniqueId) ?: false
        val out = ByteStreams.newDataOutput()
        out.writeUTF(Constants.SUB_P_TO_S.CHECK_AUTH.channel)
        out.writeBoolean(verified)
        connection.player.sendPluginMessage(
            MinecraftChannelIdentifier.create(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_NAME
            ),
            out.toByteArray()
        )
    }
}