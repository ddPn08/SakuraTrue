package run.dn5.sasa.sakuratrue.velocity.ban

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.encodeToStream
import com.velocitypowered.api.proxy.Player
import kotlinx.serialization.Serializable
import run.dn5.sasa.sakuratrue.VelocityPlugin
import java.io.File

class BanDataStore {

    private val plugin = VelocityPlugin.instance
    private val banDataList = mutableListOf<BanData>()

    init {
        this.load()
        saveData()
    }

    fun ban(player: Player, reason: String, expires: Long) {
        this.banDataList.add(
            BanData(
                player.username,
                player.uniqueId.toString(),
                reason,
                expires
            )
        )
        this.saveData()
    }

    fun pardon(uuid: String) {
        this.banDataList.removeIf { it.uuid == uuid }
        this.saveData()
    }

    fun list() = this.banDataList

    fun isBanned(player: Player): Boolean {
        val banData = this.banDataList.firstOrNull { it.uuid == player.uniqueId.toString() } ?: return false
        if (banData.expires == 0L) return true
        if (banData.expires < System.currentTimeMillis()) {
            pardon(player.uniqueId.toString())
            return false
        }
        return true
    }

    fun getBanData(player: Player): BanData? {
        return this.banDataList.firstOrNull { it.uuid == player.uniqueId.toString() }
    }

    private fun saveData() {
        val file = File("${plugin.dataFolder}/bans.yml")
        Yaml.default.encodeToStream(
            BanDataList(banDataList),
            file.outputStream()
        )
    }

    private fun load() {
        val file = File("${plugin.dataFolder}/bans.yml")
        if (!file.exists()) return
        val data = Yaml.default.decodeFromStream(BanDataList.serializer(), file.inputStream())
        this.banDataList.clear()
        this.banDataList.addAll(data.bans)
    }

    @Serializable
    data class BanData(
        val username: String,
        val uuid: String,
        val reason: String,
        val expires: Long,
    )

    @Serializable
    data class BanDataList(
        val bans: List<BanData>
    )
}