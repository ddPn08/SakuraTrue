package run.dn5.sasa.sakuratrue.velocity

import com.charleskorn.kaml.*
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import run.dn5.sasa.sakuratrue.VelocityPlugin
import java.io.File

class Messages(
) {
    private val plugin = VelocityPlugin.instance
    private fun getMessages(): YamlMap =
        Yaml.default.parseToYamlNode(File("${plugin.dataFolder}/messages.yml").inputStream()).yamlMap

    fun getMessage(key: MessageKey): String {
        return getMessages().get<YamlScalar>(key.key)?.yamlScalar?.content ?: return ""
    }

    fun getMessage(key: MessageKey, placeholder: PlaceHolderData): String {
        var str = getMessage(key)
        if (placeholder.player != null) {
            str = str.replace("%player%", placeholder.player!!.username)
                .replace("%uuid%", placeholder.player!!.uniqueId.toString())
        }
        if (placeholder.verifyCode != null) {
            str = str.replace("%verify-code%", placeholder.verifyCode!!)
        }
        return str
    }

    fun getMessageComponent(key: MessageKey): Component {
        val lines = getMessage(key).split("\n")
        val mm = MiniMessage.miniMessage()
        val result = Component.text()
        for (line in lines) {
            result.append(mm.deserialize(line + "\n"))
        }
        return result.build()
    }

    fun getMessageComponent(key: MessageKey, placeholder: PlaceHolderData): Component {
        val lines = getMessage(key, placeholder).split("\n")
        val mm = MiniMessage.miniMessage()
        val result = Component.text()
        for (line in lines) {
            result.append(mm.deserialize(line + if (line != lines.last()) "\n" else ""))
        }
        return result.build()
    }

    data class PlaceHolderData(
        var player: Player?,
        var verifyCode: String?
    )

    enum class MessageKey(val key: String) {
        DISCORD_SHOW_CODE("discord-show-code"),
        DISCORD_WRONG_CODE("discord-wrong-code"),
        DISCORD_SUCCESS_VERIFY("discord-success-verify"),
        DISCORD_RE_AUTHENTICATE_REQUESTED("discord-re-authenticate-requested"),
    }
}