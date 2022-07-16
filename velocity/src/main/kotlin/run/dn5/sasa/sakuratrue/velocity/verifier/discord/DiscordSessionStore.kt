package run.dn5.sasa.sakuratrue.velocity.verifier.discord

import com.velocitypowered.api.proxy.Player
import run.dn5.sasa.sakuratrue.VelocityPlugin
import java.util.*
import java.util.concurrent.TimeUnit

class DiscordSessionStore {
    private val plugin = VelocityPlugin.instance
    private val codeMap = mutableListOf<Session>()

    fun createCode(player: Player): Session {
        val session = Session(player.uniqueId, player.username, generateCode())
        codeMap.add(session)
        plugin.proxy.scheduler.buildTask(plugin) {
            codeMap.removeIf { it.code == session.code }
        }.delay(5L, TimeUnit.MINUTES).schedule()
        return session
    }

    fun getSession(username: String): Session? {
        return codeMap.firstOrNull { it.username == username }
    }

    private fun generateCode(): String {
        var code = ""
        var isDuplicate = true
        while (isDuplicate) {
            code = (100000..999999).random().toString()
            isDuplicate = codeMap.any { it.code == code }
        }
        return code
    }

    data class Session(
        val uuid: UUID,
        val username: String,
        val code: String,
    )
}