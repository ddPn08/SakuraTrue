package run.dn5.sasa.sakuratrue.velocity

import com.charleskorn.kaml.Yaml
import com.velocitypowered.api.proxy.Player
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.entities.Member
import run.dn5.sasa.sakuratrue.VelocityPlugin
import java.io.File
import java.util.*

class AuthStore(
    private val plugin: VelocityPlugin
) {
    private val storage = AuthStorage(mutableListOf())

    fun verified(name: String): Boolean = storage.auths.firstOrNull { it.username == name }?.verified ?: false
    fun verified(uuid: UUID): Boolean = storage.auths.firstOrNull { it.uuid == uuid.toString() }?.verified ?: false
    fun verify(uuid: UUID, username: String, member: Member) {
        if (this.verified(uuid)) return
        this.storage.auths.add(AuthData(username, uuid.toString(), true, member.id))
        this.save()
    }

    fun reauth(username: String) {
        if (!this.verified(username)) return
        this.storage.auths.removeIf { it.username == username }
        this.save()
    }

    fun checkUsername(player: Player) {
        val data = this.storage.auths.firstOrNull { it.uuid == player.uniqueId.toString() } ?: return
        if (data.username == player.username) return
        this.storage.auths.removeIf { it.uuid == player.uniqueId.toString() }
        this.storage.auths.add(AuthData(player.username, player.uniqueId.toString(), data.verified, data.discordUserId))
    }

    fun load() {
        val file = File("${this.plugin.dataFolder}/auth.yml")
        if (file.exists()) {
            val loaded = Yaml.default.decodeFromStream(AuthStorage.serializer(), file.inputStream())
            this.storage.auths.clear()
            this.storage.auths.addAll(loaded.auths)
        }
    }

    private fun save() {
        val file = File("${this.plugin.dataFolder}/auth.yml")
        Yaml.default.encodeToStream(AuthStorage.serializer(), storage, file.outputStream())
    }


    @Serializable
    data class AuthData(
        val username: String,
        val uuid: String,
        val verified: Boolean,
        val discordUserId: String
    )

    @Serializable
    data class AuthStorage(
        val auths: MutableList<AuthData>
    )
}



